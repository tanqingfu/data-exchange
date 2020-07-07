package com.sdy.dataexchange.plugin.sqlserver;

import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.converts.MySqlTypeConvert;
import com.sdy.dataexchange.plugin.common.converts.SqlServerTypeConvert;
import com.sdy.dataexchange.plugin.config.PluginConfig;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouziqiang 
 */
public class SqlServerFmtResolver implements DbFmtResolver {
    @Override
    public String decorateValue(Object value, Integer sqlType) {
        return SqlServerTypeConvert.decorateValue(value, sqlType);
    }

    @Override
    public String decorateColumnName(String columnName) {
        return "[" + columnName + "]";
    }

    @Override
    public String decorateSchemaName(String schemaName) {
        return "[" + schemaName + "]";
    }

    @Override
    public String decorateTableName(String tableName) {
        return "[" + tableName + "]";
    }

    @Override
    public List<String> createProcedure(String procedureName, List<String> sqlList, String syncType) {
        String dropStatement = "if exists(SELECT * FROM SYSOBJECTS WHERE NAME='" + procedureName + "')\n" +
                "drop PROCEDURE " + procedureName + ";\n";
        if (PluginConfig.SyncType.FULL.equals(syncType)) {
            // 全量同步，不需要进行异常处理
            return Arrays.asList(dropStatement,
                    "CREATE PROCEDURE " + procedureName + "\n" +
                    "AS\n" +
                    "BEGIN\n" +
                    String.join("\n", sqlList) + "\n" +
                    "END;");
        }
        return Arrays.asList(dropStatement,
                "CREATE PROCEDURE " + procedureName + "\n" +
                "AS\n" +
                "BEGIN\n" +
                String.join("\n", sqlList) + "\n" +
                "END;");
    }

    @Override
    public String createProcedureCaller(String procedureName) {
        return "exec [" + procedureName + "]";
    }
    
    @Override
    public String createFullSelectStatement(String schemaName, String tableName) {
        return String.format("SELECT t.* FROM [%s].[%s] t", decorateSchemaName(schemaName), decorateTableName(tableName));
    }

    @Override
    public String parserSchemaName(String dbName, String dbUser) {
        return null;
    }
}
