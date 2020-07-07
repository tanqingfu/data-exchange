package com.sdy.dataexchange.core;

import lombok.Data;

@Data
public class JobState {
    private State state;
    
    public enum State {
        UNINIT,
        READY,
        WAITING,
        RUNNING,
        FINISHED,
        DESTROYED,
        FAILED
    }

    /**
     * 作业状态 1-待作业 2-正在作业 3-作业完成 4-作业失败 5-暂停作业
     */
    public enum DbState {
        TORUN,
        RUNNING,
        FINISHED,
        FAILED,
        PAUSED
    }
}
