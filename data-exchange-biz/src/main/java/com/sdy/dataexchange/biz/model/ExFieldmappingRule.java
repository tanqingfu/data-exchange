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
public class ExFieldmappingRule extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 源数据库类型
     */
    private String sourceDbtype;

    /**
     * 目标数据库类型
     */
    private String destDbtype;

    /**
     * 字段源类型
     */
    private String sourceFieldtype;

    /**
     * 字段目标类型
     */
    private String destFieldtype;

    /**
     * 规则
     */
    private String rule;

    @TableId
    private Integer id;

}
