package com.sdy.dataexchange.plugin.oracle;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.SqlParser;
import com.sdy.dataexchange.plugin.common.converts.OracleTypeConvert;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.mvc.utils.JsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class OracleFullParser implements SqlParser {
    private static ITypeConvert oracleConvert = new OracleTypeConvert();

    @Override
    public List<SqlRedoObj> parse(String str, Map<String, String> columnTypeMap) throws Exception {
        SqlRedoObj sqlRedoObj = JsonUtil.fromJson(str, SqlRedoObj.class);
        sqlRedoObj.setDbType(DbType.ORACLE.getDb().toUpperCase());
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
        return oracleConvert;
    }
}
