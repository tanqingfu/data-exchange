package com.sdy.dataexchange.plugin.oracle;

import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.converts.OracleTypeConvert;
import com.sdy.dataexchange.plugin.config.PluginConfig;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouziqiang 
 */
public class OracleFmtResolver implements DbFmtResolver {
    @Override
    public String decorateValue(Object value, Integer sqlType) {
        return OracleTypeConvert.decorateValue(value, sqlType);
    }

    @Override
    public String decorateColumnName(String columnName) {
        return "\"" + columnName + "\"";
    }

    @Override
    public String decorateSchemaName(String schemaName) {
        return "\"" + schemaName + "\"";
    }

    @Override
    public String decorateTableName(String tableName) {
        return "\"" + tableName + "\"";
    }

    @Override
    public List<String> createProcedure(String procedureName, List<String> sqlList, String syncType) {
        if (PluginConfig.SyncType.FULL.equals(syncType)) {
            // 全量同步，不需要进行异常处理
            return Collections.singletonList("CREATE OR REPLACE PROCEDURE \"" + procedureName + "\" AS\n" +
                    " loc_c CLOB; \n" +
                    " buf_c VARCHAR2(6162); \n" +
                    " loc_b BLOB; \n" +
                    " buf_b RAW(6162); \n" +
                    " loc_nc NCLOB; \n" +
                    " buf_nc NVARCHAR2(6162); \n" +
                    "BEGIN\n" +
                    String.join("", sqlList) +
                    "\nEND;");
        }
        return Collections.singletonList("CREATE OR REPLACE PROCEDURE \"" + procedureName + "\" AS\n" +
                " loc_c CLOB; \n" +
                " buf_c VARCHAR2(6162); \n" +
                " loc_b BLOB; \n" +
                " buf_b RAW(6162); \n" +
                " loc_nc NCLOB; \n" +
                " buf_nc NVARCHAR2(6162); \n" +
                "BEGIN\n" +
                sqlList.stream().map(this::createProcedureExceptionBlock).collect(Collectors.joining()) +
                "\nEND;");
    }

    @Override
    public String createProcedureCaller(String procedureName) {
        return "call \"" + procedureName + "\"()";
    }

    private String createProcedureExceptionBlock(String sql) {
        return "BEGIN\n" +
                sql + 
                "EXCEPTION\n" +
                "WHEN DUP_VAL_ON_INDEX THEN\n" +
                "BEGIN\n" +
                "      DBMS_OUTPUT.PUT_LINE('duplicate keys found');\n" +
                "END;\n" +
                "END;\n";
    }

    @Override
    public String createFullSelectStatement(String schemaName, String tableName) {
        return String.format("SELECT t.* FROM %s.%s t", decorateSchemaName(schemaName), decorateTableName(tableName));
    }

    @Override
    public String parserSchemaName(String dbName, String dbUser) {
        return dbUser;
    }
}
