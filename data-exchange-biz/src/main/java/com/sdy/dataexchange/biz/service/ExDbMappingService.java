package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.BO.DbMappingBO;
import com.sdy.dataexchange.biz.model.DbMappingResult;
import com.sdy.dataexchange.biz.model.ExDbMapping;
import com.sdy.mvc.service.BaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
public interface ExDbMappingService extends BaseService<ExDbMapping> {
    /**
     * 分页获取库信息
     * @param page
     * @return List<DbMappingResult>
     */
    List<DbMappingResult> getMapping(Page page);
    /**
     * 通过id获取一条库映射信息
     * @param id
     * @return DbMappingResult
     */
    DbMappingResult getInfo(Integer id);
    /**
     * 通过交换节点描述获取交换节点id
     * @param gatherDesc
     * @return Integer
     */
    Integer getGatherId(String gatherDesc);
    /**
     * 通过交换节点id获取一条裤映射信息
     * @param gatherId
     * @return ExDbMapping
     */
    ExDbMapping getExDbMapping(Integer gatherId);
    /**
     * 通过库id获取库名
     * @param dbid
     * @return String
     */
    String getDbName(Integer dbid);

    /**
     * 删除库映射以及其他涉及的东西
     */
    void removeDbMapping(List<Integer> dbMappingIdList) throws Exception;

    /**
     * 获得库映射关系总量
     * @return
     */
    Integer getTotle();

    /**
     * 通过库名查找
     * @param dbName
     * @return
     */
    List<DbMappingResult> getByDbName(String dbName);

    /**
     * 通过库名查询后的总数
     * @param dbName
     * @return
     */
    Integer getResult(String dbName);

    /**
     * 新增库映射
     * @param dbMappingBO
     * @return
     */
    Response addDbMapping(DbMappingBO dbMappingBO);
    /**
     * 编辑库映射
     * @param dbMappingBO
     * @return
     */
    Response updateDbMapping(DbMappingBO dbMappingBO);
}
