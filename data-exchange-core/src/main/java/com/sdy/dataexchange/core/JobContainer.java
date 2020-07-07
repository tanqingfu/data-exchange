package com.sdy.dataexchange.core;

import com.sdy.common.utils.Assert;
import com.sdy.common.utils.StringUtil;
import javafx.util.Pair;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

@Slf4j
public class JobContainer {
    private Map<String, DataJob> jobMap = new ConcurrentHashMap<>();
    private static JobContainer jobContainer = new JobContainer();
    @Getter
    private AtomicBoolean lock = new AtomicBoolean(false);
    private Map<String, Boolean> tempJobFailedStatusMap = new ConcurrentHashMap<>();
    /**
     * DefaultMQPushConsumer Map
     */
    @Getter
    private Map<String, Object> consumerMap = new ConcurrentHashMap<>();
    /**
     * Job线程池
     */
    @Getter
    public ExecutorService dataJobThreadPool = new ThreadPoolExecutor(
            JobConstant.JOB_CORE_THREAD_NUM,
            JobConstant.JOB_MAX_THREAD_NUM,
            10,
            TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    public Pair<Long, Long> getFullSyncReadProcess(String jobId, Function<String, Pair<Long, Long>> function) {
        DataJob job = getJob(jobId);
        if (job != null) {
            if (job.getJobFragment().bFull()) {
                return job.getJobFragment().getSyncProcess();
            } else {
                return function.apply(jobId);
            }
        } else {
            // 全量同步结束
            return function.apply(jobId);
        }
    }
    
    public Pair<Long, Long> getFullSyncWriteProcess(String jobId, Function<String, Pair<Long, Long>> function) {
        return function.apply(jobId);
    }
    
    public static JobContainer getInstance() {
        return jobContainer;
    }

    /**
     * 任务开始前调用，返回是否可以启动
     */
    public boolean postJobStart(String groupId, List<String> jobIdList, String syncType) {
        if (jobIdList.stream().anyMatch(this::isJobActive)) {
            return false;
        }
        jobIdList.forEach(jobId -> tempJobFailedStatusMap.remove(jobId));
        return true;
    }
    
    public void setJobStateFailed(String jobId) {
        DataJob job = getJob(jobId);
        if (job != null) {
            job.getJobState().setState(JobState.State.FAILED);
        } else {
            tempJobFailedStatusMap.put(jobId, true);
        }
    }

    public boolean checkJobFailed(String jobId) {
        DataJob job = getJob(jobId);
        if (job != null) {
            return job.getJobState().getState().equals(JobState.State.FAILED);
        } else {
            return tempJobFailedStatusMap.getOrDefault(jobId, false);
        }
    }

    /**
     * 判断任务是否还有残留
     */
    public boolean isJobActive(String jobId) {
        if (getJob(jobId) != null) {
            return true;
        }
        if (consumerMap.containsKey(jobId)) {
            return true;
        }
        return false;
    }

    /**
     * 获取所有任务
     * @return
     */
    public Collection<DataJob> getAllJobs() {
        return jobMap.values();
    }

    /**
     * 获取任务数量
     * @return
     */
    public int jobCount() {
        return jobMap.size();
    }

    /**
     * 获取任务状态
     * @param jobId
     * @return
     */
    public JobState.State getJobState(String jobId) {
        if (StringUtil.isBlank(jobId)) {
            return JobState.State.UNINIT;
        }
        DataJob job = jobMap.get(jobId);
        if (job == null) {
            return JobState.State.UNINIT;
        }
        return job.getJobState().getState();
    }

    /**
     * 添加任务
     * @param job
     * @throws Exception
     */
    public void addJob(DataJob job) throws Exception {
        Assert.isBlank(job.getJobId(), "Job id cannot be null or empty!");
        if (jobMap.containsKey(job.getJobId())) {
            log.warn("Job[{}] is already in container!", job.getJobId());
            return;
        }
        log.info("Add job[{}] to container.", job.getJobId());
        jobMap.putIfAbsent(job.getJobId(), job);
    }

    /**
     * 移除任务
     * @param jobId
     */
    public void removeJob(String jobId) {
        DataJob job = jobMap.get(jobId);
        jobMap.remove(jobId);
        log.info("Remove job[{}-{}]", job == null ? "?" : job.getGroupId(), jobId);
    }

    /**
     * 启动所有未启动的任务
     */
    public synchronized void startAllJob() {
        if (!lock.get()) {
            jobMap.forEach((jobId, job) -> {
                if (job.getJobState().getState().equals(JobState.State.READY)) {
                    job.start();
                }
            });
        }
    }

    /**
     * 获取任务信息
     * @param jobId
     * @return
     */
    public DataJob getJob(String jobId) {
        return jobMap.get(jobId);
    }

    /**
     * 停止所有任务(不持久化到数据库)
     */
    public void stopAllJob() {
        jobMap.forEach((k, v) -> {
            v.getJobState().setState(JobState.State.DESTROYED);
        });
    }

//    /**
//     * 暂停任务执行
//     * @param jobId
//     */
//    public void pauseJob(String jobId) {
//        DataJob job = jobMap.get(jobId);
//        if (job != null) {
//            job.pause();
//        }
//    }
//
//    /**
//     * 恢复任务执行
//     * @param jobId
//     */
//    public void resumeJob(String jobId) {
//        DataJob job = jobMap.get(jobId);
//        if (job != null) {
//            job.resume();
//        }
//    }
}
