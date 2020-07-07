package com.sdy.dataexchange.plugin.common;

import java.util.List;

/**
 * 默认数据库处理
 * @author zhouziqiang 
 */
public interface DbFmtResolver {
    /**
     * 修饰值
     * @param value
     * @param sqlType
     * @return
     */
    String decorateValue(Object value, Integer sqlType);
    /**
     * 修饰列名
     * @param columnName
     * @return
     */
    String decorateColumnName(String columnName);

    /**
     * 修饰schema
     * @param schemaName
     * @return
     */
    String decorateSchemaName(String schemaName);

    /**
     * 修饰表名
     * @param tableName
     * @return
     */
    String decorateTableName(String tableName);

    /**
     * 创建存储过程
     * @return
     */
    List<String> createProcedure(String procedureName, List<String> sqlList, String syncType);

    /**
     * 创建存储过程
     * @return
     */
    String createProcedureCaller(String procedureName);

    /**
     * 创建全量语句
     */
    String createFullSelectStatement(String schemaName, String tableName);

    /**
     * 获取模式名
     */
    String parserSchemaName(String dbName, String dbUser);
}
