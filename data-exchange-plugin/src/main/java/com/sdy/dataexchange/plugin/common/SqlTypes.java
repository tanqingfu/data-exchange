package com.sdy.dataexchange.plugin.common;

/**
 * <P>The class that defines the constants that are used to identify generic
 * SQL types, called JDBC types.
 * <p>
 * This class is never instantiated.
 */
public class SqlTypes {


    /*************** 一些自定义 ****************/
    public static final int STRING = 1;
    public static final int INTEGER = 2;
    public static final int LONG = 3;
    public static final int BIG_DECIMAL = 4;
    public static final int BYTE_ARRAY = 7;
    public static final int FLOAT = 8;
    public static final int DOUBLE = 9;
    public static final int TIME = 10;
    public static final int DATE = 11;
    public static final int DATE_TIME = 12;
    public static final int YEAR = 13;
    public static final int TIMESTAMP = 14;
    public static final int TIMESTAMP_TZ = 15;
    public static final int CLOB = 16;
    public static final int BLOB = 17;
    public static final int DATE_TIME3 = 18;
    public static final int DATE_TIME6 = 19;
    public static final int DATE_TIME7 = 20;

    // Prevent instantiation
    private SqlTypes() {}
}
