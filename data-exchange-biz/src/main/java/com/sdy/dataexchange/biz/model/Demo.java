package com.sdy.dataexchange.biz.model;

import com.sdy.common.model.BaseModel;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * @since 2019-06-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class Demo extends BaseModel {
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer rid;

    private String name;

    @JsonFormat(pattern = DATETIME_FORMAT)
    private Date createTime;

}
