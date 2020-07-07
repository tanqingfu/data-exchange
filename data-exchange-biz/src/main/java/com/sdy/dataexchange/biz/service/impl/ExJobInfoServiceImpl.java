package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.BizException;
import com.sdy.common.model.Response;
import com.sdy.common.utils.Assert;
import com.sdy.dataexchange.biz.mapper.ExJobInfoMapper;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.service.ExJobInfoService;
import com.sdy.dataexchange.biz.service.ExJobTaskService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wyy
 * @since 2019-09-08
 */
@Slf4j
@Service
public class ExJobInfoServiceImpl extends BaseServiceImpl<ExJobInfo> implements ExJobInfoService {
    @Autowired
    private ExJobInfoMapper exJobInfoMapper;
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    ExJobInfoService exJobInfoService;


    /**
     * 新建作业
     *
     * @param taskNames
     * @return
     */
    @Override
    public Response save(TaskNames taskNames) throws BizException {
        String validFlag = "1";
        if (!taskNames.getTaskName().isEmpty() && StringUtils.isNotBlank(taskNames.getJobDesc())
                && StringUtils.isNotBlank(taskNames.getJobName()) && StringUtils.isNotBlank(taskNames.getIp())) {
            //如果该作业名未存在，则新增
            Assert.notNull(exJobInfoMapper.getOneJob(taskNames.getJobName()), "该作业名已存在，请重新修改");
            Integer jobState = 1;
            for (String taskName : taskNames.getTaskName()) {
                ExJobTask exJobTask = exJobTaskService.getOne(new LambdaQueryWrapper<ExJobTask>()
                        .eq(ExJobTask::getJobtaskName, taskName));
                if (exJobTask == null) {
                    return Response.error("无法获取任务'" + taskName + "'信息，请联系管理员");
                }
                Integer jobTaskId = exJobTask.getJobId();
                Assert.notNull(jobTaskId, "所选任务" + taskName + "'已存在于其他作业");
                boolean jobResult = true;
                if (exJobInfoService.getOne(new LambdaQueryWrapper<ExJobInfo>()
                        .eq(ExJobInfo::getJobName, taskNames.getJobName())) == null) {
                    jobResult = exJobInfoMapper.newJob(taskNames.getJobName(), taskNames.getJobDesc()
                            , jobState, validFlag, taskNames.getIp());
                }
                Assert.notTrue(jobResult, "新建作业失败");
                ExJobInfo exJobInfo = exJobInfoService.getOne(new LambdaQueryWrapper<ExJobInfo>()
                        .eq(ExJobInfo::getJobName, taskNames.getJobName()));
                if (exJobInfo == null) {
                    return Response.error("新建作业失败");
                }
                Integer jobId = exJobInfo.getJobId();
                //作业添加成功后，将该作业下的任务的作业id改为该作业id
                Assert.isNull(jobId, "新建作业失败");
                boolean b = exJobInfoMapper.updateJobId(taskName, jobId);
                Assert.notTrue(b, "新建作业失败");
            }
        } else {
            return Response.error("请填写完整信息");
        }
        return Response.success();
    }

    /**
     * 删除作业
     *
     * @param jobIdList
     * @throws BizException
     */
    @Override
    public void removeJob(List<Integer> jobIdList) throws BizException {
        for (Integer jobId : jobIdList) {
            ExJobInfo jobInfo = getById(jobId);
            if (jobInfo != null && jobInfo.getJobState().equals(2) && "1".equals(jobInfo.getValidFlag())) {
                throw new BizException("作业[" + jobInfo.getJobName() + "]正在进行中，禁止删除。");
            }
            exJobTaskService.lambdaUpdate().set(ExJobTask::getJobId, null).eq(ExJobTask::getJobId, jobId).update();
        }
        if (!jobIdList.isEmpty()) {
            removeByIds(jobIdList);
        }
    }

    /**
     * select下拉框 查询 所有job名称
     */
    @Override
    public List<ExJobInfo> queryAllJob() {
        return exJobInfoMapper.queryAllJob();
    }

    @Override
    public List<JobInfos> getJob(Page page) {
        return exJobInfoMapper.getJob(page);
    }

    @Override
    public JobInfos getJobById(Integer id) {
        return exJobInfoMapper.getJobById(id);
    }

    @Override
    public Boolean updateInfo(Integer jobId, String jobName, String jobDesc, String ip) {
        return exJobInfoMapper.updateInfo(jobId, jobName, jobDesc, ip);
    }

    @Override
    public List<Map> getAllTasks() {
        return exJobInfoMapper.getAllTasks();
    }

    @Override
    public List<JobDetails> getJobDetails(Integer id) {
        return exJobInfoMapper.getJobDetails(id);
    }

    @Override
    public List<JobInfos> getInfo(String key) {
        return exJobInfoMapper.getInfo(key);
    }


    @Override
    public Integer getTotle() {
        return exJobInfoMapper.getTotle();
    }

    @Override
    public boolean changeCode(Integer code, Integer jobId) {
        return exJobInfoMapper.changeCode(code, jobId);
    }

    @Override
    public boolean updateJobFlag(String flag, Integer jobId, String dealTime) {
        return exJobInfoMapper.updateJobFlag(flag, jobId, dealTime);
    }

    @Override
    public boolean updateFullJobFlag(String flag, Integer jobId) {
        return exJobInfoMapper.updateFullJobFlag(flag, jobId);
    }

    @Override
    public List<ExSyncLog> getJobLogs(Integer id) {
        return exJobInfoMapper.getJobLogs(id);
    }

    @Override
    public boolean updateTaskFlag(String flag, Integer jobId) {
        return exJobInfoMapper.updateTaskFlag(flag, jobId);
    }

}
