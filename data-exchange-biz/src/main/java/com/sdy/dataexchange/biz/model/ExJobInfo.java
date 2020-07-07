package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
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
public class ExJobInfo extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 作业ID
     */
    @TableId
    private Integer jobId;

    /**
     * 作业名
     */
    private String jobName;

    /**
     * 作业描述
     */
    private String jobDesc;

    /**
     * 有效标志 1-有效
     */
    private String validFlag;

    /**
     * 作业状态 1-待作业 2-正在作业 3-作业完成 4-作业失败 5-暂停作业
     */
    private Integer jobState;

    /**
     * 任务列表
     */
    @TableField(exist = false)
    private List<ExJobTask> taskList;

    /**
     * 任务时间轴
     */
    @TableField(exist = false)
    private List<String> taskHourList;
    /**
     * ip地址
     */
    private String ip;
    /**
     * 增量作业开始时间
     */
    private Date dealTime;

}
