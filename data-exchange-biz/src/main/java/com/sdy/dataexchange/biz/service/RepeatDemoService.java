package com.sdy.dataexchange.biz.service;

import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExFieldDict;
import com.sdy.dataexchange.biz.model.ExFieldMapping;

import java.util.List;
import java.util.Map;

public interface RepeatDemoService {
    /**
     * 获取所属地完整信息
     *
     * @param allInfo
     * @return
     */
    String getFullName(List<String> allInfo);

    /**
     * 获取mysql数据库的所有字段信息
     *
     * @param tables
     * @param exDbDict
     */
    void mysqlField(List<Map<String,Object>> tables, ExDbDict exDbDict);

    /**
     * 获取oracle数据库的字段信息
     *
     * @param tables
     * @param exDbDict
     */
    void oracleField(List<Map<String,Object>> tables, ExDbDict exDbDict);

    /**
     * 获取字段
     * @param fields
     * @param tableName
     */
    void setFields(List<Map<String, Object>> fields, Map<String, Object> tableName);
    /**
     * 获取原表名
     * @param tables
     * @param tableNames
     * @param dataAdapter
     */
    void getSourceNames(List tables, List<Map<String, Object>> tableNames, DataAdapter dataAdapter);

    /**
     * 保存任务
     *
     * @param tableInfos
     * @param fieldResult
     * @param sourceSyncId
     * @param destSyncId
     * @param leftFields
     * @param rightFields
     * @param exFieldMappings
     * @param sourceSyncField
     * @param destSyncField
     * @return
     */
    Integer saveTasks(List<Map<String, String>> tableInfos, Integer fieldResult, Integer sourceSyncId, Integer destSyncId
            , List<ExFieldDict> leftFields, List<ExFieldDict> rightFields, List<ExFieldMapping> exFieldMappings
            , List<String> sourceSyncField, List<String> destSyncField);

}
