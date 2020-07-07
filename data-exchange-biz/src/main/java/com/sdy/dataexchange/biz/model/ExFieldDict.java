package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableId;
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
public class ExFieldDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 同步表id
     */
    private Integer syncId;

    /**
     * 序号
     */
    @TableId
    private Integer syncSeqno;

    /**
     * 字段
     */
    private String syncField;

    /**
     * 字段类型
     */
    private String syncType;

}
