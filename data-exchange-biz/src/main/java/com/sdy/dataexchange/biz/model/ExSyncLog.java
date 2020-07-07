package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 同步日志
 * </p>
 *
 * @author zhouziqiang
 * @since 2019-10-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExSyncLog extends BaseModel {
    private static final long serialVersionUID = 1L;

    @TableId
    private Integer id;

    private Integer jobId;

    private Integer taskId;

    private Integer type;

    private String msg;
    
    private Date createTime;

}
