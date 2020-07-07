package com.sdy.dataexchange.plugin.oracle;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.cmdb.BaseGeneralSqlRedoFactory;
import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.SqlTypes;
import com.sdy.dataexchange.plugin.common.converts.OracleTypeConvert;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.dataexchange.plugin.config.PluginConfig;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * oracle语句重建工厂
 * @author zhouziqiang
 */
public class OracleSqlRedoFactory extends BaseGeneralSqlRedoFactory {
    private static ITypeConvert oracleConvert = new OracleTypeConvert();
    
    private static final int MAX_BYTE_LENGTH = 2048;

    @Override
    public ITypeConvert getColumnConverter() {
        return oracleConvert;
    }

    /**
     * 生成insert语句
     * @param sqlRedoObj sql语句对象
     * @return sql语句
     */
    @Override
    public String buildInsertStatement(SqlRedoObj sqlRedoObj, Map<String, String> columnTypeMap) {
        String statement = "";
        if (sqlRedoObj.getData() == null || sqlRedoObj.getData().isEmpty()) {
            return statement;
        }
        DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.getDbType(sqlRedoObj.getTargetDbType()));
        for (Map<String, Object> data : sqlRedoObj.getData()) {
            String fieldStrs = "";
            String valueStrs = "";
            Optional<Object> rowId = data.entrySet().stream().filter(es -> es.getKey().equals("ROWSEQ")).map(Map.Entry::getValue).findAny();
            AtomicReference<String> procStat = new AtomicReference<>("");
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                Object value = entry.getValue();
                int destSqlType = getSqlType(columnTypeMap.get(entry.getKey()));
                if ((SqlTypes.BLOB == destSqlType || SqlTypes.CLOB == destSqlType)
                        && value instanceof String && ((String) value).length() > 4000) {
                    if (!rowId.isPresent()) {
                        throw new RuntimeException("找不到ROWID");
                    }
                    procStat.set(createProcedureForBlob(rowId.get().toString(), sqlRedoObj.getSchema(), sqlRedoObj.getTable(), entry.getKey(), value.toString()));
                }
                fieldStrs = fieldStrs.concat(",").concat(resolver.decorateColumnName(entry.getKey()));
                valueStrs = valueStrs.concat(",").concat(resolver.decorateValue(value, destSqlType));
            }
            fieldStrs = fieldStrs.substring(1);
            valueStrs = valueStrs.substring(1);
            statement = statement.concat(formatInsertSql(
                    null,
                    resolver.decorateSchemaName(sqlRedoObj.getSchema()),
                    resolver.decorateTableName(sqlRedoObj.getTable()),
                    fieldStrs,
                    valueStrs));
            statement = statement.concat("\n").concat(procStat.get());
        }
        return statement;
    }

//    /**
//     * 生成update语句
//     * @param sqlRedoObj sql语句对象
//     * @return sql语句
//     */
//    @Override
//    public String buildUpdateStatement(SqlRedoObj sqlRedoObj, Map<String, String> columnTypeMap) {
//        String statement = "";
//        if (sqlRedoObj.getData() == null || sqlRedoObj.getData().isEmpty()) {
//            return statement;
//        }
//        DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.getDbType(sqlRedoObj.getTargetDbType()));
//        int idx = 0;
//        for (Map<String, Object> data : sqlRedoObj.getData()) {
//            if (!data.isEmpty()) {
//                List<String> setList = new ArrayList<>();
//                Optional<Object> rowId = data.entrySet().stream().filter(es -> es.getKey().equals("ROWID")).map(Map.Entry::getValue).findAny();
//                AtomicReference<String> procStat = new AtomicReference<>("");
//                data.forEach((key, value) -> {
//                    int sqlType = getSqlType(columnTypeMap.get(key));
//                    if ((SqlTypes.BLOB == sqlType || SqlTypes.CLOB == sqlType) && value instanceof String && ((String) value).length() > 4000) {
//                        if (!rowId.isPresent()) {
//                            throw new RuntimeException("找不到ROWID");
//                        }
//                        procStat.set(createProcedureForBlob(rowId.get().toString(), sqlRedoObj.getSchema(), sqlRedoObj.getTable(), key, value.toString()));
//                    } else {
//                        setList.add(resolver.decorateColumnName(key)
//                                .concat("=")
//                                .concat(resolver.decorateValue(value, sqlType)));
//                    }
//                });
//                String setStrs = String.join(",", setList);
//                Map<String, Object> oldData = sqlRedoObj.getOld().get(idx);
//                Map<String, Object> whereMap = genWhereMap(sqlRedoObj.getPkNames(), oldData, data);
//                String whereStrs = buildWhereStatement(whereMap, resolver, sqlRedoObj.getSqlType(), columnTypeMap);
//                statement = statement.concat(formatUpdateSql(
//                        null,
//                        resolver.decorateSchemaName(sqlRedoObj.getSchema()),
//                        resolver.decorateTableName(sqlRedoObj.getTable()),
//                        setStrs,
//                        whereStrs));
//                if (!"".equals(procStat.get())) {
//                    statement = statement.concat("\n").concat(procStat.get());
//                }
//            }
//            idx++;
//        }
//        return statement;
//    }
    
    private String createProcedureForBlob(String rowId, String schemaName, String tbName, String colName, String hexStr) {
        if (hexStr.length() % 2 == 1) {
            hexStr = "0" + hexStr;
        }
        String template = " select \"%s\" into loc_b from \"%s\".\"%s\" where \"ROWSEQ\" = '%s' for update;\n" +
                "%s" +
                "  dbms_lob.trim(loc_b, %d);\n";
        String loopStatTemplate = " buf_b := HEXTORAW('%s'); \n" +
                "  dbms_lob.write(loc_b, %d, %d, buf_b);\n";
        String loopStat = "";
        int start = 0;
        int restBytes = hexStr.length() / 2;
        while (restBytes > 0) {
            int bytesRead = Math.min(restBytes, MAX_BYTE_LENGTH);
            loopStat = loopStat.concat(
                    String.format(
                            loopStatTemplate,
                            hexStr.substring(start * 2, (start + bytesRead) * 2),
                            bytesRead,
                            start + 1));
            restBytes -= bytesRead;
            start += bytesRead;
        }
        return String.format(template, colName, schemaName, tbName, rowId, loopStat, hexStr.length() / 2);
    }
}
