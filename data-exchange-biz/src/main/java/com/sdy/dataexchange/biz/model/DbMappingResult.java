package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *库同步信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class DbMappingResult {
    private  Integer id;
    private  String gatherDesc;
    private Integer gatherId;
    private  String sourceDb;
    private  String sourceDbName;
    private String sourceDbIp;
    private Integer sourceDbPort;
    private Integer sourceDbId;
    private Integer destDbId;
    private String sourceDbUser;
    private String destDbName;
    private  String destDb;
    private String destDbIp;
    private Integer destDbPort;
    private String destDbUser;
    private  String createTime;
    private  String modifyTime;
    private String validFlag;
}
