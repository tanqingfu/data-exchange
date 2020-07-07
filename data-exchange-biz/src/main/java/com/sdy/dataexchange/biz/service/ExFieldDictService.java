package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.BO.FieldBO;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExFieldDict;
import com.sdy.dataexchange.biz.model.TableNameResult;
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
public interface ExFieldDictService extends BaseService<ExFieldDict> {
    /**
     * 获取所有表信息
     * @param page
     * @return  List<TableNameResult>
     */
    List<TableNameResult> getTableName(Page page);
    /**
     * 获取表信息
     * @param syncSeqno
     * @return TableNameResult
     */
    TableNameResult getInfo(Integer syncSeqno);

    /**
     * 获取字段总数
     * @return
     */
    Integer getTotle();
    /**
     * 通过库id获取库信息
     * @param dbId
     * @return  List<ExDbDict>
     */
    ExDbDict getDbInfo(Integer dbId);
    /**
     * 通过库名获取库id
     * @param dbName
     * @return  List<Map>
     */
    Integer getDbId(String dbName);
    /**
     * 获取表id
     * @param dbId
     * @param tableName
     * @return  Integer
     */
    Integer getTableId(Integer dbId,String tableName);

    /**
     * 获取所有字段
     * @param fieldBO
     * @return
     */
    Response getAllSyncFields(FieldBO fieldBO);
}
