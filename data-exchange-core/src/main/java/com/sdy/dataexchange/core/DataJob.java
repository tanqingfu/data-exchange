package com.sdy.dataexchange.core;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务
 * @author zhouziqiang 
 */
@Slf4j
@Data
public class DataJob {
    private String jobId;
    private String groupId;
    private JobScheduler scheduler;
    private JobContainer jobContainer;
    private JobState jobState = new JobState();
    private JobFragment jobFragment;
    private JobStateChecker jobStateChecker;
    private JobConsumer jobConsumer;
    private Integer syncCount;
    public final static String CACHE_BUCKET_DATASYNC = "cache_bucket_datasync";
    
    public DataJob(String groupId,
                   String jobId,
                   JobScheduler scheduler,
                   JobContainer jobContainer,
                   JobStateChecker jobStateChecker,
                   JobFragment jobFragment,
                   JobConsumer jobConsumer) {
        this.groupId = groupId;
        this.jobId = jobId;
        this.scheduler = scheduler;
        this.jobState.setState(JobState.State.READY);
        this.jobContainer = jobContainer;
        this.jobStateChecker = jobStateChecker;
        this.jobFragment = jobFragment;
        this.jobConsumer = jobConsumer;
    }
    
//    synchronized void pause() {
//        // TODO 数据库设置暂停标识
//        jobContainer.removeJob(jobId);
//    }
//
//    synchronized void resume() {
//    }
    
    void start() {
        if (this.jobState.getState().equals(JobState.State.READY)) {
            log.info("Submit job[{}], waiting thread pool schedule...", jobId);
            this.jobState.setState(JobState.State.WAITING);
            this.jobConsumer.start(this.jobId);
            JobProcessingThread jobProcessingThread = new JobProcessingThread(this);
            jobContainer.getDataJobThreadPool().submit(jobProcessingThread);
        }
    }
    
    static class JobProcessingThread extends Thread {
        private DataJob dataJob;
        JobProcessingThread(DataJob dataJob) {
            this.dataJob = dataJob;
        }
        @Override
        public void run() {
            try {
                log.info("Init job[{}-{}]", dataJob.getGroupId(), dataJob.getJobId());
                dataJob.getScheduler().init(dataJob);
                log.info("Start job[{}-{}]", dataJob.getGroupId(), dataJob.getJobId());
                dataJob.getJobState().setState(JobState.State.RUNNING);
                boolean taskState = dataJob.getScheduler().schedule(dataJob);
                log.info("Destroy job[{}-{}]", dataJob.getGroupId(), dataJob.getJobId());
                dataJob.getScheduler().destroy();
                dataJob.completeJob(taskState);
            } catch (Exception e) {
                try {
                    dataJob.getJobFragment().destroy(false);
                    dataJob.getJobConsumer().stop(dataJob.getJobId(), true);
                } catch (Exception e2) {
                    log.error("", e2);
                }
                dataJob.completeJob(false);
                log.error("Job[{}-{}] exception:", dataJob.getGroupId(), dataJob.getJobId(), e);
                // TODO 任务异常处理
            }
        }
    }
    
    private void completeJob(boolean taskState) {
//        jobState.setState(JobState.State.FINISHED);
        if (taskState) {
//            jobStateChecker.complete(this);
            log.info("Task completed! [{}-{}]", groupId, jobId);
        }
        jobContainer.removeJob(jobId);
    }

    /**
     * 停止任务
     * @param stopCanal 是否停止canal
     * @param clearAppendProgress 是否删除增量同步进度
     */
    void stopJob(boolean stopCanal, boolean clearAppendProgress) throws Exception {
        this.getJobFragment().destroy(stopCanal);
        this.getJobConsumer().stop(this.getJobId(), clearAppendProgress);
    }
}
