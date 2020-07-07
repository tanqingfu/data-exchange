package com.sdy.dataexchange.biz.model;

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
 * @since 2019-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExOrgdbMapping extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    private Integer orgId;

    /**
     * 库id
     */
    @TableId
    private Integer dbId;

    /**
     * 机构与库表描述信息
     */
    private String orgdbDesc;
    /**
     * 主键
     */
    private  Integer id;

}
