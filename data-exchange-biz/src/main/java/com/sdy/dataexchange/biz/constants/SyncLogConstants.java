package com.sdy.dataexchange.biz.constants;

/**
 * 同步日志常量
 * @author zhouziqiang 
 */
public class SyncLogConstants {
    /**
     * 日志类型
     */
    public interface LogType {
        Integer TRACE = 1;
        Integer DEBUG = 2;
        Integer INFO = 3;
        Integer WARNING = 4;
        Integer ERROR = 5;
    }
    
    public interface LogPlaceHolder {
        String JOB_INFO = "#JOB_INFO#";
        String JOB_TASK = "#JOB_TASK#";
    }
}
