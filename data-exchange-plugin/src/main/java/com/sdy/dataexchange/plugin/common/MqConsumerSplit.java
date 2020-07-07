package com.sdy.dataexchange.plugin.common;

import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.core.JobContainer;
import com.sdy.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 保证消费一次，不涉及消息重试
 * @author zhouziqiang 
 */
@Slf4j
@Component
public class MqConsumerSplit {
    @Autowired
    private RedisService redisService;
    private static Map<String, ConcurrentLinkedQueue<MessageExt>> splitConsumedMsgQueueMap = new ConcurrentHashMap<>();
    private static final int SPLIT_CONSUMED_MSG_QUEUE_SIZE = 1000;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public synchronized void init() {
        splitConsumedMsgQueueMap = redisService.get(RedisConstants.REDIS_SPLIT_COMSUMED_LIST, ConcurrentHashMap.class);
        if (splitConsumedMsgQueueMap == null) {
            splitConsumedMsgQueueMap = new ConcurrentHashMap<>(16);
        }
    }

    @PreDestroy
    public synchronized void destroy() {
        JobContainer jobContainer = JobContainer.getInstance();
        jobContainer.getLock().set(true);
        redisService.set(RedisConstants.REDIS_SPLIT_COMSUMED_LIST, splitConsumedMsgQueueMap);
    }
    
    public void consume(MessageExt message, Consumer consumer) throws Exception {
        Queue<MessageExt> splitConsumedMsgQueue = splitConsumedMsgQueueMap.computeIfAbsent(message.getTopic(), k -> new ConcurrentLinkedQueue<>());
        if (JobContainer.getInstance().getLock().get()) {
            // 程序准备退出，停止消费！！！
            Thread.sleep(3600 * 1000L);
        }
        String[] tags = message.getTags().split("###");
        if (tags.length >= 2 && "1".equals(tags[tags.length - 1])) {
            boolean add = splitConsumedMsgQueue.offer(message);
            if (!add) {
                throw new Exception("MqConsumerSplit 队列已满 - 1.1, " + message.toString());
            }
        } else {
            MessageExt msg = null;
            while (!splitConsumedMsgQueue.isEmpty()) {
                msg = mergeMessage(msg, splitConsumedMsgQueue.poll());
            }
            msg = mergeMessage(msg, message);
            consumer.apply(msg);
        }
        if (splitConsumedMsgQueue.size() > SPLIT_CONSUMED_MSG_QUEUE_SIZE) {
            throw new Exception("MqConsumerSplit 队列已满 - 1.2, " + message.toString());
        }
    }

    public void consume(List<MessageExt> messageList, BatchConsumer consumer) throws Exception {
        if (messageList == null || messageList.isEmpty()) {
            return;
        }
        Queue<MessageExt> splitConsumedMsgQueue = splitConsumedMsgQueueMap.computeIfAbsent(messageList.get(0).getTopic(), k -> new ConcurrentLinkedQueue<>());
        if (JobContainer.getInstance().getLock().get()) {
            // 程序准备退出，停止消费！！！
            Thread.sleep(3600 * 1000L);
        }
        List<MessageExt> messages = new ArrayList<>();
        for (MessageExt message : messageList) {
            String[] tags = message.getTags().split("###");
            if (tags.length >= 2 && "1".equals(tags[tags.length - 1])) {
                boolean add = splitConsumedMsgQueue.offer(message);
                if (!add) {
                    throw new Exception("MqConsumerSplit - 2.1 队列已满, " + message.toString());
                }
            } else {
                MessageExt msg = null;
                while (!splitConsumedMsgQueue.isEmpty()) {
                    msg = mergeMessage(msg, splitConsumedMsgQueue.poll());
                }
                msg = mergeMessage(msg, message);
                messages.add(msg);
            }
        }
        if (!messages.isEmpty()) {
            consumer.apply(messages);
        }
        if (splitConsumedMsgQueue.size() > SPLIT_CONSUMED_MSG_QUEUE_SIZE) {
            throw new Exception("MqConsumerSplit - 2.2 队列已满, " + messageList.get(0).toString());
        }
    }
    
    private MessageExt mergeMessage(MessageExt message1, MessageExt message2) {
        if (message1 == null) {
            return message2;
        }
        byte[] msgBody = new byte[message1.getBody().length + message2.getBody().length];
        System.arraycopy(message1.getBody(), 0, msgBody, 0, message1.getBody().length);
        System.arraycopy(message2.getBody(), 0, msgBody, message1.getBody().length, message2.getBody().length);
        message1.setBody(msgBody);
        return message1;
    }
    
    public interface Consumer {
        void apply(MessageExt msg) throws Exception;
    }

    public interface BatchConsumer {
        void apply(List<MessageExt> msgList) throws Exception;
    }
}
