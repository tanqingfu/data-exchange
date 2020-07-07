package com.sdy.dataexchange.core;

/**
 * 任务错误处理
 */
public interface JobStateChecker {
    JobState.DbState getRunningState(DataJob dataJob);
    void complete(DataJob dataJob);
    void convertToAppendeMode(String jobId);
}
