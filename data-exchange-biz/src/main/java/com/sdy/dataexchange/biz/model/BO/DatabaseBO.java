package com.sdy.dataexchange.biz.model.BO;

import lombok.Data;

@Data
public class DatabaseBO {
    private String dbDesc;
    private String dbIp;
    private Integer dbPort;
    private String dbUser;
    private String dbPasswd;
    private String dbName;
    private String dbType;
    private Integer userInfo;
    private Integer office;
}
