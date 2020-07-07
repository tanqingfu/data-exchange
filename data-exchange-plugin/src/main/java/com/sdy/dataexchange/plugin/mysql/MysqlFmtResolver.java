package com.sdy.dataexchange.plugin.mysql;

import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.converts.MySqlTypeConvert;
import com.sdy.dataexchange.plugin.config.PluginConfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author zhouziqiang 
 */
public class MysqlFmtResolver implements DbFmtResolver {
    @Override
    public String decorateValue(Object value, Integer sqlType) {
        return MySqlTypeConvert.decorateValue(value, sqlType);
    }

    @Override
    public String decorateColumnName(String columnName) {
        return "`" + columnName + "`";
    }

    @Override
    public String decorateSchemaName(String schemaName) {
        return "`" + schemaName + "`";
    }

    @Override
    public String decorateTableName(String tableName) {
        return "`" + tableName + "`";
    }

    @Override
    public List<String> createProcedure(String procedureName, List<String> sqlList, String syncType) {
        if (PluginConfig.SyncType.FULL.equals(syncType)) {
            // 全量同步，不需要进行异常处理
            return Collections.singletonList("drop PROCEDURE if exists " + procedureName + ";\n" +
                    "CREATE PROCEDURE " + procedureName + " ()\n" +
                    "BEGIN\n" +
                    String.join("\n", sqlList) + "\n" +
                    "END;;");
        }
        return Collections.singletonList("drop PROCEDURE if exists " + procedureName + ";\n" +
                "CREATE PROCEDURE " + procedureName + " ()\n" +
                "BEGIN\n" +
                "DECLARE CONTINUE HANDLER FOR 1062\n" +
                "BEGIN\n" +
                "SELECT 'duplicate keys found';\n" +
                "END;\n" +
                String.join("\n", sqlList) + "\n" +
                "END;;");
    }

    @Override
    public String createProcedureCaller(String procedureName) {
        return "call `" + procedureName + "`()";
    }

    @Override
    public String createFullSelectStatement(String schemaName, String tableName) {
        return String.format("SELECT t.* FROM %s.%s t", decorateSchemaName(schemaName), decorateTableName(tableName));
    }

    @Override
    public String parserSchemaName(String dbName, String dbUser) {
        return dbName;
    }
}
