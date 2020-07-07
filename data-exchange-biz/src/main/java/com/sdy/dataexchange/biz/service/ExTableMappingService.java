package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.biz.model.TableMappingResult;
import com.sdy.dataexchange.biz.model.TaskInfoResult;
import com.sdy.mvc.service.BaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
public interface ExTableMappingService extends BaseService<ExTableMapping> {
    /**
     * 获取所有表映射关系
     * @param page
     * @return  List<TableMappingResult>
     */
    List<TableMappingResult> getMapping(Page page);
    /**
     * 获取所有任务信息
     * @param id
     * @return  List<TaskInfoResult>
     */
    List<TaskInfoResult> getInfo(Integer id);
    /**
     * 获取表id
     * @param taskName
     * @return  Integer
     */
    Integer getTableId(String taskName);
    /**
     * 获取表映射信息
     ** @param id
     * @return  TableMappingResult
     */
    TableMappingResult getOneInfo(Integer id);
    /**
     * 更新表映射关系
     * @param tableMappingResult
     * @return  Integer
     */
    Integer updateInfo(TableMappingResult tableMappingResult);

    void removeTask(List<Integer> integers);

    /**
     * 删除表映射
     * @param tbMappingIdList
     */
    void removeTbMapping(List<Integer> tbMappingIdList) throws Exception;

    /**
     * 通过库名，ip，Port,用户名获取库id
     * @param db
     * @param ip
     * @param port
     * @param user
     * @return
     */
    Integer getDbId(String db,String ip,Integer port,String user);

    /**
     * 获取任务总条数
     * @param id
     * @return
     */
    Integer getTotle(Integer id);

    /**
     * 获取任务总条数
     * @return
     */
    Integer getAll();

    /**
     *通过任务名搜索
     * @param key
     * @param page
     * @return
     */
    List<TableMappingResult> getInfos(String key,Page page);
}
