package com.sdy.dataexchange.biz.model.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 作业统计信息
 * @author zhouziqiang 
 */
@Data
public class JobProcessVO {
    private Integer jobId;
    private Integer taskId;
    /**
     * 输入
     */
    private Long input;
    /**
     * 输出
     */
    private Long output;
    /**
     * 全量完成百分比
     */
    private BigDecimal percent;
    /**
     * 作业状态 1-待作业 2-正在作业 3-作业完成 4-作业失败 5-暂停作业
     */
    private Integer status;
}
