package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *所有任务详情信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class TaskInfoDetails {
    private Integer jobId;
    private  Integer jobtaskId;
    private String jobtaskName;
    private String jobName;
    private  String taskName;
    private String sourceDb;
    private String sourceDbName;
    private String sourceDbIp;
    private Integer sourceDbPort;
    private String sourceDbUser;
    private String sourceField;
    private String sourceFieldType;
    private String destField;
    private String destFieldType;
    private String destDb;
    private String destDbName;
    private String destDbIp;
    private Integer destDbPort;
    private String destDbUser;
    private String sourceTb;
    private  String destTb;
    private  String validFlag;
    private String gatherDesc;

}
