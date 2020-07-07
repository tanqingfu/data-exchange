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
public interface InsertFlagMapper {
    /**
     * 通过源表id，目标表id获取交换节点id
     * @param leftDbId
     * @param rightDbId
     * @return  int
     */
    Integer getGatherId(@Param("leftDbId")String leftDbId,@Param("rightDbId") String rightDbId);
    /**
     * 往库映射关系表中插入有效标志
     * @param valid
     * @param sourceDbId
     * @param destDbId
     * @return  int
     */
    Integer updateExDbMapping(@Param("valid")String valid, @Param("gatherId")Integer gatherId,@Param("sourceDbId")Integer sourceDbId,@Param("destDbId") Integer destDbId);
    /**
     * 通过表名获取表id
     * @param tableName
     * @return  int
     */
        Integer getSyncId(@Param("dbId")String dbId,@Param("tableName")String tableName);
    /**
     * 通过源表id，目标表id验证是否同步过
     * @param leftSyncId
     * @param rightSyncId
     * @return  int
     */
    Integer getId(@Param("leftSyncId")Integer leftSyncId, @Param("rightSyncId")Integer rightSyncId);
    /**
     * 往表映射关系表中插入有效标志
     * @param valid
     * @param tableMappingId
     * @return  int
     */
    Integer updateExTableMapping(@Param("valid")String valid,@Param("tableMappingId") Integer tableMappingId);
    /**
     * 通过字段id获取字段名
     * @param syncId
     * @return  List
     */
    List<String> getSyncField(@Param("syncId")Integer syncId);

}
