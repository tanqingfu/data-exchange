package com.sdy.dataexchange.plugin.oracle;

import com.sdy.dataexchange.plugin.common.FunctionResolver;
import com.sdy.dataexchange.plugin.common.SqlParserUtil;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * oracle函数解析
 * @author zhouziqiang 
 */
public class OracleFunctionResolverFactory {
    static abstract class AbstractOracleFunctionResolver implements FunctionResolver {
    }
    
//    public static final Map<Integer, FunctionResolver> FUNCTION_RESOLVER_MAP = new HashMap<>();
//    
//    static {
//        FUNCTION_RESOLVER_MAP.put(Types.NCHAR, UnistrRsv.getInstance());
//        FUNCTION_RESOLVER_MAP.put(Types.NVARCHAR, UnistrRsv.getInstance());
//        FUNCTION_RESOLVER_MAP.put(Types.LONGNVARCHAR, UnistrRsv.getInstance());
//        FUNCTION_RESOLVER_MAP.put(Types.CHAR, StringRsv.getInstance());
//    }
    
    public static final List<FunctionResolver> FUNCTION_RESOLVER_LIST = Arrays.asList(
            UnistrRsv.getInstance(),
            HextorawRsv.getInstance(),
            EmptyclobRsv.getInstance(),
            EmptyblobRsv.getInstance(),
            StringRsv.getInstance(),
            TimestampRsv.getInstance(),
            TimestamptzRsv.getInstance()
    );
    
    /**
     * unicode转中文 格式：\676D\5DDE
     */
    public static String unicodeToString(String str) {
        int len = str.length();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                if (i < len - 4) {
                    String tmps = str.substring(i + 1, i + 5);
                    if (checkHexStr(tmps)) {
                        sb.append((char) Integer.parseInt(tmps, 16));
                        i += 4;
                        continue;
                    }
                }
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否是hex字串
     */
    private static boolean checkHexStr(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')) {
                return false;
            }
        }
        return true;
    }

    /**
     * unicode解码
     * UnistrRsv('\676D\5DDE\5E02\5BCC\9')
     */
    public static class UnistrRsv implements FunctionResolver {
        private static final String FUNC_PREFIX = "UNISTR('";
        private static final String FUNC_SUFFIX = "')";
        private static UnistrRsv unistrRsv = new UnistrRsv();
        
        public static UnistrRsv getInstance() {
            return unistrRsv;
        }
        
        @Override
        public boolean check(String v) {
            // NVARCHAR2类型抓取数据的时候会出现UNISTR函数，这里直接解析UNICODE成原文
            return (v.startsWith(FUNC_PREFIX) || v.startsWith(FUNC_PREFIX.toLowerCase())) && v.endsWith(FUNC_SUFFIX);
        }

        @Override
        public String resolve(String v) {
            String unicodeStr = v.substring(FUNC_PREFIX.length(), v.length() - FUNC_SUFFIX.length());
            unicodeStr = unicodeStr.replace("\\\\", "\\").replace("''", "'");
            return unicodeToString(unicodeStr);
        }
        
        public String formatToRaw(Object v) {
            String unicodeStr = ((String) v).substring(FUNC_PREFIX.length(), ((String) v).length() - FUNC_SUFFIX.length());
            unicodeStr = unicodeStr.replace("\\\\", "\\").replace("''", "'");
            return unicodeToString(unicodeStr);
        }
    }

    /**
     * 二进制类型
     * HEXTORAW('ABCDEF')
     */
    public static class HextorawRsv extends AbstractOracleFunctionResolver {
        private static final String FUNC_PREFIX = "HEXTORAW('";
        private static final String FUNC_SUFFIX = "')";
        private static HextorawRsv hextorawRsv = new HextorawRsv();
        
        public static HextorawRsv getInstance() {
            return hextorawRsv;
        }
        
        @Override
        public boolean check(String v) {
            return (v.startsWith(FUNC_PREFIX) || v.startsWith(FUNC_PREFIX.toLowerCase())) && v.endsWith(FUNC_SUFFIX);
        }

        @Override
        public String resolve(String v) {
            String hexStr = v.substring(FUNC_PREFIX.length(), v.length() - FUNC_SUFFIX.length());
            return hexStr.length() % 2 == 0 ? hexStr : "0" + hexStr;
        }
    }

    /**
     * 空clob
     * EMPTY_CLOB()
     */
    public static class EmptyclobRsv extends AbstractOracleFunctionResolver {
        private static final String FUNC_PREFIX = "EMPTY_CLOB()";
        private static EmptyclobRsv emptyclobRsv = new EmptyclobRsv();

        public static EmptyclobRsv getInstance() {
            return emptyclobRsv;
        }

        @Override
        public boolean check(String v) {
            return v.equalsIgnoreCase(FUNC_PREFIX);
        }

        @Override
        public String resolve(String v) {
            return "";
        }
    }

    /**
     * 空blob
     * EMPTY_BLOB()
     */
    public static class EmptyblobRsv extends AbstractOracleFunctionResolver {
        private static final String FUNC_PREFIX = "EMPTY_BLOB()";
        private static EmptyblobRsv emptyblobRsv = new EmptyblobRsv();

        public static EmptyblobRsv getInstance() {
            return emptyblobRsv;
        }

        @Override
        public boolean check(String v) {
            return v.equalsIgnoreCase(FUNC_PREFIX);
        }

        @Override
        public String resolve(String v) {
            return "";
        }
    }

    /**
     * 普通字符串
     * 'xxxxx'
     */
    public static class StringRsv extends AbstractOracleFunctionResolver {
        private static final String FUNC_PREFIX = "'";
        private static final String FUNC_SUFFIX = "'";
        private static StringRsv stringRsv = new StringRsv();

        public static StringRsv getInstance() {
            return stringRsv;
        }

        @Override
        public boolean check(String v) {
            return v.length() >= 2 && (v.startsWith(FUNC_PREFIX) || v.startsWith(FUNC_PREFIX.toLowerCase())) && v.endsWith(FUNC_SUFFIX);
        }

        @Override
        public String resolve(String v) {
            String w = v.substring(1, v.length() - 1);
            return w.replace("\\\\", "\\").replace("''", "'");
        }
    }

    /**
     * TO_DATE
     * TO_DATE('2019-11-01 17:00:09', 'yyyy-mm-dd hh24:mi:ss')
     */
    public static class DateRsv extends AbstractOracleFunctionResolver {
        private static final String FUNC_PREFIX = "TO_DATE('";
        private static final String FUNC_SUFFIX = "', 'yyyy-mm-dd hh24:mi:ss')";
        private static DateRsv dateRsv = new DateRsv();

        public static DateRsv getInstance() {
            return dateRsv;
        }

        @Override
        public boolean check(String v) {
            return (v.startsWith(FUNC_PREFIX) || v.startsWith(FUNC_PREFIX.toLowerCase())) && v.endsWith(FUNC_SUFFIX);
        }

        @Override
        public String resolve(String v) {
            return SqlParserUtil.formatTimestr(v.substring(FUNC_PREFIX.length(), v.length() - FUNC_SUFFIX.length()));
        }
    }

    /**
     * TO_TIMESTAMP
     * TO_TIMESTAMP('2019-11-01 17:02:39.654321')
     */
    public static class TimestampRsv extends AbstractOracleFunctionResolver {
        private static final String FUNC_PREFIX = "TO_TIMESTAMP('";
        private static final String FUNC_SUFFIX = "')";
        private static TimestampRsv timestampRsv = new TimestampRsv();

        public static TimestampRsv getInstance() {
            return timestampRsv;
        }

        @Override
        public boolean check(String v) {
            return (v.startsWith(FUNC_PREFIX) || v.startsWith(FUNC_PREFIX.toLowerCase()))
                    && (v.endsWith(FUNC_SUFFIX) || v.endsWith(FUNC_SUFFIX.toLowerCase()));
        }

        @Override
        public String resolve(String v) {
            return SqlParserUtil.formatTimestr(v.substring(FUNC_PREFIX.length(), v.length() - FUNC_SUFFIX.length()));
        }
    }

    /**
     * TO_TIMESTAMP_TZ
     * TO_TIMESTAMP_TZ('2019-11-01 17:02:39.654321')
     */
    public static class TimestamptzRsv extends AbstractOracleFunctionResolver {
        private static final String FUNC_PREFIX = "TO_TIMESTAMP_TZ('";
        private static final String FUNC_SUFFIX = "')";
        private static TimestamptzRsv timestamptzRsv = new TimestamptzRsv();
        private static final SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yy hh.mm.ss a", Locale.ENGLISH);
        private static final SimpleDateFormat df2 = new SimpleDateFormat("dd-MMM-yy hh.mm.ss.SSSSSS a z", Locale.ENGLISH);

        public static TimestamptzRsv getInstance() {
            return timestamptzRsv;
        }

        @Override
        public boolean check(String v) {
            return (v.startsWith(FUNC_PREFIX) || v.startsWith(FUNC_PREFIX.toLowerCase())) && v.endsWith(FUNC_SUFFIX);
        }

        @Override
        public String resolve(String v) {
            return SqlParserUtil.formatTimestr(v.substring(FUNC_PREFIX.length(), v.length() - FUNC_SUFFIX.length()));
        }
    }

    /**
     * TO_TIMESTAMP('01-DEC-19 05.02.39 PM')
     * TO_TIMESTAMP('01-NOV-19 05.02.39 PM')
     * TO_TIMESTAMP('01-OCT-19 05.02.39 PM')
     * TO_TIMESTAMP('01-SEP-19 05.02.39 PM')
     * TO_TIMESTAMP('01-AUG-19 05.02.39 PM')
     * TO_TIMESTAMP('01-JUL-19 05.02.39 PM')
     * TO_TIMESTAMP('01-JUN-19 05.02.39 PM')
     * TO_TIMESTAMP('01-MAY-19 05.02.39 PM')
     * TO_TIMESTAMP('01-APR-19 05.02.39 PM')
     * TO_TIMESTAMP('01-MAR-19 05.02.39 PM')
     * TO_TIMESTAMP('01-FEB-19 05.02.39 PM')
     * TO_TIMESTAMP('01-JAN-19 05.02.39 PM')
     * 
     * TO_TIMESTAMP_TZ('01-DEC-19 11.35.10.214000 AM ASIA/SHANGHAI')
     * @param args
     */
    public static void main(String[] args) {
    }
}
