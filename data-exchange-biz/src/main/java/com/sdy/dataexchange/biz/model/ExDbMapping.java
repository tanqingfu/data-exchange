package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdy.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

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
public class ExDbMapping extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 采集id
     */
    private Integer gatherId;

    /**
     * 库源id
     */
    private Integer sourceDbid;

    /**
     * 目标库id
     */
    private Integer destDbid;

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

}
