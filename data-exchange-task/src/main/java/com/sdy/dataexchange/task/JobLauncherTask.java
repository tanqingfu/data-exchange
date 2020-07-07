package com.sdy.dataexchange.task;

import com.sdy.common.utils.DateUtil;
import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.biz.constants.MqConstants;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.constants.SyncLogConstants;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExJobInfo;
import com.sdy.dataexchange.biz.service.JobService;
import com.sdy.dataexchange.biz.service.MonitorResultService;
import com.sdy.dataexchange.core.*;
import com.sdy.dataexchange.plugin.cmdb.CommonDbResolveComsumer;
import com.sdy.dataexchange.plugin.common.Reader;
import com.sdy.dataexchange.plugin.config.PluginConfig;
import com.sdy.dataexchange.plugin.mysql.MysqlReader;
import com.sdy.dataexchange.plugin.oracle.OracleReader;
import com.sdy.dataexchange.plugin.sqlserver.SqlServerReader;
import com.sdy.mq.base.BaseOrderedBatchMessageListener;
import com.sdy.mq.config.RocketMqConfig;
import com.sdy.mvc.annotation.TaskSync;
import com.sdy.mvc.utils.HttpUtil;
import com.sdy.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

/**
 * 任务启动器
 * @author zhouziqiang 
 */
@Slf4j
@Component
public class JobLauncherTask {
    @Autowired
    private JobService jobService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MonitorResultService monitorResultService;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private CommonDbResolveComsumer commonDbResolveComsumer;
    @Autowired
    private RocketMqConfig rocketMqConfig;
    @Value("${spring.profiles.active:dev}")
    private String env;
    @Value("${server.port}")
    private String serverPort;
    private static final String JOB_STATISTIC_HOUR_FORMAT = "yyyy_MM_dd_HH";
    private static final String JOB_STATISTIC_FIELD_SEP_TOKEN = "##";
    private static String LOCAL_IP = HttpUtil.getLocalIpAddress();
    
    @PostConstruct
    public void init() {
        // 添加消费监听
        JobListener jobListener = statistic -> {
            String key = generateStatRedisKey(new Date(), statistic);
            redisService.incr(key, statistic.getCount().longValue());
            redisService.expire(key, 3600 * 3);
        };
        PluginConfig.getJobListenerList().add(jobListener);
    }
    
    /**
     * 启动未完成的任务
     */
    @Scheduled(cron = "7/10 * * * * ?")
    public void jobMonitor() {
        JobContainer jobContainer = JobContainer.getInstance();
        if (jobContainer.getLock().get()) {
            log.info("Job had been locked, stop add new job to container.");
            return;
        }
        // 增量任务
        List<ExJobInfo> jobAppendList = jobService.listJobs(2, "1", LOCAL_IP.concat(":").concat(serverPort));
        // 全量任务
        List<ExJobInfo> fullyJobList = jobService.listJobs(2, "2", LOCAL_IP.concat(":").concat(serverPort));
        // 全量完成的任务
        List<ExJobInfo> jobList = new ArrayList<>();
        jobList.addAll(jobAppendList);
        jobList.addAll(fullyJobList);
        jobList.forEach(item -> {
            item.getTaskList().forEach(task -> {
                String jobId = task.getJobtaskId().toString();
                // 加入任务容器
                if (jobContainer.getJobState(jobId).equals(JobState.State.UNINIT)) {
                    // 定义任务分片
                    Reader.AbstractJobFragment fragment;
                    ExDbDict dbDict = monitorResultService.queryExDbDictSrc(task.getJobtaskId());
                    if (DbType.ORACLE.getDb().equalsIgnoreCase(dbDict.getDbType())) {
                        fragment = new OracleReader.Task(applicationContext, task.getJobtaskId(), "2".equals(task.getValidFlag()));
                    } else if (DbType.MYSQL.getDb().equalsIgnoreCase(dbDict.getDbType())) {
                        fragment = new MysqlReader.Task(applicationContext, task.getJobtaskId(), "2".equals(task.getValidFlag()));
                    } else if (DbType.SQL_SERVER.getDb().equalsIgnoreCase(dbDict.getDbType())) {
                        fragment = new SqlServerReader.Task(applicationContext, task.getJobtaskId(), "2".equals(task.getValidFlag()));
                    } else {
                        return;
                    }
                    JobStateChecker jobStateChecker = new JobStateChecker() {
                        @Override
                        public JobState.DbState getRunningState(DataJob dataJob) {
                            ExJobInfo jobInfo = jobService.getJobInfoByTaskId(Integer.valueOf(dataJob.getJobId()));
                            if (jobInfo == null || !"1".equals(jobInfo.getValidFlag()) && !"2".equals(jobInfo.getValidFlag())) {
                                // 作业被删除或设置为无效，标记为暂停
                                return JobState.DbState.PAUSED;
                            }
                            if (jobInfo.getJobState() == null) {
                                return JobState.DbState.TORUN;
                            } else if (jobInfo.getJobState().equals(1)) {
                                return JobState.DbState.TORUN;
                            } else if (jobInfo.getJobState().equals(2)) {
                                return JobState.DbState.RUNNING;
                            } else if (jobInfo.getJobState().equals(3)) {
                                return JobState.DbState.FINISHED;
                            } else if (jobInfo.getJobState().equals(4)) {
                                return JobState.DbState.FAILED;
                            } else if (jobInfo.getJobState().equals(5)) {
                                return JobState.DbState.PAUSED;
                            }
                            return JobState.DbState.TORUN;
                        }

                        @Override
                        public void complete(DataJob dataJob) {
                            jobService.setJobComplete(Integer.valueOf(dataJob.getGroupId()));
                        }

                        @Override
                        public void convertToAppendeMode(String jobId) {
                            jobService.convertToAppendeMode(Integer.valueOf(jobId));
                        }
                    };
                    fragment.setJobStateChecker(jobStateChecker);
                    // 定义任务
                    DataJob job = new DataJob(
                            task.getJobId().toString(),
                            jobId,
                            new JobScheduler((dataJob, errorMessage) -> {
                                log.error("Job execution failed! Set work state to 4. DataJob:[{}-{}]", dataJob.getGroupId(), dataJob.getJobId());
                                jobService.setJobFailed(Integer.valueOf(dataJob.getGroupId()));
                                jobService.saveLog(
                                        Integer.valueOf(dataJob.getJobId()),
                                        SyncLogConstants.LogType.ERROR,
                                        String.format("任务[%s][%s]失败[数据抽取]，原因：%s",
                                                SyncLogConstants.LogPlaceHolder.JOB_INFO,
                                                SyncLogConstants.LogPlaceHolder.JOB_TASK,
                                                errorMessage));
                            }, -1),
                            jobContainer,
                            jobStateChecker,
                            fragment,
                            new JobConsumer() {
                                @Override
                                public void start(String jobId) {
                                    JobContainer.getInstance().getConsumerMap().computeIfAbsent(jobId, k -> {
                                        BaseOrderedBatchMessageListener s = new BaseOrderedBatchMessageListener(commonDbResolveComsumer);
                                        String topic = MqConstants.Topics.TOPIC_DATAEXCHANGE_PREFIX + jobId;
                                        DataJob job = JobContainer.getInstance().getJob(jobId);
                                        if (job.getJobFragment().bFull()) {
                                            // 全量更新，启动前清除缓存（主要是之前的mq消息）
                                            jobService.updateStartTimestamp(System.currentTimeMillis(), Integer.valueOf(jobId));
                                            redisService.set(RedisConstants.REDIS_SYNC_COUNT + jobId, 0);
                                            redisService.set(RedisConstants.REDIS_FULL_SYNC_COUNT + jobId, 0);
                                            redisService.set(RedisConstants.REDIS_FULL_SYNC_COUNT_READ + jobId, 0);
                                            redisService.set(RedisConstants.REDIS_FULL_SYNC_COUNT_TOTAL + jobId, 0);
                                            redisService.set(RedisConstants.REDIS_SYNC_START_MILLI + jobId, System.currentTimeMillis());
                                        }
                                        try {
                                            DefaultMQPushConsumer consumer = rocketMqConfig.createConsumerGroup(
                                                    MqConstants.Consumer.CONSUMER_GROUP_PREFIX + "cmdb_" + topic,
                                                    s,
                                                    topic,
                                                    1,
                                                    1000);
                                            return consumer;
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                }
                                @Override
                                public synchronized void stop(String jobId, Boolean clear) throws Exception {
                                    DataJob currentJob = JobContainer.getInstance().getJob(jobId);
                                    if (currentJob != null && currentJob.getJobState().getState().equals(JobState.State.RUNNING)) {
                                        currentJob.getJobState().setState(JobState.State.DESTROYED);
                                    }
                                    DefaultMQPushConsumer consumer = (DefaultMQPushConsumer) JobContainer.getInstance().getConsumerMap().get(jobId);
                                    if (consumer != null) {
                                        consumer.shutdown();
                                        String topic = MqConstants.Topics.TOPIC_DATAEXCHANGE_PREFIX + jobId;
                                        log.info("Consumer shutdown, topic: {}", topic);
                                    }
                                    if (clear) {
//                                        if ("dev".equals(env)) {
//                                            Thread.sleep(100L);
//                                            jobService.updateStartTimestamp(System.currentTimeMillis(), Integer.valueOf(jobId));
//                                            redisService.del(RedisConstants.REDIS_COMSUMER_LIST + jobId);
//                                        }
                                        redisService.del(RedisConstants.REDIS_LAST_SYNC_KEY + jobId);
                                    }
                                    JobContainer.getInstance().getConsumerMap().remove(jobId);
                                    jobService.saveLog(
                                            Integer.valueOf(jobId),
                                            SyncLogConstants.LogType.WARNING,
                                            String.format("任务[%s][%s]停止。",
                                                    SyncLogConstants.LogPlaceHolder.JOB_INFO,
                                                    SyncLogConstants.LogPlaceHolder.JOB_TASK));
                                }
                            });
                    try {
                        jobContainer.addJob(job);
                    } catch (Exception e) {
                        log.error("", e);
                    }
                }
            });
        });
        jobContainer.startAllJob();
    }

    @TaskSync
    @Scheduled(cron = "23/60 * * * * ?")
    public void saveJobStatistics() {
        Calendar calendar = Calendar.getInstance();
        Date nowHour = DateUtil.getDate(
                DateUtil.getDate(new Date(), "yyyy-MM-dd HH") + ":00:00",
                DateUtil.DATETIME_FORMAT);
        if (calendar.get(Calendar.SECOND) > 5) {
            String prefix = RedisConstants.REDIS_SYNC_STATISTIC;
            Set<String> keySet = redisService.keys(prefix.concat("*"));
            keySet.forEach(k -> {
                try {
                    String[] strs = k.substring(prefix.length()).split(JOB_STATISTIC_FIELD_SEP_TOKEN);
                    String hourStr = strs[0];
                    Date dt = DateUtil.getDate(hourStr, JOB_STATISTIC_HOUR_FORMAT);
                    if (dt.before(nowHour)) {
                        String groupId = strs[1];
                        String jobId = strs[2];
                        Integer cnt = redisService.get(k, Integer.class);
                        if (cnt != null) {
                            jobService.saveJobStatistics(Integer.valueOf(groupId), Integer.valueOf(jobId), hourStr, cnt);
                        }
                        redisService.del(k);
                    }
                } catch (Exception e) {
                    log.error("Job 统计失败，redis key: [{}]", k, e);
                }
            });
        }
    }

    @Scheduled(cron = "1/10 * * * * ?")
    public void printJobRate() {
//        JobContainer jobContainer = JobContainer.getInstance();
//        jobContainer.getAllJobs().forEach(job -> {
//            long r1 = SyncRateUtil.immeRate(job.getJobId(), 1);
//            log.info("Job[{}-{}], recv rate: {}/s", job.getGroupId(), job.getJobId(), r1);
//            long r2 = SyncRateUtil.immeRate(job.getJobId(), 2);
//            log.info("Job[{}-{}], handle rate: {}/s", job.getGroupId(), job.getJobId(), r2);
//        });
    }
    
    @PreDestroy
    public void destroy() throws InterruptedException {
        JobContainer jobContainer = JobContainer.getInstance();
        jobContainer.getLock().set(true);
        int countDown = 2;
        for (int i=countDown; i>0; i--) {
            log.info("Destroying job, wait {} seconds for mq sender...", i);
            Thread.sleep(1000);
        }
        jobContainer.stopAllJob();
        log.info("Mark all jobs destroyed.");
        countDown = 5;
        for (int i=countDown; i>0; i--) {
            log.info("Destroying job, wait {} seconds for rest consumer threads...", i);
            Thread.sleep(1000);
        }
        log.info("Destroyed job.");
    }
    
    private String generateStatRedisKey(Date date, JobStatistic statistic) {
        String hourStr = DateUtil.getDate(date, JOB_STATISTIC_HOUR_FORMAT);
        return RedisConstants.REDIS_SYNC_STATISTIC.concat(hourStr)
                .concat(JOB_STATISTIC_FIELD_SEP_TOKEN).concat(statistic.getGroupId())
                .concat(JOB_STATISTIC_FIELD_SEP_TOKEN).concat(statistic.getJobId());
    }
}
