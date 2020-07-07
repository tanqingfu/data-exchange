package com.sdy.dataexchange.biz.model.BO;

import lombok.Data;

@Data
public class UserInfoBO {
    private Integer gatherId;
    private String dbDesc;
    private String dbName;
    private String passwd;
    private String ip;
    private Integer port;
    private String userName;
    private String dbType;

}
