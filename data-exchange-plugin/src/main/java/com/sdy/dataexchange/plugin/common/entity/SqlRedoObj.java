package com.sdy.dataexchange.plugin.common.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

/**
 * 重建语句对象
 * @author zhouziqiang 
 */
@Data
@Accessors(chain = true)
public class SqlRedoObj {
    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 数据库类型 ORACLE MYSQL
     */
    private String dbType;
    /**
     * 数据库类型 ORACLE MYSQL
     */
    private String targetDbType;
    /**
     * 模式名
     */
    private String schema;
    /**
     * 数据库名
     */
    private String database;
    /**
     * 表名
     */
    private String table;
    /**
     * 语句类型
     * INSERT UPDATE DELETE CREATE ALTER
     * REPLACE
     */
    private String type;
    /**
     * 主键列表
     */
    private List<String> pkNames;
    /**
     * 是否ddl语句
     */
    private Boolean isDdl;
    /**
     * 时间戳
     */
    private Long ts;
    /**
     * sql数据
     */
    private List<Map<String, Object>> data;
    /**
     * 原sql数据
     */
    private List<Map<String, Object>> old;
    /**
     * 是否是空包
     */
    private Boolean empty;
    /**
     * 是否结束(全量同步的时候用)
     */
    private Boolean end;

    /**
     * 字段类型
     */
    private Map<String, String> mysqlType;

    /**
     * 字段类型
     * @see java.sql.Types
     */
    private Map<String, Integer> sqlType;
    
    public String toIdentifiedString() {
        StringBuilder s = new StringBuilder("Pk=[");
        if (data != null) {
            for (Map<String, Object> d : data) {
                if (pkNames != null) {
                    for (String pk : pkNames) {
                        Object pkValue = d.get(pk);
                        s.append(pk).append(":").append(pkValue == null ? "null" : pkValue.toString()).append(",");
                    }
                }
            }
        }
        s.append("]");
        return s.toString();
    }
}
