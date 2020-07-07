package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.DbMappingResult;
import com.sdy.dataexchange.biz.model.ExDbMapping;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
public interface ExDbMappingMapper extends BaseMapper<ExDbMapping> {
    /**
     * 获取所有库映射关系
     * @param page
     * @return
     */
    List<DbMappingResult> getMapping(@Param("page")Page page);

    /**
     * 获取库映射详情
     * @param id
     * @return
     */
    DbMappingResult getInfo(@Param("id") Integer id);
    /**
     * 通过交换节点id获取一条裤映射信息
     * @param gatherId
     * @return ExDbMapping
     */
    ExDbMapping getExDbMapping(@Param("gatherId") Integer gatherId);
    /**
     * 通过库id获取库名
     * @param dbid
     * @return String
     */
    String getDbName(@Param("dbid") Integer dbid);

    /**
     * 获取库映射总数
     * @return
     */
    Integer getTotle();

    /**
     * 通过库名获取库映射信息
     * @param dbName
     * @return
     */
    List<DbMappingResult> getByDbName(@Param("dbName") String dbName);

    /**
     * 通过库名获取库映射数
     * @param dbName
     * @return
     */
    Integer getResult(@Param("dbName") String dbName);


    /**
     * 通过源库名和用户名获取库id
     *
     * @param sourceDb
     * @param sourceUser1
     * @return Integer
     */
    Integer getDbId(@Param("sourceDb") String sourceDb, @Param("sourceUser1") String sourceUser1);
    /**
     * 通过目标库描述获取目标库id
     *
     * @param destDb
     * @return Integer
     */
    Integer getDestDbId(@Param("destDb") String destDb);
    /**
     * 将交换节点id，源库id，目标库id，创建时间加入到库映射表
     *
     * @param gatherId
     * @param sourceDbId
     * @param destDbId
     * @param createTime
     */
    boolean insertDbMapping(@Param("gatherId") Integer gatherId, @Param("sourceDbId") Integer sourceDbId
            , @Param("destDbId") Integer destDbId, @Param("createTime") String createTime);
    /**
     * 通过交换节点id，源库id，目标库id获取一条库映射信息
     *
     * @param gatherId
     * @param sourceDbId
     * @param destDbId
     * @return ExDbMapping
     */
    ExDbMapping getDbMapping(@Param("gatherId")Integer gatherId,@Param("sourceDbId")  Integer sourceDbId
            ,@Param("destDbId")  Integer destDbId);
    /**
     * 通过交换节点描述获取交换节点id
     *
     * @param gatherDesc
     * @return Integer
     */
    Integer getGatherId(@Param("gatherDesc") String gatherDesc);
    /**
     * 通过目标库id获取交换节点id
     *
     * @param destDbId
     * @return Integer
     */
    List<Integer> getGatherIdByDestId(@Param("destDbId") Integer destDbId);
    /**
     * 通过交换节点id获取库id
     * @param gatherId
     * @return Integer
     */
    List<Integer> getDbIdByGatherId(Integer gatherId);

    /**
     * 编辑库映射
     * @param id
     * @param gatherId
     * @param sourceDbId
     * @param destDbId
     * @param modifyTime
     */
    boolean updateDbMapping(@Param("id") Integer id,@Param("gatherId") Integer gatherId, @Param("sourceDbId") Integer sourceDbId, @Param("destDbId") Integer destDbId, @Param("modifyTime") String modifyTime);
}
