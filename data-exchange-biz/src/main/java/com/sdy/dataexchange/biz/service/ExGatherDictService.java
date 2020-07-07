package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.BO.GatherBO;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExGatherDict;
import com.sdy.dataexchange.biz.model.GatherDictResult;
import com.sdy.mvc.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
public interface ExGatherDictService extends BaseService<ExGatherDict> {
    /**
     * 通过交换节点描述获取交换节点
     * @param gatherDesc
     * @return ExGatherDict
     */
    List<GatherDictResult> getInfo(String gatherDesc);

    /**
     *获取所有交换节点分页信息
     * @param page
     * @return
     */
    List<GatherDictResult> getGatherInfo(Page page);

    /**
     * 通过交换节点id获取一个交换节点信息
     * @param gatherId
     * @return
     */
    GatherDictResult getOneInfo(Integer gatherId);

    /**
     * 保存交换节点
     * @param gatherDictResult
     */
    Integer saveGather(GatherDictResult gatherDictResult);

    /**
     * 保存库对应关系
     * @param gatherDictResult
     */
    Integer saveDbMapping(GatherDictResult gatherDictResult);

    /**
     * 更新采交换节名称及库对应关系
     * @param exGatherDict
     * @return
     */
    Integer updateInfo(ExGatherDict exGatherDict);
    /**
     * 获取交换节点id
     * @param gatherDesc
     * @return  Integer
     */
    Integer getGatherId(String gatherDesc);
    /**
     * 获取库信息
     * @param dbId
     * @return ExDbDict
     */
    ExDbDict getDbInfo(Integer dbId);

    /**
     * 获取交换节点总数
     * @return
     */
    Integer getTotle();


    /**
     * 新增交换节点
     * @param gatherBO
     * @return
     */

    Response addGather(GatherBO gatherBO);
    /**
     * 获取所有交换节点信息
     * @return  List<GatherDictResult>
     */
    List<GatherDictResult> getAllGatherPage();
    /**
     * 获取所有交换节点信息
     * @return  List<GatherDictResult>
     */
    List<Map> getAllGather();
}
