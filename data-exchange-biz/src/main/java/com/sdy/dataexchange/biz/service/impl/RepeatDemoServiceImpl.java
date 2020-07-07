package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.mapper.ExJobTaskMapper;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExFieldDict;
import com.sdy.dataexchange.biz.model.ExFieldMapping;
import com.sdy.dataexchange.biz.model.ExJobTask;
import com.sdy.dataexchange.biz.service.ExJobTaskService;
import com.sdy.dataexchange.biz.service.RepeatDemoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * <p>
 * 提取的重复代码
 * </p>
 *
 * @author 高连明
 * @since 2019-07-26
 */
@Service
public class RepeatDemoServiceImpl implements RepeatDemoService {
    @Autowired
    ExJobTaskMapper exJobTaskMapper;

    /**
     * 拿到省市区全名
     *
     * @param allInfo
     * @return
     */
    @Override
    public String getFullName(List<String> allInfo) {
        String name = null;
        //获取所属地的完整名称
        if (allInfo.size() == 2) {
            name = allInfo.get(0) + allInfo.get(1);
        } else if (allInfo.size() == 3) {
            name = allInfo.get(0) + allInfo.get(1) + allInfo.get(2);
        } else if (allInfo.size() == 4) {
            name = allInfo.get(0) + allInfo.get(1) + allInfo.get(2) + allInfo.get(3);
        }
        return name;
    }

    /**
     * 获取mysql数据库所有字段
     *
     * @param tables
     * @param exDbDict
     */
    @Override
    public void mysqlField(List<Map<String,Object>> tables, ExDbDict exDbDict) {
        List<Map<String, Object>> tableNames = new ArrayList<>();
        List<Map<String, Object>> fields = new ArrayList<>();
        DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, exDbDict.getDbIp(), exDbDict.getDbPort(), exDbDict.getDbName(), exDbDict.getDbUser(), exDbDict.getDbPasswd(), null));
        //获取所有表名
        tableNames = dataAdapter.executeQuery("show tables from " + exDbDict.getDbName());
        if (!tableNames.isEmpty()) {
            for (Map<String, Object> tableName : tableNames) {
                tableName.put("text", tableName.remove("Tables_in_" + exDbDict.getDbName()));
                //获取该表所有字段名
                fields = dataAdapter.queryForList("SELECT * FROM information_schema.columns WHERE table_schema =?" +
                        " AND table_name = ? ORDER BY column_name ASC;" ,exDbDict.getDbName(),tableName.get("text"));
                setFields(fields, tableName);
                tables.add(tableName);
            }
        }
    }

    @Override
    public void setFields(List<Map<String, Object>> fields, Map<String, Object> tableName) {
        if (!fields.isEmpty()) {
            for (Map<String, Object> field : fields) {
                field.put("field", field.remove("COLUMN_NAME"));
                field.put("fieldType", field.remove("DATA_TYPE"));
                tableName.put("rows", fields);
            }
        }
    }

    /**
     * 获取oracle数据库字段
     *
     * @param tables
     * @param exDbDict
     */
    @Override
    public void oracleField(List<Map<String,Object>> tables, ExDbDict exDbDict) {
        List<Map<String, Object>> tableNames = new ArrayList<>();
        List<Map<String, Object>> fields = new ArrayList<>();
        DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, exDbDict.getDbIp(), exDbDict.getDbPort(), exDbDict.getDbName(), exDbDict.getDbUser(), exDbDict.getDbPasswd(), null));
        //获取所有表名
        tableNames = dataAdapter.executeQuery("select * from user_tab_comments");
        getSourceNames(tables, tableNames, dataAdapter);
    }

    /**
     * 获取源表名
     * @param tables
     * @param tableNames
     * @param dataAdapter
     */
    @Override
    public void getSourceNames(List tables, List<Map<String, Object>> tableNames, DataAdapter dataAdapter) {
        List<Map<String, Object>> fields;
        if (!tableNames.isEmpty()) {
            for (Map<String, Object> tableName : tableNames) {
                tableName.put("text", tableName.remove("TABLE_NAME"));
                //获取所有字段名
                fields = dataAdapter.queryForList("select COLUMN_NAME,DATA_TYPE from user_tab_columns where " +
                        "TABLE_NAME=? order by COLUMN_ID ASC", tableName.get("text"));
                setFields(fields,tableName);
                tables.add(tableName);
            }
        }
    }

    /**
     * 新建映射
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
    @Override
    public Integer saveTasks(List<Map<String, String>> tableInfos, Integer fieldResult, Integer sourceSyncId, Integer destSyncId, List<ExFieldDict> leftFields, List<ExFieldDict> rightFields, List<ExFieldMapping> exFieldMappings, List<String> sourceSyncField, List<String> destSyncField) {
        for (Map<String, String> tableInfo : tableInfos) {
            ExFieldDict sourceFieldDict = new ExFieldDict();
            ExFieldDict destFieldDict = new ExFieldDict();
            ExFieldMapping exFieldMapping = new ExFieldMapping();
            String leftFieldName = tableInfo.get("field");
            String leftFieldType = tableInfo.get("fieldType");
            String rightFieldName = tableInfo.get("field1");
            String rightFieldType = tableInfo.get("field1Type");
            if (StringUtils.isNotBlank(leftFieldName) && StringUtils.isNotBlank(leftFieldType) && StringUtils.isNotBlank(rightFieldName) && StringUtils.isNotBlank(rightFieldType)) {
                if (!sourceSyncField.contains(leftFieldName)) {
                    sourceFieldDict.setSyncId(sourceSyncId);
                    sourceFieldDict.setSyncField(leftFieldName);
                    sourceFieldDict.setSyncType(leftFieldType);
                }
                if (destSyncField.contains(rightFieldName)) {
                } else {
                    if (sourceSyncId.equals(destSyncId) && leftFieldName.equals(rightFieldName)) {
                    } else {
                        destFieldDict.setSyncId(destSyncId);
                        destFieldDict.setSyncField(rightFieldName);
                        destFieldDict.setSyncType(rightFieldType);
                    }
                }
                if (exJobTaskMapper.getFieldMappingId(sourceSyncId, leftFieldName, destSyncId, rightFieldName) == null) {
                    exFieldMapping.setSourceSyncid(sourceSyncId);
                    exFieldMapping.setDestSyncid(destSyncId);
                    exFieldMapping.setSourceSyncname(leftFieldName);
                    exFieldMapping.setDestSyncname(rightFieldName);
                    exFieldMapping.setValidFlag("1");
                }
                if (sourceFieldDict.getSyncId() != null && sourceFieldDict.getSyncField() != null && sourceFieldDict.getSyncType() != null) {
                    leftFields.add(sourceFieldDict);
                }
                if (destFieldDict.getSyncId() != null && destFieldDict.getSyncField() != null && destFieldDict.getSyncType() != null) {
                    rightFields.add(destFieldDict);
                }
                if (exFieldMapping.getSourceSyncid() != null && exFieldMapping.getSourceSyncname() != null && exFieldMapping.getDestSyncid() != null && exFieldMapping.getDestSyncname() != null && exFieldMapping.getValidFlag() != null) {
                    exFieldMappings.add(exFieldMapping);
                }
            }
        }
        if (leftFields.size() != 0) {
            //将源字段加入到ExFieldDict
            exJobTaskMapper.insertIntoLeftExFieldDicts(leftFields);
        }
        if (rightFields.size() != 0) {
            //将目标字段加入到ExFieldDict
            exJobTaskMapper.insertIntoRightExFieldDicts(rightFields);
        }
        if (exFieldMappings.size() != 0) {
            //将字段映射关系加入到映射表
            fieldResult = exJobTaskMapper.insertIntoExFieldMappings(exFieldMappings);
        }
        return fieldResult;
    }

}
