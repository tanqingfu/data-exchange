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
 * @author wyy
 * @since 2019-08-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExSyncmonUserinfo extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 交换节点id
     */
    private Integer gatherId;

    /**
     * 同步账号id
     */
    @TableId
    private Integer syncdbId;

    /**
     * 同步账号名称
     */
    private String syncDesc;

    /**
     * 同步ip地址
     */
    private String syncdbIp;

    /**
     * 同步端口
     */
    private Integer syncdbPort;

    /**
     * 同步用户名
     */
    private String syncdbUser;

    /**
     * 同步密码
     */
    private String syncdbPasswd;

    /**
     * 同步数据库名称
     */
    private String syncdbName;

    /**
     * 同步数据库类型
     */
    private String syncdbType;

    /**
     * 创建时间
     */
   // @JsonFormat(pattern = DATETIME_FORMAT)
    private String createTime;

    /**
     * 修改时间
     */
   // @JsonFormat(pattern = DATETIME_FORMAT)
    private String modifyTime;

    private Integer validFlag;

}
