package com.sdy.dataexchange.core;

public class JobConstant {
    public static final int JOB_CORE_THREAD_NUM = 0;
    public static final int JOB_MAX_THREAD_NUM = 100;

    public static final int SINGLE_JOB_CORE_THREAD_NUM = 0;
    public static final int SINGLE_JOB_MAX_THREAD_NUM = 1;
    /**
     * 默认任务分片执行间隔：-1=auto
     */
    public static final int JOB_FRAGMENT_SECOND_DELAY = -1;
    /**
     * 两个任务分片执行间隔（秒）
     */
    public static final int JOB_FRAGMENT_BASE_SECOND_DELAY = 3;
}
