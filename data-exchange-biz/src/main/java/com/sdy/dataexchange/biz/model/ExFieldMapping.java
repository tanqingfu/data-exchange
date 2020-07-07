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
 * @since 2019-07-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExFieldMapping extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 同步id1
     */
    private Integer sourceSyncid;

    /**
     * 序号1
     */
    private String sourceSyncname;

    /**
     * 函数-是否单独定义
     */
    private String sourceFunc;

    /**
     * 同步id2
     */
    private Integer destSyncid;

    /**
     * 序号2
     */
    private String destSyncname;

    /**
     * 函数-是否单独定义
     */
    private String destFunc;

    /**
     * 序号
     */
    @TableId
    private Integer id;

    /**
     * 是否有效
     */
    private String validFlag;

}
