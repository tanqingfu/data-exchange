package com.sdy.dataexchange.biz.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 高连明
 * @since 2019-08-26
 */
public interface ShowInfoMapper {
    /**
     * 通过库id，表名获取表id
     * @param dbId
     * @param dbTable
     */
    Map getSyncId(@Param("dbId")Integer dbId,@Param("dbTable") String dbTable);
    /**
     * 通过表id，字段名判断该字段是否同步过
     * @param syncId
     * @param syncField
     */
    Integer getSyncSeqno(@Param("syncId")Integer syncId,@Param("syncField") String syncField);
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
