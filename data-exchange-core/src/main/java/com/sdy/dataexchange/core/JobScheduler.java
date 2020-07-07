package com.sdy.dataexchange.core;

import com.sdy.common.utils.RandomUtil;
import com.sdy.dataexchange.core.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 单任务调度器
 */
@Slf4j
public class JobScheduler {
//    private ScheduledExecutorService singleJobThreadPool = Executors.newScheduledThreadPool(0);
    private Integer fragmentSecondDelay;
    private JobFailedResolver jobFailedResolver;

    public JobScheduler() {
        this.fragmentSecondDelay = JobConstant.JOB_FRAGMENT_SECOND_DELAY;
    }
    
    public JobScheduler(JobFailedResolver jobFailedResolver, int fragmentSecondDelay) {
        this.jobFailedResolver = jobFailedResolver;
        this.fragmentSecondDelay = fragmentSecondDelay;
    }
    
    void init(DataJob dataJob) throws Exception {
        try {
            CacheUtil.clearCache(DataJob.CACHE_BUCKET_DATASYNC + dataJob.getJobId());
            dataJob.getJobFragment().init();
        } catch (Exception e) {
            log.error("Job[{}-{}] init failed!", dataJob.getGroupId(), dataJob.getJobId(), e);
            jobFailedResolver.resolve(dataJob, e.toString());
            throw e;
        }
    }

    /**
     * 任务调度
     * @param dataJob
     * @return if finished
     * @throws Exception
     */
    boolean schedule(DataJob dataJob) throws Exception {
        log.info("Start scheduling job[{}-{}]...", dataJob.getGroupId(), dataJob.getJobId());
        boolean result = false;
        int delay = this.fragmentSecondDelay.equals(-1)
                ? dataJob.getJobContainer().jobCount() * JobConstant.JOB_FRAGMENT_BASE_SECOND_DELAY
                : this.fragmentSecondDelay;
        Thread.sleep(RandomUtil.randomNumber(0, delay * 1000));
        while (true) {
            try {
                // 任务强行终止
                if (dataJob.getJobState().getState().equals(JobState.State.DESTROYED)) {
                    dataJob.stopJob(false, false);
                    return false;
                }
                JobState.DbState dbState = dataJob.getJobStateChecker().getRunningState(dataJob);
                if (dbState.equals(JobState.DbState.FINISHED)) {
                    // 数据库任务完成
                    dataJob.stopJob(false, false);
                    return true;
                } else if (dbState.equals(JobState.DbState.TORUN)) {
                    // 数据库任务结束/未开始
                    dataJob.stopJob(true, true);
                    return false;
                } else if (dbState.equals(JobState.DbState.FAILED)) {
                    // 数据库任务失败
                    dataJob.stopJob(false, true);
                    return false;
                } else {
                    // 禁止执行任务
                    if (dataJob.getJobContainer().getLock().get()) {
                        result = false;
                    } else if (dbState.equals(JobState.DbState.RUNNING)) {
                        log.debug("Run job[{}-{}]...", dataJob.getGroupId(), dataJob.getJobId());
                        dataJob.getJobConsumer().start(dataJob.getJobId());
                        result = dataJob.getJobFragment().run();
                    } else { // PAUSED
                        dataJob.stopJob(false, false);
                        return false;
                    }
                    if (result) {
                        break;
                    }
                    delay = this.fragmentSecondDelay.equals(-1)
                            ? (int) Math.log1p(dataJob.getJobContainer().jobCount() * JobConstant.JOB_FRAGMENT_BASE_SECOND_DELAY * 6.0)
                            : this.fragmentSecondDelay;
                    Thread.sleep(delay * 1000L);
                }
            } catch (Exception e) {
                log.error("Job[{}-{}] failed!", dataJob.getGroupId(), dataJob.getJobId(), e);
                dataJob.stopJob(false, true);
                jobFailedResolver.resolve(dataJob, e.toString());
                break;
            }
        }
        return result;
    }
    
    void destroy() {
    }
}
