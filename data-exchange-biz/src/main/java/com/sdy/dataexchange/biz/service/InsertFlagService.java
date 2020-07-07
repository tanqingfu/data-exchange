package com.sdy.dataexchange.biz.service;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.List;

public interface InsertFlagService {
    /**
     * 通过源表id，目标表id获取交换节点id
     * @param leftDbId
     * @param rightDbId
     * @return  int
     */
   Integer getGatherId(String leftDbId, String rightDbId);
    /**
     * 通过表名获取表id
     * @param tableName
     * @return  int
     */
    Integer getSyncId(String dbId,String tableName);
    /**
     * 通过源表id，目标表id验证是否同步过
     * @param leftSyncId
     * @param rightSyncId
     * @return  int
     */
    Integer getId(Integer leftSyncId, Integer rightSyncId);
    /**
     * 通过字段id获取字段名
     * @param syncId
     * @return  List
     */
    List getSyncField(Integer syncId);

}
