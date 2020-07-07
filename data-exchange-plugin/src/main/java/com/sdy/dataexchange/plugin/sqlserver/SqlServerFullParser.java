package com.sdy.dataexchange.plugin.sqlserver;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.SqlParser;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.mvc.utils.JsonUtil;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SqlServerFullParser implements SqlParser {

    @Override
    public List<SqlRedoObj> parse(String str, Map<String, String> columnTypeMap) throws Exception {
        SqlRedoObj sqlRedoObj = JsonUtil.fromJson(str, SqlRedoObj.class);
        sqlRedoObj.setDbType(DbType.SQL_SERVER.getDb().toUpperCase());
        return Collections.singletonList(sqlRedoObj);
    }

    @Override
    public DbType getDbType() {
        return DbType.SQL_SERVER;
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
