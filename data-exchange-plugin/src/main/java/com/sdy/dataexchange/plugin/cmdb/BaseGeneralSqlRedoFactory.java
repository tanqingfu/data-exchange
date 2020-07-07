package com.sdy.dataexchange.plugin.cmdb;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.SqlRedoFactory;
import com.sdy.dataexchange.plugin.common.SqlTypes;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.dataexchange.plugin.config.PluginConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * sql语句重建工厂
 * @author zhouziqiang 
 */
public abstract class BaseGeneralSqlRedoFactory implements SqlRedoFactory {
    private static final String INSERT_TEMPLATE = "insert into %s.%s (%s) values (%s);";
    // TODO limit 1
    private static final String UPDATE_TEMPLATE = "update %s.%s set %s %s;";
    private static final String DELETE_TEMPLATE = "delete from %s.%s %s;";

    public String formatInsertSql(String db, String schema, String table, String fieldStr, String valueStr) {
        return String.format(INSERT_TEMPLATE, schema, table, fieldStr, valueStr);
    }
    
    public String formatUpdateSql(String db, String schema, String table, String setStr, String whereStr) {
        return String.format(UPDATE_TEMPLATE, schema, table, setStr, whereStr);
    }
    
    public String formatDeleteSql(String db, String schema, String table, String whereStr) {
        return String.format(DELETE_TEMPLATE, schema, table, whereStr);
    }
    
    /**
     * sql语句重建
     * @param sqlRedoObj sql语句对象
     * @return sql语句
     */
    @Override
    public String build(SqlRedoObj sqlRedoObj, String mappingDatabase, String mappingSchema, String mappingTable, Map<String, String> fieldMap, Map<String, String> columnTypeMap) {
        String statement;
        replaceProperties(sqlRedoObj, mappingDatabase, mappingSchema, mappingTable, fieldMap);
        switch (sqlRedoObj.getType()) {
            case "INSERT":
                statement = buildInsertStatement(sqlRedoObj, columnTypeMap);
                break;
            case "UPDATE":
                statement = buildUpdateStatement(sqlRedoObj, columnTypeMap);
                break;
            case "DELETE":
                statement = buildDeleteStatement(sqlRedoObj, columnTypeMap);
                break;
            default:
                statement = null;
        }
        return statement;
    }

    /**
     * 生成insert语句
     * @param sqlRedoObj sql语句对象
     * @return sql语句
     */
    public String buildInsertStatement(SqlRedoObj sqlRedoObj, Map<String, String> columnTypeMap) {
        String statement = "";
        if (sqlRedoObj.getData() == null || sqlRedoObj.getData().isEmpty()) {
            return statement;
        }
        DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.getDbType(sqlRedoObj.getTargetDbType()));
        for (Map<String, Object> data : sqlRedoObj.getData()) {
            String fieldStrs = "";
            String valueStrs = "";
            StringBuilder procedure = new StringBuilder();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                fieldStrs = fieldStrs.concat(",")
                        .concat(resolver.decorateColumnName(entry.getKey()));
                int destSqlType = getSqlType(columnTypeMap.get(entry.getKey()));
                Object value = entry.getValue();
//                if (sqlType == SqlTypes.STRING && checkLongText(entry.getValue())) {
//                    procedure.append(constructProcedureForLongText(sqlType, (String) entry.getValue()));
//                    value = "";
//                }
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
            statement = statement.concat("\n").concat(procedure.toString());
        }
        return statement;
    }

    /**
     * 生成update语句
     * @param sqlRedoObj sql语句对象
     * @return sql语句
     */
    public String buildUpdateStatement(SqlRedoObj sqlRedoObj, Map<String, String> columnTypeMap) {
        String statement = "";
        if (sqlRedoObj.getData() == null || sqlRedoObj.getData().isEmpty()) {
            return statement;
        }
        DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.getDbType(sqlRedoObj.getTargetDbType()));
        int idx = 0;
        for (Map<String, Object> data : sqlRedoObj.getData()) {
            if (!data.isEmpty()) {
                String setStrs = data.entrySet().stream().map(
                        es -> resolver.decorateColumnName(es.getKey())
                                .concat("=")
                                .concat(resolver.decorateValue(es.getValue(), getSqlType(columnTypeMap.get(es.getKey()))))
                ).collect(Collectors.joining(","));
                Map<String, Object> oldData = sqlRedoObj.getOld().get(idx);
                Map<String, Object> whereMap = genWhereMap(sqlRedoObj.getPkNames(), oldData, data);
                String whereStrs = buildWhereStatement(whereMap, resolver, sqlRedoObj.getSqlType(), columnTypeMap);
                statement = statement.concat(formatUpdateSql(
                        null,
                        resolver.decorateSchemaName(sqlRedoObj.getSchema()),
                        resolver.decorateTableName(sqlRedoObj.getTable()),
                        setStrs,
                        whereStrs));
            }
            idx++;
        }
        return statement;
    }

    /**
     * 生成delete语句
     * @param sqlRedoObj sql语句对象
     * @return sql语句
     */
    public String buildDeleteStatement(SqlRedoObj sqlRedoObj, Map<String, String> columnTypeMap) {
        String statement = "";
        int size = 0;
        if (sqlRedoObj.getOld() != null && !sqlRedoObj.getOld().isEmpty()) {
            size = sqlRedoObj.getOld().size();
        }
        if (sqlRedoObj.getData() != null && !sqlRedoObj.getData().isEmpty()) {
            size = sqlRedoObj.getData().size();
        }
        if (size == 0) {
            return statement;
        }
        DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.getDbType(sqlRedoObj.getTargetDbType()));
        for (int i = 0; i < size; i++) {
            Map<String, Object> old = new HashMap<>();
            if (sqlRedoObj.getOld() != null && sqlRedoObj.getOld().size() > i) {
                old = sqlRedoObj.getOld().get(i);
            }
            Map<String, Object> data = Collections.emptyMap();
            if (sqlRedoObj.getData() != null && sqlRedoObj.getData().size() > i) {
                data = sqlRedoObj.getData().get(i);
            }
            Map<String, Object> whereMap = genWhereMap(sqlRedoObj.getPkNames(), old, data);
            String whereStrs = buildWhereStatement(whereMap, resolver, sqlRedoObj.getSqlType(), columnTypeMap);
            statement = statement.concat(formatDeleteSql(
                    null,
                    resolver.decorateSchemaName(sqlRedoObj.getSchema()),
                    resolver.decorateTableName(sqlRedoObj.getTable()),
                    whereStrs));
        }
        return statement;
    }

    private boolean checkLongText(Object obj) {
        return obj instanceof String && ((String) obj).length() > 3800;
    }

    private String constructProcedureForLongText(int sqlType, String value) {
        return "";
    }

    /**
     * 获取sql字段类型
     */
    protected Integer getSqlType(String columnType) {
        return getColumnConverter().processSqlTypeConvert(columnType);
    }

    /**
     * 属性映射
     */
    private void replaceProperties(SqlRedoObj sqlRedoObj, String mappingDatabase, String mappingSchema, String mappingTable, Map<String, String> fieldMap) {
        // 字段类型
        Map<String, Integer> newSqlTypeMap = new HashMap<>(sqlRedoObj.getSqlType().size() + 4);
        sqlRedoObj.getSqlType().forEach((k, v) -> {
            if (fieldMap.containsKey(k)) {
                newSqlTypeMap.put(fieldMap.get(k), v);
            }
        });
        sqlRedoObj.setSqlType(newSqlTypeMap);
        // 映射基本属性
        sqlRedoObj.setSchema(mappingSchema);
        sqlRedoObj.setTable(mappingTable);
        sqlRedoObj.setDatabase(mappingDatabase);
        // 映射主键
        if (sqlRedoObj.getPkNames() != null && sqlRedoObj.getPkNames().size() > 0) {
            List<String> pkList = new ArrayList<>();
            sqlRedoObj.getPkNames().forEach(pk -> {
                String newPk = fieldMap.get(pk);
                if (newPk != null) {
                    pkList.add(newPk);
                }
            });
            sqlRedoObj.setPkNames(pkList);
        }
        // 映射新数据字段
        if (sqlRedoObj.getData() != null && sqlRedoObj.getData().size() > 0) {
            List<Map<String, Object>> dataFieldMapList = new ArrayList<>();
            sqlRedoObj.getData().forEach(dataMap -> {
                Map<String, Object> dataFieldMap = new HashMap<>();
                dataMap.forEach((k, v) -> {
                    String newk = fieldMap.get(k);
                    if (newk != null) {
                        dataFieldMap.put(newk, v);
                    }
                });
                dataFieldMapList.add(dataFieldMap);
            });
            sqlRedoObj.setData(dataFieldMapList);
        }
        // 映射旧数据字段
        if (sqlRedoObj.getOld() != null && sqlRedoObj.getOld().size() > 0) {
            List<Map<String, Object>> dataFieldMapList = new ArrayList<>();
            sqlRedoObj.getOld().forEach(dataMap -> {
                Map<String, Object> dataFieldMap = new HashMap<>();
                dataMap.forEach((k, v) -> {
                    String newk = fieldMap.get(k);
                    if (newk != null) {
                        dataFieldMap.put(newk, v);
                    }
                });
                dataFieldMapList.add(dataFieldMap);
            });
            sqlRedoObj.setOld(dataFieldMapList);
        }
    }

    /**
     * 生成where所需的map数据
     * @param pkNames 主键列表
     * @param oldData 原始数据
     * @param data 新数据
     * @return where map
     */
    protected Map<String, Object> genWhereMap(List<String> pkNames, Map<String, Object> oldData, Map<String, Object> data) {
        Map<String, Object> whereMap = new HashMap<>(data.size() * 2);
        if (pkNames != null && !pkNames.isEmpty()) {
            pkNames.forEach(pk -> {
                Object v = oldData.computeIfAbsent(pk, data::get);
                whereMap.put(pk, v);
            });
        } else {
            whereMap.putAll(data);
            whereMap.putAll(oldData);
        }
        return whereMap;
    }

    /**
     * 生成where语句
     * @param whereData where条件
     * @param resolver 数据库处理器
     * @return
     */
    protected String buildWhereStatement(Map<String, Object> whereData, DbFmtResolver resolver, Map<String, Integer> sqlTypeMap, Map<String, String> columnTypeMap) {
        String whereStrs = "";
        if (!whereData.isEmpty()) {
            for (Map.Entry<String, Object> entry : whereData.entrySet()) {
                whereStrs = whereStrs.concat(" and ")
                        .concat(resolver.decorateColumnName(entry.getKey()))
                        .concat(entry.getValue() == null ? " is " : "=")
                        .concat(resolver.decorateValue(entry.getValue(), getSqlType(columnTypeMap.get(entry.getKey()))));
            }
            whereStrs = "where ".concat(whereStrs.substring(5));
        }
        return whereStrs;
    }
}
