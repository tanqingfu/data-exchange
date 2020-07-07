package com.sdy.dataexchange.plugin.sqlserver;

import com.sdy.dataexchange.plugin.cmdb.BaseGeneralSqlRedoFactory;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.converts.SqlServerTypeConvert;

/**
 * mysql语句重建工厂
 * @author zhouziqiang 
 */
public class SqlServerSqlRedoFactory extends BaseGeneralSqlRedoFactory {
    private static final String INSERT_TEMPLATE = "insert into %s (%s) values (%s);";
    private static final String UPDATE_TEMPLATE = "update %s set %s %s limit 1;";
    private static final String DELETE_TEMPLATE = "delete from %s %s;";

    private static ITypeConvert sqlServerTypeConvert = new SqlServerTypeConvert();

    @Override
    public String formatInsertSql(String db, String schema, String table, String fieldStr, String valueStr) {
        return String.format(INSERT_TEMPLATE, table, fieldStr, valueStr);
    }

    @Override
    public String formatUpdateSql(String db, String schema, String table, String setStr, String whereStr) {
        return String.format(UPDATE_TEMPLATE, table, setStr, whereStr);
    }

    @Override
    public String formatDeleteSql(String db, String schema, String table, String whereStr) {
        return String.format(DELETE_TEMPLATE, table, whereStr);
    }

    @Override
    public ITypeConvert getColumnConverter() {
        return sqlServerTypeConvert;
    }

    public static void main(String[] args) {
        System.out.println(String.format("insert into %s (%s) values (%s);", "1", "1", "1"));
    }
}
