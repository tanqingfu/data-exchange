package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *交换节点信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class GatherDictResult {
    private Integer gatherId;
    private String gatherDesc;
    private  String serviceIp;
    private  String serviceDesc;
    private String gatherPath;
    private Integer sshPort;
    private String sshUser;

}
