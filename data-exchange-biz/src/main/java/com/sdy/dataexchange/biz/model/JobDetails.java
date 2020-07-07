package com.sdy.dataexchange.biz.model;

import lombok.Data;

import java.util.List;

/**
 * <p>
 * 作业详情
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class JobDetails {
    private Integer jobId;
    private Integer taskId;

    private String jobName;

    private String jobDesc;

    private String validFlag;
    private String ip;

    private String jobState;
    private String dealTime;
    private String taskName;
    private String sourceDbDesc;
    private String sourceDb;
    private String sourceTb;
    private String destDbDesc;
    private String destDb;
    private String destTb;
    private Integer jobStatus;
    private List jobLog;
    private Long syncReadCurrent;
    private Long syncReadTotal;
    private Long syncWriteCurrent;
    private Long syncWriteTotal;
}
