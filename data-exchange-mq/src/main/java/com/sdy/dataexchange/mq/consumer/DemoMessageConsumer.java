//package com.sdy.dataexchange.mq.consumer;
//
//import com.sdy.dataexchange.biz.constants.MqConstants;
//import com.sdy.mq.base.BaseMessageConsumer;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.rocketmq.common.message.MessageExt;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class DemoMessageConsumer extends BaseMessageConsumer {
//
//    @Override
//    protected boolean consume(MessageExt message) {
//
//        switch (message.getTags()) {
//            case MqConstants.Tags.TAG_DEMO1:
//                break;
//            case MqConstants.Tags.TAG_DEMO2:
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
//}
