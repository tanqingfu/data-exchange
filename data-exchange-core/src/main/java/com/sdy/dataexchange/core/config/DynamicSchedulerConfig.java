package com.sdy.dataexchange.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz配置
 * @author zhouziqiang 
 */
@Configuration
public class DynamicSchedulerConfig {
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        return new SchedulerFactoryBean();
    }
}
