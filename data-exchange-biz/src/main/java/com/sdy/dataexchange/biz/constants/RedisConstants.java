package com.sdy.dataexchange.biz.constants;

public class RedisConstants {
    public static final String REDIS_PREFIX = "dataexchange_";
    public static final String REDIS_LAST_SYNC_KEY = REDIS_PREFIX + "lastsynckey_";
    public static final String REDIS_ADD_SIZE = REDIS_PREFIX + "addsize_";
    public static final String REDIS_DELSIZE = REDIS_PREFIX + "delsize_";
    public static final String REDIS_COMSUMER_LIST = REDIS_PREFIX + "consumerlist_";
    public static final String REDIS_LATELY_COMSUMED_LIST = REDIS_PREFIX + "latelyConsumedlist_";
    public static final String REDIS_SPLIT_COMSUMED_LIST = REDIS_PREFIX + "splitConsumedlist_";
    public static final String REDIS_SYNC_STATISTIC = REDIS_PREFIX + "sync_statistic_";
    public static final String REDIS_CONSUME_FAILED_TS_MAP = REDIS_PREFIX + "consume_failed_ts_map_";
    public static final String REDIS_SYNC_COUNT = REDIS_PREFIX + "redis_sync_count_";
    public static final String REDIS_FULL_SYNC_COUNT = REDIS_PREFIX + "redis_full_sync_count_";
    public static final String REDIS_FULL_SYNC_COUNT_READ = REDIS_PREFIX + "redis_full_sync_count_read_";
    public static final String REDIS_FULL_SYNC_COUNT_TOTAL = REDIS_PREFIX + "redis_full_sync_count_total_";
    public static final String REDIS_SYNC_START_MILLI = REDIS_PREFIX + "redis_sync_start_milli_";
    public static final String REDIS_MONITOR_MYSQL_COUNT = REDIS_PREFIX + "redis_monitor_mysql_count_";

    public static final String REDIS_FILE_WRITE= REDIS_PREFIX + "redis_file_write_";
}
