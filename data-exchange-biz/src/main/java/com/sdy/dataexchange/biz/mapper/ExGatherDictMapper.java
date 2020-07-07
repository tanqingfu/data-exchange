package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExGatherDict;
import com.sdy.dataexchange.biz.model.GatherDictResult;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
public interface ExGatherDictMapper extends BaseMapper<ExGatherDict> {
    /**
     * 通过交换节点描述获取交换节点
     * @param gatherDesc
     * @return
     */
    List<GatherDictResult> getInfo(@Param("gatherDesc") String gatherDesc);

    /**
     * 获取所有交换节点的分页信息
     * @param page
     * @return
     */
    List<GatherDictResult> getGatherInfo(@Param("page")Page page);

    /**
     * 通过交换节点id获取一个交换节点信息
     * @param gatherId
     * @return
     */
    GatherDictResult getOneInfo(@Param("gatherId") Integer gatherId);

    /**
     * 保存交换节点
     * @param gatherDictResult
     */
    Integer saveGather(@Param("gatherDictResult")GatherDictResult gatherDictResult);

    /**
     * 保存库对应关系
     * @param gatherDictResult
     */
    Integer saveDbMapping(@Param("gatherDictResult")GatherDictResult gatherDictResult);
    /**
     * 更新交换节点名称及库对应关系
     * @param exGatherDict
     * @return
     */
    Integer updateInfo(@Param("exGatherDict") ExGatherDict exGatherDict);
    /**
     * 获取库信息
     * @param dbId
     * @return ExDbDict
     */
    ExDbDict getDbInfo(@Param("dbId") Integer dbId);

    /**
     * 获取交换接节点总数
     * @return
     */
    Integer getTotle();

    /**
     * 通过交换节点描述获取交换节点id
     * @param gatherDesc
     * @return  Integer
     */
    Integer getGatherIdByGatherDesc(@Param("gatherDesc")String gatherDesc);

    /**
     * 新增交换节点
     * @param gatherDesc
     * @param serviceIp
     * @param serviceDesc
     * @param gatherPath
     * @param sshPassword
     * @param sshPort
     * @param sshUser
     * @return
     */
    int insertIntoExGatherDict(@Param("gatherDesc")String gatherDesc,@Param("serviceIp") String serviceIp
            , @Param("serviceDesc")String serviceDesc,@Param("gatherPath") String gatherPath
            ,@Param("sshPassword")String sshPassword,@Param("sshPort")Integer sshPort,@Param("sshUser")String sshUser);

    /**
     * 通过源库跟目标库的描述，获取交换节点id
     * @param sourceDesc
     * @param destDesc
     * @return
     */
    String getGatherId(@Param("sourceDesc")String sourceDesc, @Param("destDesc")String destDesc);

    /**
     * 获取所有交换节点信息
     * @return  List<GatherDictResult>
     */
    List<GatherDictResult> getAllGatherPage();
    /**
     * 获取所有交换节点信息
     * @return  List
     */
    List<Map> getAllGather();
}
