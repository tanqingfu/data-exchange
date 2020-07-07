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
 * @author wyy
 * @since 2019-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExConfigChange extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 变化的ID(各类表ID)
     */
    @TableId
    private Integer changeId;

    /**
     * 变化的表ID
     */
    private Integer changeTbname;

    /**
     * 用户名
     */
    private String modifyTime;

    /**
     * 有效标志
     */
    private String validFlag;

}
