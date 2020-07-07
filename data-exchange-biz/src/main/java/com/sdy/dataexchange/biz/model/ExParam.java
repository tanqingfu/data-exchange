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
 * @author wyy
 * @since 2019-10-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExParam extends BaseModel {
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;

    private String paramKey;

    private String paramValue;

}
