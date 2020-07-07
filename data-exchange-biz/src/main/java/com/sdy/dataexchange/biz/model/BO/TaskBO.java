package com.sdy.dataexchange.biz.model.BO;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TaskBO {
    /**
     * 任务名
     */
    String taskName;
    /**
     * 源库id
     */
    String sourceDbId;
    /**
     * 目标库id
     */
    String destDbId;
    /**
     * 源表名
     */
    String sourceTableName;
    /**
     * 目标表名
     */
    String destTableName;
    /**
     * 同步字段信息
     */
    List<Map<String, String>> tableInfos;

}
