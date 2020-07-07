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
 * @since 2019-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExSyncdbCtl extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 采集id
     */
    private Integer gatherId;

    /**
     * 库源id
     */
    private Integer sourceDbid;

    /**
     * 同步序号
     */
    @TableId
    private Integer syncSeqno;

    /**
     * 同步时间
     */
    private String syncTime;

}
