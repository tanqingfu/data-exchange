package com.sdy.dataexchange.plugin.common;

import com.sdy.dataadapter.DbType;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;


public class SqlParserPlus {

    public Statement parse(DbType dbType, String sql) throws JSQLParserException {
        if (dbType.equals(DbType.ORACLE)) {
            sql = sql.replace("\\", "\\\\");
        }
        return CCJSqlParserUtil.parse(sql);
    }
}
