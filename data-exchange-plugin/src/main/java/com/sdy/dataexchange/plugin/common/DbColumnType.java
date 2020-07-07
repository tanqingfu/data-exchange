package com.sdy.dataexchange.plugin.common;

import lombok.Getter;

import java.sql.Types;

/**
 * <p>
 * 表字段类型
 * </p>
 */
public enum DbColumnType implements IColumnType {
    // 基本类型
    BASE_BYTE("byte", null, null),
    BASE_SHORT("short", null, null),
    BASE_CHAR("char", null, null),
    BASE_INT("int", null, null),
    BASE_LONG("long", null, null),
    BASE_FLOAT("float", null, null),
    BASE_DOUBLE("double", null, null),
    BASE_BOOLEAN("boolean", null, null),

    // 包装类型
    BYTE("Byte", null, null),
    SHORT("Short", null, null),
    CHARACTER("Character", null, Types.CHAR),
    INTEGER("Integer", null, Types.INTEGER),
    LONG("Long", null, Types.BIGINT),
    FLOAT("Float", null, Types.FLOAT),
    DOUBLE("Double", null, Types.DOUBLE),
    BOOLEAN("Boolean", null, Types.BOOLEAN),
    STRING("String", null, Types.VARCHAR),

    // sql 包下数据类型
    DATE_SQL("Date", "java.sql.Date", Types.DATE),
    TIME("Time", "java.sql.Time", Types.DATE),
    TIMESTAMP("Timestamp", "java.sql.Timestamp", Types.DATE),
    BLOB("Blob", "java.sql.Blob", Types.BLOB),
    CLOB("Clob", "java.sql.Clob", Types.CLOB),

    // java8 新时间类型
    LOCAL_DATE("LocalDate", "java.time.LocalDate", Types.DATE),
    LOCAL_TIME("LocalTime", "java.time.LocalTime", Types.DATE),
    YEAR("Year", "java.time.Year", Types.DATE),
    YEAR_MONTH("YearMonth", "java.time.YearMonth", Types.DATE),
    LOCAL_DATE_TIME("LocalDateTime", "java.time.LocalDateTime", Types.DATE),

    // 其他杂类
    BYTE_ARRAY("byte[]", null, Types.BINARY),
    OBJECT("Object", null, null),
    DATE("Date", "java.util.Date", Types.DATE),
    BIG_INTEGER("BigInteger", "java.math.BigInteger", Types.BIGINT),
    BIG_DECIMAL("BigDecimal", "java.math.BigDecimal", Types.DECIMAL);

    /**
     * 类型
     */
    @Getter
    private final String type;

    /**
     * 包路径
     */
    @Getter
    private final String pkg;

    /**
     * @see java.sql.Types
     */
    @Getter
    private final Integer sqlType;

    DbColumnType(final String type, final String pkg, Integer sqlType) {
        this.type = type;
        this.pkg = pkg;
        this.sqlType = sqlType;
    }
}
