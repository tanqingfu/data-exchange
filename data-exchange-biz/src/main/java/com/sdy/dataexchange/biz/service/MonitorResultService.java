package com.sdy.dataexchange.biz.service;


import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataexchange.biz.model.DTO.TaskRowRecord;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExFieldDict;
import com.sdy.dataexchange.biz.model.ExFieldMapping;
import com.sdy.dataexchange.biz.model.ExJobTask;
import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.biz.model.ExTablePrimarykey;
import com.sdy.dataexchange.biz.model.MonitorResult;
import javafx.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author 王越洋
 * @date 2019年6月26日-下午4:59:57
 * @info
 */
public interface MonitorResultService {

    /**
     * 查询任务信息
     * @param taskId 任务id
     * @return
     */
    ExJobTask queryTask(Integer taskId);

    /**
     * 查询任务的源数据库信息
     * @param taskId 任务id
     * @return
     */
    ExDbDict queryExDbDictSrc(Integer taskId);

    /**
     * 查询任务的目标数据库信息
     * @param taskId 任务id
     * @return
     */
    ExDbDict queryExDbDictDest(Integer taskId);

    /**
     * 查询任务的源表信息
     * @param taskId 任务id
     * @return
     */
    ExTableDict queryExTableDictSrc(Integer taskId);

    /**
     * 查询任务的目标表信息
     * @param taskId 任务id
     * @return
     */
    ExTableDict queryExTableDictDest(Integer taskId);

    /**
     * 查询表信息
     * @return
     */
    ExTableDict queryExTableDict(Integer dbId, String tableName);

    /**
     * 查询表字段映射关系
     * @param sourceTabId
     * @return
     */
    List<ExFieldMapping> queryExFieldMapping(Integer sourceTabId, Integer destTableId);

    /**
     * 查询表映射关系
     * @param taskId
     * @return
     */
    ExTableMapping queryTableMapping(Integer taskId);

    List<ExMonitorMysql> pageExMonitorMysql(Integer taskId, Integer tableId, Integer row, TaskRowRecord taskRowRecord, AtomicBoolean gcFlag);
    List<ExMonitorMysql> pageFullExMonitorMysql(Integer taskId, Integer tableId, Integer row, TaskRowRecord taskRowRecord);
    
    DataAdapter getSyncDataAdapterByTaskId(Integer taskId);
    DataAdapter getSrcDbDataAdapterByTaskId(Integer taskId);

    Map<String, String> getFieldDict(Integer srcTbId);
}
