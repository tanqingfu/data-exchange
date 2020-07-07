package com.sdy.dataexchange.biz.redis;

/**
 * @author: 王越洋
 * @version: v1.0
 * @description: com.sdy.dataexchange.biz.redis
 * @date:2019/8/26
 */
public class CacheNames {

    public final static String CACHE_BUCKET_DATA_MYSQL_ORIGIN = "cache_bucket_data_msyql_origin";
    public final static String CACHE_BUCKET_DATASTATISTIC = "cache_bucket_datastatistic";
    public final static String CACHE_BUCKET_CONSUME_TS = "cache_bucket_consume_ts";
    
    public final static String pagingSelect = "pagingSelect";
    public final static String querySourceId = "querySourceId";
    public final static String querySourceTable = "querySourceTable";
    public final static String queryDestTable = "queryDestTable";


    public final static String getDataAdapterByTaskId = "getDataAdapterByTaskId";
    public final static String queryExJobTask = "queryExJobTask";
    public final static String queryTableMapping = "queryTableMapping";
    public final static String queryExDbDictSrc = "queryExDbDictSrc";
    public final static String queryExDbDictDest = "queryDestTable";
    public final static String queryExTableDictDest = "queryExTableDictDest";
    public final static String queryExTableDictSrc = "queryExTableDictSrc";
    public final static String queryExFieldMapping = "queryExFieldMapping";
    public final static String queryTablePrimaryKey = "queryTablePrimaryKey";
    public final static String querySyncmonUserinfo = "querySyncmonUserinfo";
    public final static String queryExDbDict = "queryExDbDict";
    public final static String queryExGatherDict = "queryExGatherDict";
    public final static String queryExTableMappingDictId = "queryExTableMappingDictId";
    public final static String getJobInfoByTaskId = "getJobInfoByTaskId";
    public final static String consumeFailedTsMap = "consumeFailedTsMap";
    public final static String getSrcFieldDict = "getSrcFieldDict";
    public final static String getDestFieldDict = "getDestFieldDict";



}
