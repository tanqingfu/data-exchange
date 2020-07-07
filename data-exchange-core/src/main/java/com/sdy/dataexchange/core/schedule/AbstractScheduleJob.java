package com.sdy.dataexchange.core.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.context.ApplicationContext;

/**
 * 定时任务实现
 * @author zhouziqiang 
 */
public abstract class AbstractScheduleJob implements Job {
    static ApplicationContext applicationContext = null;
    protected <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
    @Override
    public abstract void execute(JobExecutionContext jobExecutionContext);
}