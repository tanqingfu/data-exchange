package com.sdy.dataexchange.biz.service;


import java.util.List;
import java.util.Map;

public interface ShowInfoService {
    /**
     * 通过库id，表名获取表id
     * @param dbId
     * @param dbTable
     */
    Map getSyncId(Integer dbId, String dbTable);
    /**
     * 通过表id，字段名判断该字段是否同步过
     * @param syncId
     * @param syncField
     */
    Integer getSyncSeqno(Integer syncId, String syncField);
    /**
     * 获取创建时间
     * @return  List<Map<String, Object>>
     */
    List<Map<String, Object>> getCreateTime();

    /**
     * 通过库id获取表id
     * @param destDbid
     */
    List<Map<String, Object>> getSyncIds(Integer destDbid);
}
