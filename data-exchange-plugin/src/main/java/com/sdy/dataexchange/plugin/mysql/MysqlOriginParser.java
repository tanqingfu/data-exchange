package com.sdy.dataexchange.plugin.mysql;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.SqlParser;
import com.sdy.dataexchange.plugin.common.SqlParserUtil;
import com.sdy.dataexchange.plugin.common.converts.MySqlTypeConvert;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.mvc.utils.JsonUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MysqlOriginParser implements SqlParser {
    private static ITypeConvert typeConvert = new MySqlTypeConvert();

    @Override
    public List<SqlRedoObj> parse(String str, Map<String, String> columnTypeMap) throws Exception {
        SqlRedoObj sqlRedoObj = JsonUtil.fromJson(str, SqlRedoObj.class);
        sqlRedoObj.setDbType(DbType.MYSQL.getDb().toUpperCase());
        if (sqlRedoObj.getEmpty() == null) {
            sqlRedoObj.setEmpty(false);
        }
        if (sqlRedoObj.getMysqlType() == null) {
            sqlRedoObj.setMysqlType(Collections.emptyMap());
        }
        Map<String, Integer> tm = new HashMap<>();
        sqlRedoObj.getMysqlType().forEach((k, v) -> {
            String newv = SqlParserUtil.convertRawtypeToStandard(v);
            sqlRedoObj.getMysqlType().put(k, newv);
            tm.put(k, typeConvert.processSqlTypeConvert(newv));
        });
        sqlRedoObj.setSqlType(tm);
        if (sqlRedoObj.getData() != null) {
            sqlRedoObj.getData().forEach(dataMap -> {
                dataMap.forEach((k, v) -> {
                    dataMap.put(k, typeConvert.formatValue(v, sqlRedoObj.getMysqlType().getOrDefault(k, "VARCHAR"), 2));
                });
            });
        }
        if (sqlRedoObj.getOld() != null) {
            sqlRedoObj.getOld().forEach(dataMap -> {
                dataMap.forEach((k, v) -> {
                    dataMap.put(k, typeConvert.formatValue(v, sqlRedoObj.getMysqlType().getOrDefault(k, "VARCHAR"), 2));
                });
            });
        }
        return Collections.singletonList(sqlRedoObj);
    }

    @Override
    public DbType getDbType() {
        return DbType.MYSQL;
    }

    @Override
    public String getPkName() {
        return "ROWID";
    }

    @Override
    public ITypeConvert getColumnConverter() {
        return null;
    }
}
