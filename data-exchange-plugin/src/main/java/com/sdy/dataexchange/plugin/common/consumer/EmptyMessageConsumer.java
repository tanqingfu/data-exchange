package com.sdy.dataexchange.plugin.common.consumer;

import com.sdy.mq.base.BaseMessageConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.stereotype.Component;

/**
 * 用来处理无用的消息，一般用来恢复错误任务
 * @author zhouziqiang 
 */
@Slf4j
@Component
public class EmptyMessageConsumer extends BaseMessageConsumer {

    @Override
    protected boolean consume(MessageExt message) {
        return true;
    }
}
