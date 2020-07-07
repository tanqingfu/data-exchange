//package com.sdy.dataexchange.mq.config;
//
//import com.sdy.dataexchange.biz.constants.MqConstants;
//import com.sdy.dataexchange.biz.model.ExTableMapping;
//import com.sdy.dataexchange.biz.service.MonitorResultService;
//import com.sdy.dataexchange.mq.consumer.DemoMessageConsumer;
//import com.sdy.mq.base.BaseMessageListener;
//import com.sdy.mq.config.RocketMqConfig;
//import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
////@Configuration
////@Component
//public class ConsumerGroupConfig {
//    @Autowired
//    private RocketMqConfig rocketMqConfig;
//    @Autowired
//    private DemoMessageConsumer demoMessageConsumer;
//    @Autowired
//    private MonitorResultService monitorResultService;
//
//    public DefaultMQPushConsumer demoMessageListener(String subTopic) throws Exception {
////        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer();
////        for (ExTableMapping e:exTableMappings) {
////        }
//        List<ExTableMapping> exTableMappings = monitorResultService.querySourceId();
//        return rocketMqConfig.createConsumer(new BaseMessageListener(demoMessageConsumer),subTopic);
////"topic_dataexchange_" + e.getSourceSyncid()
////        exTableMappings.forEach(m->{
////            try {
////                consumer = rocketMqConfig.createConsumer(new BaseMessageListener(demoMessageConsumer), "topic_dataexchange_" + m.getSourceSyncid());
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        });
//
//    }
////    public  DefaultMQPushConsumer getConsumer(String subTopic) throws IllegalAccessException, InstantiationException {
////        List<ExTableMapping> exTableMappings = monitorResultService.querySourceId();
////        demoMessageListener()
////        return null;
////    }
//
//}
