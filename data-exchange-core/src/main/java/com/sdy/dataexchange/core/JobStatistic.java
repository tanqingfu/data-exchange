package com.sdy.dataexchange.core;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 任务统计
 * @author zhouziqiang 
 */
@Data
@Accessors(chain = true)
public class JobStatistic {
    private String groupId;
    private String jobId;
    private Integer count;
}
