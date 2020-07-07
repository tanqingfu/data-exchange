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
public class ExOrganizationLevel extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 机构id
     */
    @TableId
    private String orgLevel;

    /**
     * 机构层级信息
     */
    private String orgDesc;

}
