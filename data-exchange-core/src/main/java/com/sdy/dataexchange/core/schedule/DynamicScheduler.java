package com.sdy.dataexchange.core.schedule;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class DynamicScheduler {
    @Autowired
    private Scheduler scheduler;
    private static final String DEFAULT_JOB_GROUP = "JobGroupDefault";
    @Autowired
    private ApplicationContext applicationContext;
    
    @Data
    public static class JobParameter {
        public static final String JOB_PARAM = "jobParam";
        private String jobName;
        private String jobGroup;
        private String jobTrigger;
        private String status;
        private String cronExpression;
        private Boolean isSync;
        private Date updatedTime;
    }

    /**
     * 删除任务
     * @param jobName 任务ID
     */
    public synchronized void deleteTrigger(String jobName) throws SchedulerException {
        JobKey jobkey = JobKey.jobKey(jobName, DEFAULT_JOB_GROUP);
        scheduler.deleteJob(jobkey);
    }

    /**
     * 更新任务
     * @param jobName 任务ID
     * @param scheduleTaskClazz 任务线程
     * @param jobParameter 任务参数
     */
    public synchronized void updateCronTrigger(String jobName, Class<? extends AbstractScheduleJob> scheduleTaskClazz, JobParameter jobParameter) throws SchedulerException {
        JobKey jobkey = JobKey.jobKey(jobName, jobParameter.getJobGroup());
        if (scheduler.checkExists(jobkey)) {
            JobDetail jobDetail = scheduler.getJobDetail(jobkey);
            JobParameter param = (JobParameter) jobDetail.getJobDataMap().get(JobParameter.JOB_PARAM);
            if (param.getCronExpression().equals(jobParameter.getCronExpression())) {
                return;
            }
        }
        scheduler.deleteJob(jobkey);
        if (AbstractScheduleJob.applicationContext == null) {
            AbstractScheduleJob.applicationContext = applicationContext;
        }
        // 构建job信息
        JobDetail jobDetail = JobBuilder.newJob(scheduleTaskClazz).withIdentity(jobParameter.getJobName(), jobParameter.getJobGroup()).build();
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobParameter.getCronExpression());
        // 按cronExpression表达式构建trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobParameter.getJobName(), jobParameter.getJobGroup()).withSchedule(scheduleBuilder).build();
        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(JobParameter.JOB_PARAM, jobParameter);
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 更新任务
     * @param jobName 任务ID
     * @param scheduleTaskClazz 任务线程
     * @param cron cron表达式
     */
    public synchronized void updateCronTrigger(String jobName, Class<? extends AbstractScheduleJob> scheduleTaskClazz, String cron) throws SchedulerException {
        JobParameter jobParameter = new JobParameter();
        jobParameter.setJobName(jobName);
        jobParameter.setJobGroup(DEFAULT_JOB_GROUP);
        jobParameter.setCronExpression(cron);
        jobParameter.setUpdatedTime(new Date());
        jobParameter.setIsSync(false);
        updateCronTrigger(jobName, scheduleTaskClazz, jobParameter);
    }
}

