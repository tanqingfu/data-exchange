package com.sdy.dataexchange.plugin.cmdb;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.biz.model.MonitorResult;
import com.sdy.dataexchange.plugin.common.SqlParser;
import com.sdy.dataexchange.plugin.common.SqlParserPlus;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.dataexchange.plugin.oracle.BlobTmpObject;
import com.sdy.dataexchange.plugin.oracle.MonitorResultPlus;
import com.sdy.mvc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;

import java.io.StringReader;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractMonitorResultParser implements SqlParser {
    
    @Override
    public List<SqlRedoObj> parse(String str, Map<String, String> columnTypeMap) throws Exception {
        MonitorResult monitorResult = JsonUtil.fromJson(str, MonitorResult.class);
        SqlRedoObj sqlRedoObj = new SqlRedoObj();
        if (monitorResult.getEmpty() == null) {
            monitorResult.setEmpty(false);
        }
        sqlRedoObj.setEmpty(monitorResult.getEmpty());
        if (!sqlRedoObj.getEmpty()) {
            if (monitorResult.isBlobData()) {
                MonitorResultPlus monitorResultPlus = JsonUtil.fromJson(str, MonitorResultPlus.class);
                BlobTmpObject blobTmpObject = monitorResultPlus.getBlobData();
                List<SqlRedoObj> result = new ArrayList<>();
                if (blobTmpObject.getObj1() != null) {
                    result.add(blobTmpObject.getObj1());
                }
                if (blobTmpObject.getObj2() != null) {
                    result.add(blobTmpObject.getObj2());
                }
                return result;
            }
            if (monitorResult.getSqlRedo() == null) {
                log.error("数据错误！{}", str);
            }
            String redoSqlTpl = monitorResult.getSqlRedo().trim();
            try {
                if (redoSqlTpl.startsWith("insert ") || redoSqlTpl.startsWith("INSERT ")) {
                    sqlParserInsert(sqlRedoObj, redoSqlTpl, monitorResult.getRowseq(), columnTypeMap);
                } else if (redoSqlTpl.startsWith("update ") || redoSqlTpl.startsWith("UPDATE ")) {
                    sqlParserUpdate(sqlRedoObj, redoSqlTpl, monitorResult.getRowseq(), columnTypeMap);
                } else if (redoSqlTpl.startsWith("delete ") || redoSqlTpl.startsWith("DELETE ")) {
                    sqlParserDelete(sqlRedoObj, redoSqlTpl, monitorResult.getRowseq(), columnTypeMap);
                } else {
                    sqlRedoObj.setIsDdl(true);
                    sqlRedoObj.setType("UNKNOWN");
                }
            } catch (Exception e) {
                log.error("Sql解析失败！MonitorResult: {}", str);
                throw e;
            }
        } else {
            sqlRedoObj.setType("UNKNOWN");
        }
        return Collections.singletonList(sqlRedoObj);
    }

    @Override
    public DbType getDbType() {
        throw new RuntimeException("Unknown db type!");
    }

    @Override
    public String getPkName() {
        return "ROWID";
    }

    private SqlRedoObj sqlParserInsert(SqlRedoObj sqlRedoObj, String sql, String pkValue, Map<String, String> columnTypeMap) throws Exception {
        SqlParserPlus parser = new SqlParserPlus();
        Statement stmt = parser.parse(DbType.ORACLE, sql);
        Insert insert = (Insert) stmt;
        //获取表信息T
        DbType dbType = getDbType();

        Table table = insert.getTable();
        sqlRedoObj.setType("INSERT");
        sqlRedoObj.setEmpty(false);
        sqlRedoObj.setIsDdl(false);
        sqlRedoObj.setSchema(table.getSchemaName());
        sqlRedoObj.setTable(table.getName());
        sqlRedoObj.setDatabase(table.getDatabase().getDatabaseName());
        sqlRedoObj.setDbType(dbType.getDb().toUpperCase());
        // 字段列表
        List<String> fieldList = insert.getColumns()
                .stream()
                .map(Column::getColumnName)
                .map(c -> c.substring(1, c.length() - 1))
                .collect(Collectors.toList());
        // 值列表
        List<Object> valueList = new ArrayList<>();
        ((ExpressionList)insert.getItemsList()).getExpressions().forEach(expression -> {
            valueList.add(expression.toString().trim());
        });
        Map<String, Integer> sqlTypeMap = new HashMap<>();
        Map<String, Object> d = new HashMap<>();
        for (int i = 0; i < fieldList.size(); i++) {
            String f = fieldList.get(i);
            Object v = valueList.get(i);
            int sqlType = getSqlType(columnTypeMap.get(f));
            sqlTypeMap.put(f, sqlType);
            d.put(f, getColumnConverter().formatValue(v, columnTypeMap.getOrDefault(f, "VARCHAR"), 2));
        }
        // 添加ROWID
        d.put(getPkName(), pkValue);
        sqlTypeMap.put(getPkName(), Types.CHAR);
        sqlRedoObj.setData(Collections.singletonList(d));
        sqlRedoObj.setSqlType(sqlTypeMap);
        return sqlRedoObj;
    }

    /**
     * 获取sql字段类型
     */
    private Integer getSqlType(String columnType) {
        return getColumnConverter().processSqlTypeConvert(columnType);
    }

    private SqlRedoObj sqlParserUpdate(SqlRedoObj sqlRedoObj, String sql, String pkValue, Map<String, String> columnTypeMap) throws Exception {
        SqlParserPlus parser = new SqlParserPlus();
        Statement stmt = parser.parse(DbType.ORACLE, sql);
        Update update = (Update) stmt;
        //获取表信息
        DbType dbType = getDbType();

        Table table = update.getTable();
        sqlRedoObj.setType("UPDATE");
        sqlRedoObj.setEmpty(false);
        sqlRedoObj.setIsDdl(false);
        sqlRedoObj.setSchema(table.getSchemaName());
        sqlRedoObj.setTable(table.getName());
        sqlRedoObj.setDatabase(table.getDatabase().getDatabaseName());
        sqlRedoObj.setDbType(dbType.getDb().toUpperCase());

        // 字段列表
        List<String> fieldList = update.getColumns()
                .stream()
                .map(Column::getColumnName)
                .map(c -> c.substring(1, c.length() - 1))
                .collect(Collectors.toList());
        // 值列表
        List<Object> valueList = new ArrayList<>();
        update.getExpressions().forEach(expression -> valueList.add(expression.toString().trim()));
        Map<String, Integer> sqlTypeMap = new HashMap<>();
        // 新数据
        Map<String, Object> data = new HashMap<>();
        for (int i = 0; i < fieldList.size(); i++) {
            String field = fieldList.get(i);
            int sqlType = getSqlType(columnTypeMap.get(field));
            sqlTypeMap.put(field, sqlType);
            String value = (String) getColumnConverter().formatValue(valueList.get(i), columnTypeMap.getOrDefault(field, "VARCHAR"), 2);
            data.put(field, value);
        }
        // 原数据
        Map<String, Object> old = new HashMap<>();
//        String rowId = parseRowId(update.getWhere());
        // 添加ROWID
        old.put(getPkName(), pkValue);
        sqlTypeMap.put(getPkName(), Types.CHAR);
//        old.put(getPkName(), rowId);
        
        sqlRedoObj.setData(Collections.singletonList(data));
        sqlRedoObj.setOld(Collections.singletonList(old));
        sqlRedoObj.setPkNames(Collections.singletonList(getPkName()));
        sqlRedoObj.setSqlType(sqlTypeMap);
        return sqlRedoObj;
    }

    private SqlRedoObj sqlParserDelete(SqlRedoObj sqlRedoObj, String sql, String pkValue, Map<String, String> columnTypeMap) throws Exception {
        SqlParserPlus parser = new SqlParserPlus();
        Statement stmt = parser.parse(DbType.ORACLE, sql);
        Delete delete = (Delete) stmt;
        //获取表信息
        DbType dbType = getDbType();

        Table table = delete.getTable();
        sqlRedoObj.setType("DELETE");
        sqlRedoObj.setEmpty(false);
        sqlRedoObj.setIsDdl(false);
        sqlRedoObj.setSchema(table.getSchemaName());
        sqlRedoObj.setTable(table.getName());
        sqlRedoObj.setDatabase(table.getDatabase().getDatabaseName());
        sqlRedoObj.setDbType(dbType.getDb().toUpperCase());
        Map<String, Integer> sqlTypeMap = new HashMap<>();
        // 原数据
        Map<String, Object> old = new HashMap<>();
//        String rowId = parseRowId(delete.getWhere());
        old.put(getPkName(), pkValue);
        sqlTypeMap.put(getPkName(), Types.CHAR);
//        old.put(getPkName(), rowId);
        sqlRedoObj.setData(Collections.emptyList());
        sqlRedoObj.setOld(Collections.singletonList(old));
        sqlRedoObj.setPkNames(Collections.singletonList(getPkName()));
        sqlRedoObj.setSqlType(sqlTypeMap);
        return sqlRedoObj;
    }
    
    private String parseRowId(Expression expression) {
        String rowId = null;
        if (expression instanceof AndExpression) {
            rowId = parseRowId(((AndExpression) expression).getLeftExpression());
            if (rowId == null) {
                rowId = parseRowId(((AndExpression) expression).getRightExpression());
            }
        } else if (expression instanceof OrExpression) {
            rowId = parseRowId(((OrExpression) expression).getLeftExpression());
            if (rowId == null) {
                rowId = parseRowId(((OrExpression) expression).getRightExpression());
            }
        } else {
            if (expression instanceof BinaryExpression) {
                if (((BinaryExpression) expression).getLeftExpression().toString().equals(getPkName())) {
                    rowId = ((BinaryExpression) expression).getRightExpression().toString();
                }
            } else if (expression instanceof Parenthesis) {
                rowId = parseRowId(((Parenthesis) expression).getExpression());
            }
        }
        return rowId;
    }
}
