package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wyy
 * @since 2019-09-08
 */
public interface ExJobInfoMapper extends BaseMapper<ExJobInfo> {
    /**
     * 获取所有作业
     *
     * @param page
     * @return List<JobInfos>
     */
    List<JobInfos> getJob(@Param("page") Page page);

    /**
     * 获取作业详情
     *
     * @param id
     * @return JobInfos
     */
    JobInfos getJobById(@Param("id") Integer id);

    /**
     * 更新作业
     *
     * @param jobId
     * @param jobName
     * @param jobDesc
     * @return
     */
    Boolean updateInfo(@Param("jobId") Integer jobId, @Param("jobName") String jobName, @Param("jobDesc") String jobDesc,@Param("ip") String ip);

    /**
     * 获取所有作业
     *
     * @return List<ExJobInfo>
     */
    List<ExJobInfo> queryAllJob();

    /**
     * 获取所有转换名
     *
     * @return List<Map>
     */
    List<Map> getAllTasks();

    /**
     * 创建作业
     *
     * @param jobName
     * @param jobDesc
     * @param jobState
     * @param validFlag
     * @return Integer
     */
    boolean newJob(@Param("jobName") String jobName, @Param("jobDesc") String jobDesc, @Param("jobState") Integer jobState
            , @Param("validFlag") String validFlag, @Param("ip") String ip);

    /**
     * 获取作业详情
     *
     * @param jobName
     * @return Integer
     */
    Integer getOneJob(@Param("jobName") String jobName);

    /**
     * 更新作业
     *
     * @param taskName
     * @param jobId
     */
    boolean updateJobId(@Param("taskName") String taskName, @Param("jobId") Integer jobId);

    /**
     * 通过转换名获取作业id
     *
     * @param taskName
     * @return Integer
     */
    Integer getJobId(@Param("taskName") String taskName);

    /**
     * 获取作业详情
     *
     * @param id
     * @return List<JobDetails>
     */
    List<JobDetails> getJobDetails(@Param("id") Integer id);

    /**
     * 获取作业
     *
     * @param key
     * @return List<JobInfos>
     */
    List<JobInfos> getInfo(@Param("key") String key);

    /**
     * 获取作业总数
     *
     * @return
     */
    Integer getTotle();

    /**
     * 通过工作id更改工作状态
     *
     * @param code
     * @param jobId
     */
    boolean changeCode(@Param("code") Integer code, @Param("jobId") Integer jobId);

    /**
     * 开启增量作业
     *
     * @param flag
     * @param jobId
     */
    boolean updateJobFlag(@Param("flag") String flag, @Param("jobId") Integer jobId,@Param("dealTime") String dealTime);

    /**
     * 秀爱任务flag，全量为2，增量为1
     *
     * @param flag
     * @param jobId
     */
    boolean updateTaskFlag(@Param("flag") String flag, @Param("jobId") Integer jobId);

    /**
     * 开启全量作业
     * @param flag
     * @param jobId
     */
    boolean updateFullJobFlag(@Param("flag") String flag,@Param("jobId") Integer jobId);

    /**
     * 获取该作业任务的日志
     * @param id
     * @return
     */
    List<ExSyncLog> getJobLogs(@Param("id") Integer id);
}
