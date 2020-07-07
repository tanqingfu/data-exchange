package com.sdy.dataexchange.plugin.common.converts;


import com.sdy.dataexchange.plugin.common.DbColumnType;
import com.sdy.dataexchange.plugin.common.IColumnType;
import com.sdy.dataexchange.plugin.common.ITypeConvert;

/**
 * <p>
 * PostgreSQL 字段类型转换
 * </p>
 */
public class PostgreSqlTypeConvert implements ITypeConvert {

    @Override
    public IColumnType processTypeConvert(String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains("char")) {
            return DbColumnType.STRING;
        } else if (t.contains("bigint")) {
            return DbColumnType.LONG;
        } else if (t.contains("int")) {
            return DbColumnType.INTEGER;
        } else if (t.contains("date") || t.contains("time")) {
            return DbColumnType.DATE;
        } else if (t.contains("text")) {
            return DbColumnType.STRING;
        } else if (t.contains("bit")) {
            return DbColumnType.BOOLEAN;
        } else if (t.contains("decimal")) {
            return DbColumnType.BIG_DECIMAL;
        } else if (t.contains("clob")) {
            return DbColumnType.CLOB;
        } else if (t.contains("blob")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("double")) {
            return DbColumnType.DOUBLE;
        } else if (t.contains("json") || t.contains("enum")) {
            return DbColumnType.STRING;
        } else if (t.contains("boolean")) {
            return DbColumnType.BOOLEAN;
        }
        return DbColumnType.STRING;
    }

    @Override
    public Integer processSqlTypeConvert(String fieldType) {
        return null;
    }

    @Override
    public Object formatValue(Object value, String originType, Integer srcType) {
        return null;
    }

}
