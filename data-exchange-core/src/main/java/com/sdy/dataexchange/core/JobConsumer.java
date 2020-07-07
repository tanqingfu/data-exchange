package com.sdy.dataexchange.core;

/**
 * 消费者
 */
public interface JobConsumer {
    void start(String jobId);
    void stop(String jobId, Boolean clear) throws Exception;
}
