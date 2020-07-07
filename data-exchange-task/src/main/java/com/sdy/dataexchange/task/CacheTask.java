package com.sdy.dataexchange.task;

import com.sdy.dataexchange.core.util.CacheUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时清理缓存
 * @author zhouziqiang 
 */
@Component
public class CacheTask {
    @Scheduled(cron = "10 10 0/1 * * ?")
    public void clearCache() {
        CacheUtil.clearAllCache();
    }
}
