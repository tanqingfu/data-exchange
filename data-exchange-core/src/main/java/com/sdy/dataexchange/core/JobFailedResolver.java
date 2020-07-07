package com.sdy.dataexchange.core;

/**
 * 任务错误处理
 */
@FunctionalInterface
public interface JobFailedResolver {
    void resolve(DataJob dataJob, String errorMessage);
}
