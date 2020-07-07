package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *所有同步账号信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class UserInfo {
    private Integer gatherId;
    private String gatherDesc;
    private String syncDesc;
    private String syncdbIp;
    private String syncdbId;
    private Integer syncdbPort;
    private String syncdbUser;
    private String syncdbPasswd;
    private String syncdbName;
    private String syncdbType;
    private String createTime;
    private String modifyTime;
    private String validFlag;
}
