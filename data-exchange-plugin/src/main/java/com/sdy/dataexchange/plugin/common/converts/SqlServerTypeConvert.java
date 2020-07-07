package com.sdy.dataexchange.plugin.common.converts;

import com.google.common.primitives.Longs;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.EncodeUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.plugin.common.DbColumnType;
import com.sdy.dataexchange.plugin.common.IColumnType;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.SqlParserUtil;
import com.sdy.dataexchange.plugin.common.SqlTypes;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * SQLSERVER 数据库字段类型转换
 * </p>
 */
public class SqlServerTypeConvert implements ITypeConvert {

    @Override
    public IColumnType processTypeConvert(String fieldType) {
        return DbColumnType.STRING;
    }

    private static final Map<String, Integer> SQL_TYPE_MAP = new HashMap<>(64);

    static {
        SQL_TYPE_MAP.put("TINYINT", SqlTypes.INTEGER);
        SQL_TYPE_MAP.put("SMALLINT", SqlTypes.INTEGER);
        SQL_TYPE_MAP.put("INT", SqlTypes.INTEGER);
        SQL_TYPE_MAP.put("BIGINT", SqlTypes.LONG);
        SQL_TYPE_MAP.put("BIT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("DECIMAL", SqlTypes.BIG_DECIMAL);
        SQL_TYPE_MAP.put("REAL", SqlTypes.DOUBLE);
        SQL_TYPE_MAP.put("SMALLMONEY", SqlTypes.DOUBLE);
        SQL_TYPE_MAP.put("MONEY", SqlTypes.DOUBLE);
        SQL_TYPE_MAP.put("NUMERIC", SqlTypes.DOUBLE);
        SQL_TYPE_MAP.put("CHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("NCHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("VARCHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("NVARCHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("TEXT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("NTEXT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("SYSNAME", SqlTypes.STRING);
        SQL_TYPE_MAP.put("UNIQUEIDENTIFIER", SqlTypes.STRING);
        SQL_TYPE_MAP.put("SQL_VARIANT", SqlTypes.STRING);
        SQL_TYPE_MAP.put("SMALLDATETIME", SqlTypes.DATE_TIME);
        SQL_TYPE_MAP.put("DATE", SqlTypes.DATE);
        SQL_TYPE_MAP.put("DATETIME", SqlTypes.DATE_TIME3);
        SQL_TYPE_MAP.put("DATETIME2", SqlTypes.DATE_TIME7);
        SQL_TYPE_MAP.put("DATETIMEOFFSET", SqlTypes.DATE_TIME7);
        SQL_TYPE_MAP.put("TIME", SqlTypes.TIME);
        SQL_TYPE_MAP.put("TIMESTAMP", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("VARBINARY", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("BINARY", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("GEOGRAPHY", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("GEOMETRY", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("HIERARCHYID", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("IMAGE", SqlTypes.BYTE_ARRAY);
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
                    result = ((Boolean) value) ? 1 : 0;
                    break;
                case "YEAR":
                    result = value.toString().substring(0, 4);
                    break;
                case "TIME":
                case "DATE":
                case "DATETIME":
                case "SMALLDATETIME":
                case "DATETIME2":
                case "DATETIMEOFFSET": {
                    result = SqlParserUtil.formatTimestamp((Timestamp) value);
                    break;
                }
                case "TIMESTAMP":
                    result = SqlParserUtil.formatTimestamp((byte[]) value);
                    break;
                case "DOUBLE":
                case "REAL":
                case "MONEY":
                case "SMALLMONEY":
                case "NUMERIC":
                case "DECIMAL":
                    result = value.toString();
                    break;
                case "VARBINARY":
                case "BINARY":
                case "GEOGRAPHY":
                case "GEOMETRY":
                case "HIERARCHYID":
                case "IMAGE": {
                    result = EncodeUtil.bytesToHexString((byte[]) value);
                    break;
                }
                case "CHAR":
                case "NCHAR":
                case "NTEXT":{
                    // 去除定长字符串后面的空格
                    result = value.toString();
                    break;
                }
                default:
                    result = value;
            }
        } else if (srcType.equals(2)) {
            // 来源?
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
        if ("null".equalsIgnoreCase(value.toString())) {
            return value.toString();
        }
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
                result = "0x" + v;
                break;
            case SqlTypes.TIME:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "HH:mm:ss"));
                } else {
                    result = convertToString(SqlParserUtil.formatOnlyTimeStr(value.toString(), 7));
                }
                break;
            case SqlTypes.DATE:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "yyyy-MM-dd"));
                } else {
                    result = convertToString(SqlParserUtil.formatDateStr(value.toString()));
                }
                break;
            case SqlTypes.DATE_TIME:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "yyyy-MM-dd HH:mm:ss"));
                } else {
                    result = convertToString(SqlParserUtil.formatTimestr(value.toString(), 0));
                }
                break;
            case SqlTypes.DATE_TIME3:
                if (value instanceof Date) {
                    result = convertToString(DateUtil.getDate((Date) value, "yyyy-MM-dd HH:mm:ss.SSS"));
                } else {
                    result = convertToString(SqlParserUtil.formatTimestr(value.toString(), 3));
                }
                break;
            case SqlTypes.DATE_TIME7:
                if (value instanceof Date) {
                    result = convertToString(SqlParserUtil.formatTimestr(DateUtil.getDate((Date) value, "yyyy-MM-dd HH:mm:ss.SSS"), 7));
                } else {
                    result = convertToString(SqlParserUtil.formatTimestr(value.toString(), 7));
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
