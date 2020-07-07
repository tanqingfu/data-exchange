package com.sdy.dataexchange.plugin.mysql;

import com.sdy.dataexchange.plugin.cmdb.BaseGeneralSqlRedoFactory;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.converts.MySqlTypeConvert;

/**
 * mysql语句重建工厂
 * @author zhouziqiang 
 */
public class MysqlSqlRedoFactory extends BaseGeneralSqlRedoFactory {
    private static final String UPDATE_TEMPLATE = "update %s.%s set %s %s limit 1;";

    private static ITypeConvert mySqlTypeConvert = new MySqlTypeConvert();
    
    @Override
    public String formatUpdateSql(String db, String schema, String table, String setStr, String whereStr) {
        return String.format(UPDATE_TEMPLATE, schema, table, setStr, whereStr);
    }
    
    @Override
    public ITypeConvert getColumnConverter() {
        return mySqlTypeConvert;
    }
}
