package com.sdy.dataexchange.biz.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.sdy.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2019-07-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExTableDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 同步表id
     */
    @TableId(type = IdType.AUTO)
    private Integer syncId;

    /**
     * 库id
     */
    private Integer dbId;

    /**
     * 表名
     */
    private String dbTable;

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

}
