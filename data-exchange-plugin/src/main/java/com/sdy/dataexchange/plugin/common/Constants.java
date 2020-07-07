package com.sdy.dataexchange.plugin.common;

public class Constants {
    public static final String HEX_PREFIX = "0x";
    
    public interface FieldType {
        String FieldInteger = "integer";
        String FieldLong = "long";
        String FieldString = "string";
        String FieldByte = "byte";
        String FieldDecimal = "decimal";
    }
    
    public static final long CANAL_TIMESTAMP_UNIT = 1000L;
    public static final int APPEND_EXPIRED_DATE = 10;
}
