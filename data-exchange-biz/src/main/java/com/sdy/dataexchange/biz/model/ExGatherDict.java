package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExGatherDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 交换节点id
     */
    @TableId
    private Integer gatherId;

    /**
     * 交换节点名称
     */
    private String gatherDesc;

    private String serviceIp;
    private String serviceDesc;

    /**
     * 采集程序路径
     */
    private String gatherPath;
    private String sshPassword;
    private Integer sshPort;
    private String sshUser;
}
