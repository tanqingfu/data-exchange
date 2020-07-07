package com.sdy.dataexchange.biz.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExTableMapping extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 库源表id1
     */
    private Integer sourceSyncid;

    /**
     * 目标表id2
     */
    private Integer destSyncid;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = DATETIME_FORMAT)
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = DATETIME_FORMAT)
    private Date modifyTime;

    /**
     * 有效标志
     */
    private String validFlag;

    /**
     * 序号
     */
    @TableId
    private Integer id;

    /**
     * 转换名
     */
    private String taskName;

}
