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
 * @since 2019-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExOrganizationDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @TableId
    private Integer orgId;

    /**
     * 组织机构代码
     */
    private String orgCode;

    /**
     * 组织机构名称
     */
    private String orgName;

    /**
     * 机构所属等级
     */
    private String orgLevel;

    /**
     * 组织机构上级id
     */
    private Integer orgParentid;

}
