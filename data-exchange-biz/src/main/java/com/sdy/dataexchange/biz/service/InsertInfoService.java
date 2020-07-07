package com.sdy.dataexchange.biz.service;

import java.util.List;

public interface InsertInfoService {
    /**
     * 如果是第一次同步，将同步表id，源库id，表名插入ex_table_dict
     * @param leftDbId
     * @param leftTableName
     * @param createTime
     */
    void insertIntoExTableDict( Integer leftDbId, String leftTableName, String createTime);

    /**
     * 如果是第一次同步，将源库同步表和目标库同步表id插入ex_table_mapping
     * @param sourceSyncId
     * @param destSyncId
     * @param createTime
     */

    void insertIntoExTableMapping(Integer sourceSyncId, Integer destSyncId, String createTime);

    /**
     * 如果不是第一次同步，在ex_field_dict中查到sync_seqno
     * @param sourceSyncid
     * @param field
     */
    Integer getSyncSeqno(Integer sourceSyncid, String field);
    /**
     * 通过源表id，目标表id获取同步id
     * @param sourceSyncId
     * @param destSyncId
     * @return  Integer
     */
    Integer getId(Integer sourceSyncId, Integer destSyncId);
    /**
     * 通过库id，表名获取表id
     * @param dbId
     * @param tableName
     * @return  Integer
     */
    Integer getSyncIds(Integer dbId, String tableName);

    /**
     * 通过字段id，获取字段名
     * @param syncId
     * @return  List<String>
     */
    List<String> getSyncField(Integer syncId);

}
