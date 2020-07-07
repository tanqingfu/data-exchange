package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *库详情
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class DbDetails {
    private Integer dbId;
    /**
     * 库描述
     */
    private String dbDesc;

    /**
     * ip地址
     */
    private String dbIp;

    /**
     * 端口
     */
    private Integer dbPort;

    /**
     * 用户名
     */
    private String dbUser;

    /**
     * 密码
     */
    private String dbPasswd;

    /**
     * 数据库名
     */
    private String dbName;

    /**
     * 数据库类型
     */
    private String dbType;

    /**
     * 源库或者目标库
     */
    private String sourceOrDest;
    /**
     * 同步账号信息
     */
    private Integer syncdbId;
    private String syncDesc;
    private String syncdbName;
    private String syncdbIp;
    private String syncdbUser;
    private Integer syncdbPort;
    /**
     * 库所属单位
     */
    private String orgdbDesc;
    private Integer orgId;
}
