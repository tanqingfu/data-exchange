package com.sdy.dataexchange.biz.model.BO;

import lombok.Data;

@Data
public class GatherBO {
    /**
     * 交换节点描述
     */
    private String gatherDesc;
    /**
     * 服务器ip
     */
    private String serviceIp;
    /**
     * 服务器描述
     */
    private String serviceDesc;
    private String gatherPath;
    private String sshPassword;
    private Integer sshPort;
    private String sshUser;
}
