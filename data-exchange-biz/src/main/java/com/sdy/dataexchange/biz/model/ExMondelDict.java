package com.sdy.dataexchange.biz.model;

import java.util.Date;
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
 * @since 2019-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExMondelDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 被删除的UUID
     */
    @TableId
    private String uuid;

    /**
     * 删除时间
     */
    @JsonFormat(pattern = DATETIME_FORMAT)
    private Date deltime;

    /**
     * 状态
     */
    private String state;

}
