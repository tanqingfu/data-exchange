package com.sdy.dataexchange.plugin.common;

import com.google.common.primitives.Longs;
import com.sdy.common.utils.DateUtil;
import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.plugin.oracle.OracleFunctionResolverFactory;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.ArrayUtils;

import java.io.StringReader;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SqlParserUtil {
    /**
     * 例如：varchar(255) -> VARCHAR
     * @return
     */
    public static String convertRawtypeToStandard(String t) {
        int j = t.indexOf('(');
        if (j > 0) {
            return t.substring(0, j).toUpperCase();
        }
        return t.toUpperCase();
    }
    
    private static final String[] TS_NANO_SUFFIX = new String[]{"", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000", "000000000"};

    /**
     * 格式化时间戳
     * java.sql.Timestamp -> String(eg."2019-11-01 11:35:06.123456789")
     */
    public static String formatTimestamp(Timestamp ts) {
        if (ts == null) {
            return null;
        }
        return formatTimestr(ts.toString());
    }

    /**
     * 格式化时间戳
     * java.sql.Timestamp -> String(eg."2019-11-01 11:35:06.123456789")
     */
    public static String formatTimestamp(byte[] ts) {
        if (ts == null) {
            return null;
        }
        return DateUtil.getDate(new Date(Longs.fromByteArray(ts)), "yyyy-MM-dd HH:mm:ss.SSS");
    }
    
    public static String formatTime(Time ts) {
        if (ts == null) {
            return null;
        }
        return formatTimestr("1970-01-01 " + ts.toString());
    }

    public static String formatTimestr(String str) {
        return formatTimestr(str, 9);
    }

    public static String formatTimestr(String str, int milliCnt) {
        int len = str.length();
        if (len == 4) {
            str = str.concat("-01-01 00:00:00");
            len = 19;
        } else if (len == 10) {
            str = str.concat(" 00:00:00");
            len = 19;
        }
        if (str.charAt(2) == ':' && str.charAt(5) == ':' && len == 8) {
            str = "1970-01-01 ".concat(str);
            len = 19;
        }
        if (str.length() < 19) {
            throw new RuntimeException("时间格式不正确：" + str);
        }
        if (milliCnt == 0) {
            return str.substring(0, 19);
        }
        if (len == 19) {
            str = str.concat(".").concat(TS_NANO_SUFFIX[milliCnt]);
        } else if (len <= 29) {
            str = str.concat(TS_NANO_SUFFIX[29 - len]).substring(0, 20 + milliCnt);
        }
        return str;
    }

    /**
     * 转为仅日期格式
     */
    public static String formatDateStr(String str) {
        return formatTimestr(str, 0).substring(0, 10);
    }

    /**
     * 转为仅时间格式
     */
    public static String formatOnlyTimeStr(String str, int milliCnt) {
        if (milliCnt == 0) {
            return formatTimestr(str, 0).substring(11, 19);
        }
        return formatTimestr(str, 9).substring(11, 20 + Math.min(milliCnt, 9));
    }

    /**
     * 转为仅时间格式
     */
    public static String formatOnlyTimeStr(String str) {
        return formatOnlyTimeStr(str, 0);
    }

    public static void main(String[] args) throws JSQLParserException {
        SqlParserPlus parser = new SqlParserPlus();
        // UPDATE "TEST"."SYS_USER" SET "NAME" = '\' WHERE "ID_CARD" = '330600000000000529';
        Statement stmt = parser.parse(DbType.ORACLE, "UPDATE SYS_USER SET NAME = '\\\\' WHERE ID_CARD = '330600000000000529';");
        Update update = (Update) stmt;
        List<Object> valueList = new ArrayList<>();
        update.getExpressions().forEach(expression -> valueList.add(OracleFunctionResolverFactory.StringRsv.getInstance().resolve(expression.toString().trim())));
        int a = 1;
    }

    /**
     * 格式化栈异常信息
     */
    public static String throwableToString(Throwable t) {
        StringBuilder sb = new StringBuilder("## ");
        Throwable tmp = t;
        int maxCnt = 10;
        do {
            sb.append(tmp.getMessage()).append(" ## ");
        } while ((tmp = tmp.getCause()) != null && --maxCnt > 0);
        return sb.toString();
    }

    /**
     * 拆分字符串
     * @param str
     * @return
     */
    public static List<String> splitMysqlMonitorStr(String str, int maxPackSize) {
        List<String> splitList = new ArrayList<>();
        int len = str.length();
        int packCount = (len - 1) / maxPackSize + 1;
        for (int i = 0; i < packCount; i++) {
            String newStr = str.substring(i * maxPackSize, Math.min((i + 1) * maxPackSize, len));
            splitList.add(newStr);
        }
        return splitList;
    }

    /**
     * 拆分字节
     */
    public static List<byte[]> splitMysqlMonitorByte(byte[] str, int maxPackSize) {
        List<byte[]> splitList = new ArrayList<>();
        int len = str.length;
        int packCount = (len - 1) / maxPackSize + 1;
        for (int i = 0; i < packCount; i++) {
            byte[] newStr = ArrayUtils.subarray(str, i * maxPackSize, Math.min((i + 1) * maxPackSize, len));
            splitList.add(newStr);
        }
        return splitList;
    }
}
