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
public class MqConsumerOnce {
    @Autowired
    private RedisService redisService;
    private static Map<String, ConcurrentLinkedQueue<String>> latelyConsumedMsgQueueMap = new ConcurrentHashMap<>();
    private static final int LATELY_CONSUMED_MSG_QUEUE_SIZE = 10;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public synchronized void init() {
        latelyConsumedMsgQueueMap = redisService.get(RedisConstants.REDIS_LATELY_COMSUMED_LIST, ConcurrentHashMap.class);
        if (latelyConsumedMsgQueueMap == null) {
            latelyConsumedMsgQueueMap = new ConcurrentHashMap<>(16);
        }
    }

    @PreDestroy
    public synchronized void destroy() {
        JobContainer jobContainer = JobContainer.getInstance();
        jobContainer.getLock().set(true);
        redisService.set(RedisConstants.REDIS_LATELY_COMSUMED_LIST, latelyConsumedMsgQueueMap);
    }
    
    public void consume(MessageExt message, Consumer consumer) throws Exception {
        Queue<String> latelyConsumedMsgQueue = latelyConsumedMsgQueueMap.computeIfAbsent(message.getTopic(), k -> new ConcurrentLinkedQueue<>());
        if (latelyConsumedMsgQueue.contains(message.getMsgId())) {
            log.info("消息重复消费! MsgId={}", message.getMsgId());
            return;
        }
        if (JobContainer.getInstance().getLock().get()) {
            // 程序准备退出，停止消费！！！
            Thread.sleep(3600 * 1000L);
        }
        consumer.apply(message);
        latelyConsumedMsgQueue.offer(message.getMsgId());
        if (latelyConsumedMsgQueue.size() > LATELY_CONSUMED_MSG_QUEUE_SIZE) {
            latelyConsumedMsgQueue.poll();
        }
    }

    public void consume(List<MessageExt> messageList, BatchConsumer consumer) throws Exception {
        if (messageList == null || messageList.isEmpty()) {
            return;
        }
        Queue<String> latelyConsumedMsgQueue = latelyConsumedMsgQueueMap.computeIfAbsent(messageList.get(0).getTopic(), k -> new ConcurrentLinkedQueue<>());
        if (latelyConsumedMsgQueue.contains(messageList.get(0).getMsgId())) {
            log.info("消息重复消费! MsgId={}", messageList.get(0).getMsgId());
            return;
        }
        if (JobContainer.getInstance().getLock().get()) {
            // 程序准备退出，停止消费！！！
            Thread.sleep(3600 * 1000L);
        }
        consumer.apply(messageList);
        latelyConsumedMsgQueue.offer(messageList.get(0).getMsgId());
        if (latelyConsumedMsgQueue.size() > LATELY_CONSUMED_MSG_QUEUE_SIZE) {
            latelyConsumedMsgQueue.poll();
        }
    }
    
    public interface Consumer {
        void apply(MessageExt msg) throws Exception;
    }

    public interface BatchConsumer {
        void apply(List<MessageExt> msgList) throws Exception;
    }
}
