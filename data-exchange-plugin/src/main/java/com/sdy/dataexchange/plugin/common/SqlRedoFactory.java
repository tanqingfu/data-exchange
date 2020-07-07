package com.sdy.dataexchange.plugin.common;

import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;

import java.util.Map;

/**
 * sql语句重建工厂
 * @author zhouziqiang 
 */
public interface SqlRedoFactory {
    /**
     * sql语句重建
     * @param sqlRedoObj sql语句对象
     * @param mappingDatabase 目标库
     * @param mappingTable 目标表
     * @param mappingSchema 目标模式
     * @param fieldMap 字段映射
     * @return sql语句
     */
    String build(SqlRedoObj sqlRedoObj, String mappingDatabase, String mappingSchema, String mappingTable, Map<String, String> fieldMap, Map<String, String> columnTypeMap);

    /**
     * 获取类型转换器
     */
    ITypeConvert getColumnConverter();
}
