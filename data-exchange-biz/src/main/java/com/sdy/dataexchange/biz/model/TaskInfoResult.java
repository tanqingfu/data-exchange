package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *所有任务，同步数据信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class TaskInfoResult {
    private  String taskName;
    private String sourceDbid;
    private String sourceDb;
    private String sourceDbName;
    private String sourceDbIp;
    private Integer sourceDbPort;
    private String sourceTable;
    private String sourceUser;
    private String destDbid;
    private String destDb;
    private String destDbName;
    private String destDbIp;
    private Integer destDbPort;
    private String destTable;
    private String destUser;
    private String swaData;
    private String swaGross;

}
