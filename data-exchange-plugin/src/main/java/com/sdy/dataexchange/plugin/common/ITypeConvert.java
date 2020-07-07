package com.sdy.dataexchange.plugin.common;

import com.sdy.dataexchange.core.util.CacheUtil;

import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * 数据库字段类型转换
 * </p>
 */
public interface ITypeConvert {


    /**
     * <p>
     * 执行类型转换
     * </p>
     *
     * @param fieldType    字段类型
     * @return
     */
    @Deprecated
    IColumnType processTypeConvert(String fieldType);
    
    Integer processSqlTypeConvert(String fieldType);

    /**
     * 转为可序列化的数据格式
     * @param value
     * @param originType
     * @param srcType 源数据来源 1-全量JdbcTemplate 2-增量MonitorResult/canal
     * @return
     */
    Object formatValue(Object value, String originType, Integer srcType);

    /**
     * 根据原始字段类型，获取程序中定义的数据类型
     */
    default Integer processSqlTypeConvert(String fieldType, Map<String, Integer> typeMap, String keyPrefix) {
        if (fieldType == null) {
            // 默认字符串类型，因为这时候映射不存在，所以反正也用不到
            return SqlTypes.STRING;
        }
        return CacheUtil.cacheProcessing("permanent", keyPrefix + "_" + fieldType, () -> {
            String upp = fieldType.toUpperCase();
            Optional<Map.Entry<String, Integer>> v = typeMap.entrySet().stream().filter(es -> upp.equals(es.getKey())).findAny();
            if (v.isPresent()) {
                return v.get().getValue();
            }
            v = typeMap.entrySet().stream().filter(es -> upp.startsWith(es.getKey())).findAny();
            if (v.isPresent()) {
                return v.get().getValue();
            }
            return SqlTypes.STRING;
        });
    }
}
