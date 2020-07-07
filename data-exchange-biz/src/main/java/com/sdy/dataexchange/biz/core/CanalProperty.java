package com.sdy.dataexchange.biz.core;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CanalProperty {
    private Integer gatherId;
    private String sshIp;
    private Integer sshPort;
    private String sshUser;
    private String sshPassword;
    private String canalPath;
    private String destination;
    private String syncDbAddr;
    private String syncUser;
    private String syncPwd;

    public CanalProperty(Integer gatherId, String sshIp, String sshUser, String sshPassword, Integer sshPort, String canalPath, String destination, String syncDbAddr, String syncUser, String syncPwd) {
        this.gatherId = gatherId;
        this.sshIp = sshIp;
        this.sshUser = sshUser;
        this.sshPassword = sshPassword;
        this.sshPort = sshPort;
        this.canalPath = canalPath;
        this.destination = destination;
        this.syncDbAddr = syncDbAddr;
        this.syncUser = syncUser;
        this.syncPwd = syncPwd;
    }

    @Override
    public String toString() {
        return "CanalProperty{" +
                "gatherId='" + gatherId + '\'' +
                ", sshIp='" + sshIp + '\'' +
                ", sshPort='" + sshPort + '\'' +
                ", sshUser='" + sshUser + '\'' +
                ", canalPath='" + canalPath + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }
}
