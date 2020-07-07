package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.BO.DatabaseBO;
import com.sdy.dataexchange.biz.model.DbDetails;
import com.sdy.dataexchange.biz.model.ExDbDict;
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
public interface ExDbDictService extends BaseService<ExDbDict> {
    /**
     * 通过库id获取摸个库的信息
     * @param dbId
     * @return ExDbDict
     */
    DbDetails getOneInfo(Integer dbId);
    /**
     * 通过库描述获取库id
     * @param dbDesc
     * @return CharSequence
     */
    CharSequence getDbId(String dbDesc);

    /**
     * 编辑数据源
     * @param dbId
     * @param dbDesc
     * @param dbIp
     * @param dbPort
     * @param dbUser
     * @param dbPasswd
     * @param dbName
     * @param dbType
     * @param office
     * @return
     */
    Response updateDb(Integer dbId,String dbDesc, String dbIp, Integer dbPort, String dbUser, String dbPasswd
            , String dbName, String dbType, Integer office,String userInfo);

    /**
     * 获得库的总量
     * @return
     */
    Integer getTotle();

    /**
     * 获得库详情信息
     * @param page1
     * @return
     */
    List<DbDetails> getDb(Page<DbDetails> page1);
    /**
     * 新增数据源
     * @param databaseBO
     * @return
     */
    Response addDatabase(DatabaseBO databaseBO);

    /**
     * 通过库id和表名获取表id
     *
     * @param dbId text
     * @return  Integer
     */
    Integer getSyncIds(Integer dbId, String text);

    /**
     * 得到库的所属地信息
     * @param dbId
     * @return String
     */
    String getInfo(Integer dbId);
    /**
     * 获取所有库信息
     * @return  List<Map<String,Object>>
     */
    List<Map<String,Object>> getAllDatabases();

    /**
     * 通过交换节点名称获取所有库
     * @param gatherDesc
     */
    Response getDbByDesc(String gatherDesc);
    /**
     * 通过库id获取库信息
     * @param dbId
     */
    ExDbDict getDbInfo(Integer dbId);

    /**
     * 获取源库，目标库
     * @param gatherId
     * @return
     */
    Map getSourceDbsAndDestDbs(Integer gatherId);
    /**
     * 通过交换节点获取库
     * @param gatherDesc
     * @return
     */
    List<Map<String,Object>> getDbByGatherDesc(Integer gatherDesc);

    /**
     * 通过库名获取用户
     * @param dbDesc
     */
    List<String> getUserByDbDesc(String dbDesc);

    /**
     * 通过库名搜索
     * @param dbName
     * @param page
     * @return
     */
    Integer getTotleByName(String dbName,Page page);
}
