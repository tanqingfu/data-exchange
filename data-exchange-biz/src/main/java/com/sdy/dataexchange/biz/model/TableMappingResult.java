package com.sdy.dataexchange.biz.model;

import lombok.Data;

/**
 * <p>
 * 所有表同步结果信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class TableMappingResult {
    private String gatherDesc;
    private String sourceDb;
    private String sourceDbName;
    private String sourceDbIp;
    private Integer sourceDbPort;
    private String sourceUser;
    private String sourceTable;
    private Integer sourceDbId;
    private Integer destDbId;
    private String destDb;
    private String destDbName;
    private String destDbIp;
    private Integer destDbPort;
    private String destUser;
    private String destTable;
    private String createTime;
    private String modifyTime;
    private String validFlag;
    private Integer id;
    private String taskName;
    private Integer jobId;
    private String jobName;

}
