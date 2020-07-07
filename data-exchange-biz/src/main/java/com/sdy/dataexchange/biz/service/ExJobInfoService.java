package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.BizException;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.mvc.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyy
 * @since 2019-09-08
 */
public interface ExJobInfoService extends BaseService<ExJobInfo> {
    /**
     * 获取所有作业
     * @return  List<ExJobInfo>
     */
    List<ExJobInfo> queryAllJob();
    /**
     * 获取所有作业
     * @param page
     * @return   List<JobInfos>
     */
    List<JobInfos> getJob(Page page);
    /**
     * 获取作业详情
     * @param id
     * @return  JobInfos
     */
    JobInfos getJobById(Integer id);

    /**
     * 更新作业
     * @param jobId
     * @param jobName
     * @param jobDesc
     * @return
     */
    Boolean updateInfo(Integer jobId,String jobName,String jobDesc,String ip);
    /**
     * 获取所有转换名
     * @return  List<String>
     */
    List<Map> getAllTasks();

    /**
     * 获取作业详情
     * @param id
     * @return  List<JobDetails>
     */
    List<JobDetails> getJobDetails(Integer id);
    /**
     * 获取作业
     * @param key
     * @return  List<JobInfos>
     */
    List<JobInfos> getInfo(String key);

    /**
     * 新建作业
     * @param taskNames
     * @return
     */
    Response save(TaskNames taskNames) throws BizException;

    /**
     * 删除作业
     * @param jobIdList
     */
    void removeJob(List<Integer> jobIdList) throws BizException;

    /**
     * 获取作业总数
     * @return
     */
    Integer getTotle();

    /**
     * 通过工作id更改工作状态
     * @param code
     * @param jobId
     */
    boolean changeCode(Integer code,Integer jobId);

    /**
     * 开启增量作业
     * @param flag
     */
    boolean updateJobFlag(String flag,Integer jobId,String dealTime);

    /**
     * 修改任务flag,全量为2，增量为1
     * @param flag
     * @param jobId
     */
    boolean updateTaskFlag(String flag, Integer jobId);

    /**
     * 开启全量作业
     * @param flag
     * @param jobId
     */
    boolean updateFullJobFlag(String flag, Integer jobId);

    /**
     * 获取改作业任务的日志
     * @param id
     * @return
     */
    List<ExSyncLog> getJobLogs(Integer id);
}
