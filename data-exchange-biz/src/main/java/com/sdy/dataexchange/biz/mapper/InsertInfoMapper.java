package com.sdy.dataexchange.biz.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 高连明
 * @since 2019-08-26
 */
public interface InsertInfoMapper {
    /**
     * 如果是第一次同步，将同步表id，源库id，表名插入ex_table_dict
     * @param leftDbId
     * @param leftTableName
     * @param createTime
     */
    void insertIntoExTableDict( @Param("leftDbId") Integer leftDbId, @Param("leftTableName") String leftTableName
            , @Param("create_time") String createTime);

    /**
     * 如果是第一次同步，将源库同步表和目标库同步表id插入ex_table_mapping
     * @param destSyncId
     * @param createTime
     */

    void insertIntoExTableMapping(@Param("sourceSyncId") Integer sourceSyncId,@Param("destSyncId") Integer destSyncId
            , @Param("createTime") String createTime);

    /**
     * 如果不是第一次同步，在ex_field_dict中查到sync_seqno
     * @param sourceSyncid
     * @param field
     */
    Integer getSyncSeqno(@Param("sourceSyncid") Integer sourceSyncid, @Param("field") String field);
    /**
     * 通过源表id，目标表id获取同步id
     * @param sourceSyncId
     * @param destSyncId
     * @return  Integer
     */
    Integer getId(@Param("sourceSyncId") Integer sourceSyncId, @Param("destSyncId") Integer destSyncId);
    /**
     * 通过库id，表名获取表id
     * @param dbId
     * @param tableName
     * @return  Integer
     */
    Integer getSyncIds(@Param("dbId") Integer dbId,@Param("tableName") String tableName);
    /**
     * 通过字段id，获取字段名
     * @param syncId
     * @return  List<String>
     */
    List<String> getSyncField(@Param("syncId")Integer syncId);
}
