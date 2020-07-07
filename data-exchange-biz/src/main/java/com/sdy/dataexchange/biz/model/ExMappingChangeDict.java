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
 * @since 2019-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExMappingChangeDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 库id
     */
    private Integer dbId;

    /**
     * 表id
     */
    private Integer tableId;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = DATETIME_FORMAT)
    private Date modifyTime;

    /**
     * 有效标志
     */
    private String flag;

    /**
     * 主键
     */
    @TableId
    private Integer id;

}
