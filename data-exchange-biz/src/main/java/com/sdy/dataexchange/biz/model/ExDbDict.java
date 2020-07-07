package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.sdy.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class ExDbDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 库id
     */
    @TableId(type = IdType.INPUT)
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


    private Integer organizationId;
    /**
     * 同步账号信息
     */
    private Integer syncdbId;

}
