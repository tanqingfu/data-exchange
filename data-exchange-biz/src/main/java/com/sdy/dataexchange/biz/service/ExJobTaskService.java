package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.BO.TaskBO;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.mvc.service.BaseService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wyy
 * @since 2019-09-08
 */
public interface ExJobTaskService extends BaseService<ExJobTask> {
    /**
     * 获所有任务信息
     *
     * @return List<ExJobTask>
     */
    List<ExJobTask> queryAllTask();

    /**
     * 获所有任务信息
     *
     * @param page
     * @return List<TaskInfos>
     */
    List<TaskInfos> getTask(Page page);

    /**
     * 获取任务详情
     *
     * @param id
     * @return List<TaskInfoDetails>
     */
    List<TaskInfoDetails> getOneTask(Integer id);

    /**
     * 获取任务详情
     *
     * @param key
     * @return List<TaskInfos>
     */
    List<TaskInfos> getInfo(String key);

    /**
     * 删除表映射、字段映射
     *
     * @param taskIdList
     */
    void removeTask(List<Integer> taskIdList) throws Exception;

    /**
     * 获取任务总数
     *
     * @return
     */
    Integer getTotle();



    /**
     * 新增任务
     * @param taskBO
     * @return
     */
    Response saveTask(TaskBO taskBO);

    /**
     * 编辑任务
     * @param taskBO
     * @return
     */
    Response editTask(TaskBO taskBO);


    List<ExDbDict> listMappedSrcDb(Integer exTaskId);

    List<ExTableDict> listMappedSrcTb(Integer exTaskId);

    Response getRowseq(Integer destDbId,String destTableName);
}
