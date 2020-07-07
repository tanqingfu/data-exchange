package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyy
 * @since 2019-09-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExJobTask extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 任务表ID
     */
    @TableId
    private Integer jobtaskId;

    /**
     * 任务表名
     */
    private String jobtaskName;

    /**
     * 作业ID
     */
    private Integer jobId;

    /**
     * 表与表映射的ID
     */
    private Integer tbmapId;

    /**
     * 库映射的ID
     */
    private Integer dbmapId;

    /**
     * 同步源表
     */
    private Integer syncId;

    /**
     * 有效标志
     */
    private String validFlag;

    /**
     * 任务统计信息
     */
    @TableField(exist = false)
    private List<ExSwapdataDict> taskStats;

}
