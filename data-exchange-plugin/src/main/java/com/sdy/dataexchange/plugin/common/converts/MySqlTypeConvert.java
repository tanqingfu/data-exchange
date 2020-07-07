package com.sdy.dataexchange.plugin.common.converts;

import com.google.common.primitives.Longs;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.EncodeUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.core.util.CacheUtil;
import com.sdy.dataexchange.plugin.common.DbColumnType;
import com.sdy.dataexchange.plugin.common.IColumnType;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.SqlParserUtil;
import com.sdy.dataexchange.plugin.common.SqlTypes;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * MYSQL 数据库字段类型转换
 * </p>
 */
public class MySqlTypeConvert implements ITypeConvert {

    @Override
    public IColumnType processTypeConvert(String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains("char")) {
            return DbColumnType.STRING;
        } else if (t.contains("bigint")) {
            return DbColumnType.LONG;
        } else if (t.contains("tinyint(1)")) {
            return DbColumnType.BOOLEAN;
        } else if (t.contains("int")) {
            return DbColumnType.INTEGER;
        } else if (t.contains("text")) {
            return DbColumnType.STRING;
        } else if (t.contains("bit")) {
            return DbColumnType.BOOLEAN;
        } else if (t.contains("decimal")) {
            return DbColumnType.BIG_DECIMAL;
        } else if (t.contains("clob")) {
            return DbColumnType.CLOB;
        } else if (t.contains("blob")) {
            return DbColumnType.BLOB;
        } else if (t.contains("binary")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("double")) {
            return DbColumnType.DOUBLE;
        } else if (t.contains("json") || t.contains("enum")) {
            return DbColumnType.STRING;
        } else if (t.contains("date") || t.contains("time") || t.contains("year")) {
            return DbColumnType.DATE;
        }
        return DbColumnType.STRING;
    }
    
    private static final Map<String, Integer> SQL_TYPE_MAP = new HashMap<>(64);

    static {
        SQL_TYPE_MAP.put("BIGINT", SqlTypes.LONG);
        SQL_TYPE_MAP.put("BINARY", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("BIT", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("BLOB", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("CHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("DATE", SqlTypes.DATE);
        SQL_TYPE_MAP.put("DATETIME", SqlTypes.DATE_TIME);
        SQL_TYPE_MAP.put("DECIMAL", SqlTypes.BIG_DECIMAL);
        SQL_TYPE_MAP.put("DOUBLE", SqlTypes.DOUBLE);
        SQL_TYPE_MAP.put("FLOAT", SqlTypes.FLOAT);
        SQL_TYPE_MAP.put("ENUM", SqlTypes.STRING);
        SQL_TYPE_MAP.put("GEOMETRY", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("GEOMCOLLECTION", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("INT", SqlTypes.INTEGER);
        SQL_TYPE_MAP.put("JSON", SqlTypes.STRING);
        SQL_TYPE_MAP.put("LINESTRING", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("LONGBLOB", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("LONGTEXT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("MEDIUMBLOB", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("MEDIUMINT", SqlTypes.LONG);
        SQL_TYPE_MAP.put("MEDIUMTEXT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("MULTILINESTRING", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("MULTIPOINT", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("MULTIPOLYGON", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("POINT", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("POLYGON", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("SET", SqlTypes.STRING);
        SQL_TYPE_MAP.put("SMALLINT", SqlTypes.INTEGER);
        SQL_TYPE_MAP.put("TEXT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("TIME", SqlTypes.TIME);
        SQL_TYPE_MAP.put("TIMESTAMP", SqlTypes.DATE_TIME);
        SQL_TYPE_MAP.put("TINYBLOB", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("TINYINT", SqlTypes.INTEGER);
        SQL_TYPE_MAP.put("TINYTEXT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("VARBINARY", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("VARCHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("YEAR", SqlTypes.YEAR);
    }

    @Override
    public Integer processSqlTypeConvert(String fieldType) {
        return processSqlTypeConvert(fieldType, SQL_TYPE_MAP, "processSqlTypeConvert_" + this.getClass().getSimpleName());
    }

    @Override
    public Object formatValue(Object value, String originType, Integer srcType) {
        if (value == null) {
            return null;
        }
        Object result;
        if (srcType.equals(1)) {
            // 来源JdbcTemplate
            switch (originType) {
                case "BIT":
                    result = EncodeUtil.bytesToHexString((byte[]) value);
                    break;
                case "YEAR":
                    result = value.toString().substring(0, 4);
                    break;
                case "TIME":
                case "DATE":
                case "TIMESTAMP":
                case "DATETIME": {
                    result = SqlParserUtil.formatTimestamp((Timestamp) value);
                    break;
                }
                case "DOUBLE":
                case "FLOAT":
                case "DECIMAL":
                    result = value.toString();
                    break;
                case "LONGBLOB":
                case "MEDIUMBLOB":
                case "TINYBLOB":
                case "GEOMETRY":
                case "BINARY":
                case "BLOB": {
                    result = EncodeUtil.bytesToHexString((byte[]) value);
                    break;
                }
                case "CHAR":{
                    // 去除定长字符串后面的空格
                    result = value.toString();
                    break;
                }
                default:
                    result = value;
            }
        } else if (srcType.equals(2)) {
            // 来源canal
            switch (originType) {
                case "BIT":
                    result = EncodeUtil.bytesToHexString(Longs.toByteArray(Long.valueOf((String) value)));
                    break;
                case "YEAR":
                    result = value.toString().substring(0, 4);
                    break;
                case "DATE":
                case "TIME":
                case "TIMESTAMP":
                case "DATETIME": {
                    result = SqlParserUtil.formatTimestr(value.toString());
                    break;
                }
                case "DOUBLE":
                case "FLOAT":
                case "DECIMAL":
                    result = value.toString();
                    break;
                case "LONGBLOB":
                case "MEDIUMBLOB":
                case "TINYBLOB":
                case "GEOMETRY":
                case "BINARY":
                case "BLOB": {
                    char[] s = value.toString().toCharArray();
                    byte[] bytes = new byte[s.length];
                    for (int i = 0; i < s.length; i++) {
                        bytes[i] = (byte) s[i];
                    }
                    result = EncodeUtil.bytesToHexString(bytes);
                    break;
                }
                default:
                    result = value;
            }
        } else {
            result = value;
        }
        return result;
    }
    /**
     * 字段写入
     */
    public static String decorateValue(Object value, int sqlType) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String && StringUtil.isBlank(value)) {
            return "''";
        }
//        if ("null".equalsIgnoreCase(value.toString())) {
//            return value.toString();
//        }
        String result;
        switch (sqlType) {
            case SqlTypes.STRING:
                result = convertToString(value);
                break;
            case SqlTypes.INTEGER:
            case SqlTypes.LONG:
            case SqlTypes.FLOAT:
            case SqlTypes.DOUBLE:
            case SqlTypes.BIG_DECIMAL:
                result = value.toString();
                break;
            case SqlTypes.BYTE_ARRAY:
                String v = value.toString();
                if (v.length() % 2 == 1) {
                    v = "0".concat(v);
                }
                result = "x".concat(convertToString(v));
                break;
            case SqlTypes.TIME:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "HH:mm:ss"));
                } else {
                    result = convertToString(SqlParserUtil.formatOnlyTimeStr(value.toString()));
                }
                break;
            case SqlTypes.DATE:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "yyyy-MM-dd"));
                } else {
                    result = convertToString(SqlParserUtil.formatDateStr(value.toString()));
                }
                break;
            case SqlTypes.TIMESTAMP_TZ:
            case SqlTypes.TIMESTAMP:
            case SqlTypes.DATE_TIME:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "yyyy-MM-dd HH:mm:ss"));
                } else {
                    result = convertToString(SqlParserUtil.formatTimestr(value.toString(), 0));
                }
                break;
            case SqlTypes.YEAR:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "yyyy"));
                } else {
                    result = convertToString(value.toString());
                }
                break;
            default:
                result = convertToString(value);
        }
        return result;
    }

    private static String convertFromQuote(String value) {
        return value.replace("\\", "\\\\").replace("'", "\\'");
    }

    private static String convertToString(Object value) {
        return  "'".concat(convertFromQuote(value.toString())).concat("'");
    }
}
