package com.sdy.dataexchange.plugin.common;

import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;

import java.util.List;
import java.util.Map;

public interface SqlParser {
    /**
     * 将原始对象解析成公用的SqlRedoObj对象
     * @param str
     * @return
     */
    List<SqlRedoObj> parse(String str, Map<String, String> columnTypeMap) throws Exception;

    /**
     * 获取数据库类型
     * @return
     */
    DbType getDbType();

    /**
     * 获取主键名称
     * @return
     */
    String getPkName();

    /**
     * 函数解析
     */
    default String functionConverse(String v) {
        return v;
    }

    /**
     * 获取类型转换器
     */
    ITypeConvert getColumnConverter();
}
