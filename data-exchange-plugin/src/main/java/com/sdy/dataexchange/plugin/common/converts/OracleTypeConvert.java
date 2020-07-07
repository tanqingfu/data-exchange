package com.sdy.dataexchange.plugin.common.converts;


import com.baomidou.mybatisplus.annotation.SqlParser;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.EncodeUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.core.util.CacheUtil;
import com.sdy.dataexchange.plugin.common.DbColumnType;
import com.sdy.dataexchange.plugin.common.FunctionResolver;
import com.sdy.dataexchange.plugin.common.IColumnType;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.SqlParserUtil;
import com.sdy.dataexchange.plugin.common.SqlTypes;
import com.sdy.dataexchange.plugin.oracle.OracleFunctionResolverFactory;
import oracle.sql.TIMESTAMP;
import oracle.sql.TIMESTAMPLTZ;
import oracle.sql.TIMESTAMPTZ;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * <p>
 * ORACLE 字段类型转换
 * </p>
 */
public class OracleTypeConvert implements ITypeConvert {

    @Override
    public IColumnType processTypeConvert(String fieldType) {
        String t = fieldType.toLowerCase();
        if (t.contains("char")) {
            return DbColumnType.STRING;
        } else if (t.contains("date") || t.contains("timestamp")) {
            return DbColumnType.DATE;
        } else if (t.contains("number")) {
            if (t.matches("number\\(+\\d\\)")) {
                return DbColumnType.INTEGER;
            } else if (t.matches("number\\(+\\d{2}+\\)")) {
                return DbColumnType.LONG;
            }
            return DbColumnType.DOUBLE;
        } else if (t.contains("float")) {
            return DbColumnType.FLOAT;
        } else if (t.contains("clob")) {
            return DbColumnType.CLOB;
        } else if (t.contains("blob")) {
            return DbColumnType.BLOB;
        } else if (t.contains("binary")) {
            return DbColumnType.BYTE_ARRAY;
        } else if (t.contains("raw")) {
            return DbColumnType.BYTE_ARRAY;
        }
        return DbColumnType.STRING;
    }

    /**
     * BLOB:2004
     * CHAR:1
     * VARCHAR2:12
     * CHAR:1
     * VARCHAR2:12
     * CLOB:2005
     * DATE:93
     * NUMBER:2
     * NUMBER:2
     * NUMBER:2
     * NUMBER:2
     * NUMBER:2
     * LONG:-1
     * NCHAR:-15
     * NVARCHAR2:-9
     * NCHAR:-15
     * NVARCHAR2:-9
     * NCHAR:-15
     * NVARCHAR2:-9
     * NCLOB:2011
     * NUMBER:2
     * NUMBER:2
     * NVARCHAR2:-9
     * RAW:-3
     * NUMBER:2
     * ROWID:-8
     * NUMBER:2
     * TIMESTAMP:93
     * TIMESTAMP WITH LOCAL TIME ZONE:-102
     * TIMESTAMP WITH TIME ZONE:-101
     * null:1111
     * VARCHAR2:12
     * VARCHAR2:12
     * BFILE:-13
     * BINARY_DOUBLE:101
     * BINARY_FLOAT:100
     * INTERVALDS:-104
     * INTERVALYM:-103
     * ROWID:-8
     */
    private static final Map<String, Integer> SQL_TYPE_MAP = new HashMap<>();

    static {
        // BIGINT
        SQL_TYPE_MAP.put("BLOB", SqlTypes.BLOB);
        SQL_TYPE_MAP.put("CHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("VARCHAR2", SqlTypes.STRING);
        SQL_TYPE_MAP.put("CLOB", SqlTypes.CLOB);
        SQL_TYPE_MAP.put("DATE", SqlTypes.DATE_TIME);
        SQL_TYPE_MAP.put("NUMBER", SqlTypes.LONG);
        SQL_TYPE_MAP.put("DOUBLE", SqlTypes.DOUBLE);
        SQL_TYPE_MAP.put("FLOAT", SqlTypes.FLOAT);
        SQL_TYPE_MAP.put("LONG", SqlTypes.STRING);
        SQL_TYPE_MAP.put("NCHAR", SqlTypes.STRING);
        SQL_TYPE_MAP.put("NVARCHAR2", SqlTypes.STRING);
        SQL_TYPE_MAP.put("NCLOB", SqlTypes.CLOB);
        SQL_TYPE_MAP.put("RAW", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("ROWID", SqlTypes.STRING);
        SQL_TYPE_MAP.put("TIMESTAMP", SqlTypes.TIMESTAMP);
        SQL_TYPE_MAP.put("TIMESTAMP WITH LOCAL TIME ZONE", SqlTypes.TIMESTAMP);
        SQL_TYPE_MAP.put("TIMESTAMP WITH TIME ZONE", SqlTypes.TIMESTAMP_TZ);
        SQL_TYPE_MAP.put("BFILE", SqlTypes.BYTE_ARRAY);
        SQL_TYPE_MAP.put("BINARY_DOUBLE", SqlTypes.DOUBLE);
        SQL_TYPE_MAP.put("BINARY_FLOAT", SqlTypes.FLOAT);
        SQL_TYPE_MAP.put("INTERVALDS", SqlTypes.STRING);
        SQL_TYPE_MAP.put("INTERVALYM", SqlTypes.STRING);
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
                case "TIMESTAMP WITH TIME ZONE":
                case "TIMESTAMP WITH LOCAL TIME ZONE":
                case "TIMESTAMP":
                case "DATE":
                    result = SqlParserUtil.formatTimestamp((Timestamp) value);
                    break;
                case "DOUBLE":
                case "FLOAT":
                case "BINARY_DOUBLE":
                case "BINARY_FLOAT":
                case "NUMBER":
                    result = value.toString();
                    break;
                case "ROWID":
                    result = new String(((RowId) value).getBytes());
                    break;
                case "RAW":
                    result = EncodeUtil.bytesToHexString((byte[]) value);
                    break;
                case "BLOB":
                    result = EncodeUtil.bytesToHexString(blobToBytes((Blob) value));
                    break;
                case "CLOB":
                case "NCLOB":
                    result = clobToString((Clob) value);
                    break;
                case "CHAR":
                case "NCHAR": {
                    // 去除定长字符串后面的空格
                    result = value.toString();
                    break;
                }
                default:
                    result = value;
            }
        } else if (srcType.equals(2)) {
            // MonitorResult
            if ("null".equalsIgnoreCase(value.toString())) {
                return null;
            }
            // 先去除value中的单引号
            FunctionResolver stringRsv = OracleFunctionResolverFactory.StringRsv.getInstance();
            if (value instanceof String && stringRsv.check((String) value)) {
                value = stringRsv.resolve((String) value);
            }
            switch (originType) {
                case "NCHAR": {
                    FunctionResolver rsv = OracleFunctionResolverFactory.UnistrRsv.getInstance();
                    if (rsv.check((String) value)) {
                        result = rsv.resolve((String) value);
                    } else {
                        result = value.toString();
                    }
                    result = (String) result;
                    break;
                }
                case "NVARCHAR2": {
                    FunctionResolver rsv = OracleFunctionResolverFactory.UnistrRsv.getInstance();
                    if (rsv.check((String) value)) {
                        result = rsv.resolve((String) value);
                    } else {
                        result = value.toString();
                    }
                    break;
                }
                case "DATE": {
                    FunctionResolver rsv = OracleFunctionResolverFactory.DateRsv.getInstance();
                    if (rsv.check((String) value)) {
                        result = rsv.resolve((String) value);
                    } else {
                        result = value.toString();
                    }
                    break;
                }
                case "TIMESTAMP": {
                    FunctionResolver rsv1 = OracleFunctionResolverFactory.TimestampRsv.getInstance();
                    FunctionResolver rsv2 = OracleFunctionResolverFactory.TimestamptzRsv.getInstance();
                    if (rsv1.check((String) value)) {
                        result = rsv1.resolve((String) value);
                    } else if (rsv2.check((String) value)) {
                        result = rsv2.resolve((String) value);
                    } else {
                        result = value.toString();
                    }
                    break;
                }
                case "BLOB":{
                    FunctionResolver rsv1 = OracleFunctionResolverFactory.EmptyblobRsv.getInstance();
                    FunctionResolver rsv2 = OracleFunctionResolverFactory.HextorawRsv.getInstance();
                    if (rsv1.check((String) value)) {
                        result = rsv1.resolve((String) value);
                    } else if (rsv2.check((String) value)) {
                        result = rsv2.resolve((String) value);
                    } else {
                        result = value.toString();
                    }
                    break;
                }
                case "RAW": {
                    FunctionResolver rsv = OracleFunctionResolverFactory.HextorawRsv.getInstance();
                    if (rsv.check((String) value)) {
                        result = rsv.resolve((String) value);
                    } else {
                        result = value.toString();
                    }
                    break;
                }
                case "CLOB":
                case "NCLOB": {
                    FunctionResolver rsv = OracleFunctionResolverFactory.EmptyclobRsv.getInstance();
                    if (rsv.check((String) value)) {
                        result = rsv.resolve((String) value);
                    } else {
                        result = value.toString();
                    }
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
                result = "HEXTORAW('".concat((String) value).concat("')");
                break;
            case SqlTypes.DATE:
            case SqlTypes.DATE_TIME:
                result = "TO_DATE('" + SqlParserUtil.formatTimestr(value.toString(), 0) + "', 'YYYY-MM-DD HH24:MI:SS')";
                break;
            case SqlTypes.TIMESTAMP:
                String dateStr;
                if (value instanceof Long) {
                    dateStr = DateUtil.getDate(new Date((Long) value), "yyyy-MM-dd HH:mm:ss.SSS");
                } else {
                    dateStr = value.toString();
                }
                result = "TO_TIMESTAMP('" + SqlParserUtil.formatTimestr(dateStr, 6) + "', 'SYYYY-MM-DD HH24:MI:SS:FF6')";
                break;
            case SqlTypes.TIMESTAMP_TZ:
                result = "TO_TIMESTAMP_TZ('" + SqlParserUtil.formatTimestr(value.toString(), 6) + "', 'SYYYY-MM-DD HH24:MI:SS:FF6 TZR')";
                break;
            case SqlTypes.BLOB:
                if ("".equals(value) || value instanceof String && ((String) value).length() > 4000) {
                    result = "EMPTY_BLOB()";
                } else {
                    result = "HEXTORAW('".concat(value.toString()).concat("')");
                }
                break;
            case SqlTypes.CLOB:
                if ("".equals(value) || value instanceof String && ((String) value).length() > 4000) {
                    result = "EMPTY_CLOB()";
                } else {
                    result = convertToString(value);
                }
                break;
            default:
                result = convertToString(value);
        }
        return result;
    }
    
    private static String convertFromQuote(String value) {
        return value.replace("'", "''");
    }

    private static String convertToString(Object value) {
        return "'".concat(convertFromQuote(value.toString())).concat("'");
    }


    private String clobToString(Clob clob) {
        String res;
        try {
            if (clob == null || clob.getCharacterStream() == null) {
                return null;
            } else {
                Reader io = clob.getCharacterStream();
                try (BufferedReader br = new BufferedReader(io)) {
                    String s = br.readLine();
                    StringBuilder sb = new StringBuilder();
                    while (s != null) {
                        sb.append(s);
                        s = br.readLine();
                    }
                    res = sb.toString();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return res;
    }

    private byte[] blobToBytes(Blob blob) {
        byte[] b;
        try {
            try (InputStream is = blob.getBinaryStream()) {
                b = new byte[(int) blob.length()];
                is.read(b);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return b;
    }
}
