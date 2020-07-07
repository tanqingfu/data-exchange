package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.DbNameResult;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.mvc.service.BaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzq
 * @since 2019-07-23
 */
public interface ExTableDictService extends BaseService<ExTableDict> {
    /**
     * 获取库表信息
     * @param page
     * @return  List<DbNameResult>
     */
    List<DbNameResult> getDbName(Page page);
    /**
     * 通过表id获取库表信息
     * @param syncId
     * @return  DbNameResult
     */
    DbNameResult getInfo(Integer syncId);

    /**
     * 获取表的总数
     * @return
     */
    Integer getTotle();

    /**
     * 查询需要监控的MYSQL库表
     * @return
     */
    List<ExTableDict> listToSync();

    /**
     * 通过库id获取库信息
     * @param dbId
     * @return ExDbDict
     */
    ExDbDict getDbInfo(Integer dbId);

    /**
     * 获取目标表，字段
     * @param destDbId
     * @return
     */
    Response getDestTablesAndFields(Integer destDbId);
//    void setFields(List<Map<String, Object>> fields, Map<String, Object> tableName);

    /**
     * 获取源表，字段
     * @param sourceDbId
     * @param gatherId
     * @return
     */
    Response getSourceTablesAndFields(Integer sourceDbId, Integer gatherId);
}
