package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.model.vo.JobProcessVO;

import java.util.List;

public interface JobService {
    /**
     * 查询作业
     * @param state 作业状态
     * @return
     */
    List<ExJobInfo> listJobs(Integer state, String validFlag);
    /**
     * 查询作业
     */
    List<ExJobInfo> listJobs(Integer state, String validFlag, String ip);

    /**
     * 设置作业失败
     * @param jobInfoId 作业id
     */
    void setJobFailed(Integer jobInfoId);

    /**
     * 设置作业失败
     * @param taskId task id
     */
    void setJobFailedByTaskId(Integer taskId);

    /**
     * 获取作业信息
     * @param jobInfoId
     * @return
     */
    ExJobInfo getJobInfo(Integer jobInfoId);

    /**
     * 获取作业信息
     * @param taskId
     * @return
     */
    ExJobInfo getJobInfoByTaskId(Integer taskId);

    /**
     * 设置作业完成
     * @param jobInfoId
     */
    void setJobComplete(Integer jobInfoId);

    /**
     * 设置作业暂停
     * @param jobInfoId
     */
    void pauseJob(Integer jobInfoId);

    /**
     * 设置作业继续
     * @param jobInfoId
     */
    void resumeJob(Integer jobInfoId);

    /**
     * 保存任务统计信息
     */
    void saveJobStatistics(Integer jobInfoId, Integer taskId, String hourStr, Integer count) throws Exception;

    /**
     * 查询任务统计信息
     * @param jobInfoId 作业id
     * @param taskId 任务id
     * @param hourStr 时间 格式：yyyy_MM_dd_HH
     * @return
     */
    List<ExSwapdataDict> listByTaskId(Integer jobInfoId, Integer taskId, String hourStr);

    /**
     * 查询任务统计信息
     * @return
     */
    List<ExSwapdataDict> listByTimeRange(String hourStrStart, String hourStrEnd, Integer jobId);

    /**
     * 查询任务统计信息
     * @return
     */
    List<JobProcessVO> listStatByJobIds(List<Integer> jobIdList);

    /**
     * 查询任务统计信息 - 分页
     * @param jobInfoId 作业id
     * @param taskId 任务id
     * @param hourStr 时间 格式：yyyy_MM_dd_HH
     * @return
     */
    IPage<ExSwapdataDict> pageByTaskId(Integer jobInfoId, Integer taskId, String hourStr, Page<ExSwapdataDict> page);

    /**
     * 任务交换为增量模式
     * @param jobTaskId
     */
    void convertToAppendeMode(Integer jobTaskId);

    /**
     * 同步canal库配置
     * @param exDbDict 库
     * @param type 1-增加 2-删除
     */
    void syncCanalDbConfig(ExDbDict exDbDict, Integer type) throws Exception;

    /**
     * 同步canal表配置
     * @param exTableDict 表
     * @param type 1-增加 2-删除
     */
    void syncCanalTbConfig(ExTableDict exTableDict, Integer type) throws Exception;

    /**
     * 查询映射中的库
     * @return
     */
    List<ExDbDict> listMappedSrcDb(Integer exTaskId);

    /**
     * 查询映射中的库
     * @return
     */
    List<ExTableDict> listMappedSrcTb(Integer exTaskId);

    /**
     * 记录日志
     * @param taskId 任务id
     * @param logType 日志类型
     * @param msg 日志消息
     */
    void saveLog(Integer taskId, Integer logType, String msg);

    /**
     * 获取任务详情
     */
    ExJobTask getJobTask(Integer jobTaskId);

    /**
     * 更新增量开始时间
     */
    void updateStartTimestamp(Long ts, Integer taskId);

    /**
     * 添加任务的canal配置
     * @param taskId 任务id
     */
    void addCanalConfig(Integer taskId) throws Exception;

    /**
     * 移除任务的canal配置
     * @param taskId 任务id
     */
    void removeCanalConfig(Integer taskId) throws InterruptedException;
}
