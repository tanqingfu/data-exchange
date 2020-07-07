package com.sdy.dataexchange.plugin.mysql;

import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.MapUtil;
import com.sdy.dataexchange.biz.constants.MqConstants;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.service.ExMonitorMysqlService;
import com.sdy.dataexchange.biz.service.ExParamService;
import com.sdy.dataexchange.biz.service.ExTableDictService;
import com.sdy.dataexchange.core.JobContainer;
import com.sdy.dataexchange.plugin.common.Constants;
import com.sdy.mq.base.BaseOrderedBatchMessageListener;
import com.sdy.mq.config.RocketMqConfig;
import com.sdy.mvc.annotation.TaskSync;
import com.sdy.mvc.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 启动消费者，消费并落地canal产生的数据
 * @author zhouziqiang 
 */
@Slf4j
@Component
public class MysqlSyncTask {
    @Autowired
    private ExTableDictService exTableDictService;
    @Autowired
    private RocketMqConfig rocketMqConfig;
    @Autowired
    private ExParamService exParamService;
    @Autowired
    private MysqlOriginConsumer mysqlOriginConsumer;
    @Autowired
    private ExMonitorMysqlService exMonitorMysqlService;
    private static Map<String, DefaultMQPushConsumer> syncMap = new ConcurrentHashMap<>();
    private static String LOCAL_IP = HttpUtil.getLocalIpAddress();
    @Value("${server.port}")
    private String serverPort;

    /**
     * 删除过期日志
     */
    @TaskSync
    @Scheduled(cron = "10 20 2 * * ?")
    public void clearExpiredAppendLog() {
        String exp = exParamService.getParamOrDefault("append_expire_date", Constants.APPEND_EXPIRED_DATE + "");
        Date now = new Date();
        Date expireDate = DateUtil.addDate(now, -Integer.valueOf(exp));
        long ts = expireDate.getTime() * Constants.CANAL_TIMESTAMP_UNIT;
        log.info("删除{}[ts={}]前的日志[MYSQL]", DateUtil.formatTime(expireDate), ts);
        exMonitorMysqlService.deleteExpiredAppendLog(ts);
    }
    
    @Scheduled(cron = "2/10 * * * * ?")
    public void consumerTask() throws Exception {
        // 由于消费者需要用到时间戳，为了防止多服务器时间戳不同步的问题，这里只允许单台服务器执行该定时任务，如果需要修改IP，请尽量同步新服务器时间与原有服务器一致
        if (!checkIp()) {
            if (!syncMap.isEmpty()) {
                syncMap.forEach((k, v) -> v.shutdown());
                syncMap.clear();
            }
            return;
        }
        JobContainer jobContainer = JobContainer.getInstance();
        if (jobContainer.getLock().get()) {
            log.info("Job had been locked, stop add new job to container.");
            return;
        }
        List<ExTableDict> mysqlTables = exTableDictService.listToSync();
        Map<String, ExTableDict> mysqlTableMap = MapUtil.collectionToMap(mysqlTables, tb -> tb.getDbId().toString());
        for (ExTableDict tb : mysqlTables) {
            if (!syncMap.containsKey(tb.getDbId().toString())) {
                String topic = "canal_example_" + tb.getDbId();
                BaseOrderedBatchMessageListener s = new BaseOrderedBatchMessageListener(mysqlOriginConsumer);
                DefaultMQPushConsumer consumer = rocketMqConfig.createConsumerGroup(MqConstants.Consumer.CONSUMER_GROUP_PREFIX + "MysqlSyncTask_" + topic, s, topic, 1, 1000);
                syncMap.put(tb.getDbId().toString(), consumer);
            }
        }
        Set<String> toRmSet = new HashSet<>();
        syncMap.forEach((k, v) -> {
            if (!mysqlTableMap.containsKey(k)) {
                v.shutdown();
                toRmSet.add(k);
            }
        });
        toRmSet.forEach(k -> syncMap.remove(k));
    }

    /**
     * 检查task执行IP
     */
    private boolean checkIp() {
        String ip = exParamService.getParamOrDefault("task_ip", null);
        return ip != null && ip.equals(LOCAL_IP + ":" + serverPort);
    }
    
    @PreDestroy
    public void destroy() throws InterruptedException {
        Thread.sleep(2000);
        syncMap.forEach((k, v) -> {
            v.shutdown();
        });
        JobContainer jobContainer = JobContainer.getInstance();
        jobContainer.getLock().set(true);
        int countDown = 5;
        for (int i=countDown; i>0; i--) {
            log.info("Wait {} seconds for canal consumer complete...", i);
            Thread.sleep(1000);
        }
        syncMap.forEach((k, v) -> v.shutdown());
        log.info("Destroyed canal consumer.");
    }
}
