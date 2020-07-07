package com.sdy.dataexchange.biz.model;

import lombok.Data;

@Data
public class ExDbDictUser {
    private String dbId;

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

    private String syncUser;

    public ExDbDictUser() {
    }

    @Override
    public String toString() {
        return "exDbDictUser{" +
                "dbId='" + dbId + '\'' +
                ", dbDesc='" + dbDesc + '\'' +
                ", dbIp='" + dbIp + '\'' +
                ", dbPort=" + dbPort +
                ", dbUser='" + dbUser + '\'' +
                ", dbPasswd='" + dbPasswd + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbType='" + dbType + '\'' +
                ", sourceOrDest='" + sourceOrDest + '\'' +
                ", syncUser=" + syncUser +
                '}';
    }
}
