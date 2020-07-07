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
public class ExClassdataMapping extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 类别表元数据表映射ID
     */
    @TableId
    private Integer cdId;

    /**
     * 类别表Id
     */
    private Integer classId;

    /**
     * 元数据表Id
     */
    private Integer mdId;

}
