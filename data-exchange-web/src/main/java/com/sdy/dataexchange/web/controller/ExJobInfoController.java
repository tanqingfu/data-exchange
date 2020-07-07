package com.sdy.dataexchange.web.controller;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.BizException;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.core.nacos.NacosService;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.service.ExJobInfoService;
import com.sdy.dataexchange.biz.service.ExJobTaskService;
import com.sdy.dataexchange.core.JobContainer;
import com.sdy.dataexchange.plugin.config.PluginConfig;
import com.sdy.mvc.controller.BaseController;
import com.sdy.redis.service.RedisService;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wyy
 * @since 2019-09-12
 */
@Slf4j
@Controller
@RequestMapping("/exJobInfo")
public class ExJobInfoController extends BaseController {
    @Autowired
    private ExJobInfoService exJobInfoService;
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    private NacosService nacosService;
    @Autowired
    private RedisService redisService;

    /**
     * 获取所有任务名
     *
     * @return
     */
    @GetMapping("/getAllTasks")
    @ResponseBody
    public List getAllTasks() {
        return exJobInfoService.getAllTasks();
    }

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/jobPage";
    }

    /**
     * 页面
     */
    @GetMapping("/jobType")
    public String jobType() {
        return "/page/ex_job_info/jobType";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_job_info/new";
    }

    /**
     * 页面
     *
     * @return String
     */
    @GetMapping("/jobPage")
    public String jobPage() {
        return "/page/ex_job_info/page";
    }

    /**
     * 详情页面
     */
    @GetMapping("/jobPage/detailPage")
    public String detailPage(Model model, Integer id) {
        if (id != null) {
            //通过作业id获取作业详情
            List<JobDetails> jobDetails = exJobInfoService.getJobDetails(id);
            Map<String, Object> info1 = new HashMap<>(16);
            if (!jobDetails.isEmpty()) {
                for (JobDetails info : jobDetails) {
                    String state = info.getJobState();
                    JobInfos infos = new JobInfos();
                    infos.setJobDesc(info.getJobDesc());
                    infos.setJobState(info.getJobState());
                    infos.setValidFlag(info.getValidFlag());
                    infos.setJobId(info.getJobId());
                    infos.setJobStatus(info.getJobStatus());
                    infos.setJobName(info.getJobName());
                    getJobStatus(infos, state);
                    Pair<Long, Long> syncReadProcess = JobContainer.getInstance().getFullSyncReadProcess(info.getTaskId().toString(), jobId -> new Pair<>(
                            redisService.getOrDefault(RedisConstants.REDIS_FULL_SYNC_COUNT_READ + jobId, Integer.class, -1).longValue(),
                            redisService.getOrDefault(RedisConstants.REDIS_FULL_SYNC_COUNT_TOTAL + jobId, Integer.class, -1).longValue()
                    ));
                    Pair<Long, Long> syncWriteProcess = JobContainer.getInstance().getFullSyncWriteProcess(info.getTaskId().toString(), jobId -> new Pair<>(
                            redisService.getOrDefault(RedisConstants.REDIS_FULL_SYNC_COUNT + jobId, Integer.class, -1).longValue(),
                            redisService.getOrDefault(RedisConstants.REDIS_FULL_SYNC_COUNT_TOTAL + jobId, Integer.class, -1).longValue()
                    ));
                    if (syncReadProcess!= null) {
                        info.setSyncReadCurrent(syncReadProcess.getKey());
                        info.setSyncReadTotal(syncReadProcess.getValue());
                    }
                    if (syncWriteProcess!= null) {
                        info.setSyncWriteCurrent(syncWriteProcess.getKey());
                        info.setSyncWriteTotal(syncWriteProcess.getValue());
                    }
                }
                info1.put("jobName", jobDetails.get(0).getJobName());
                info1.put("jobDesc", jobDetails.get(0).getJobDesc());
                String state = jobDetails.get(0).getJobState();
                //通过获得的作业state返回给前端作业状态
                if (("1").equals(state) && "1".equals(jobDetails.get(0).getValidFlag())) {
                    state = "增量迁移待作业";
                    info1.put("jobState", state);
                } else if (("2").equals(state) && "1".equals(jobDetails.get(0).getValidFlag())) {
                    state = "增量迁移正在作业";
                    info1.put("jobState", state);
                } else if (("3").equals(state) && "1".equals(jobDetails.get(0).getValidFlag())) {
                    state = "增量迁移作业完成";
                    info1.put("jobState", state);
                } else if (("4").equals(state) && "1".equals(jobDetails.get(0).getValidFlag())) {
                    state = "增量迁移作业失败";
                    info1.put("jobState", state);
                } else if (("5").equals(state) && "1".equals(jobDetails.get(0).getValidFlag())) {
                    state = "增量迁移暂停作业";
                    info1.put("jobState", state);
                } else if (("1").equals(state) && "2".equals(jobDetails.get(0).getValidFlag())) {
                    state = "全量迁移待作业";
                    info1.put("jobState", state);
                } else if (("2").equals(state) && "2".equals(jobDetails.get(0).getValidFlag())) {
                    state = "全量迁移正在作业";
                    info1.put("jobState", state);
                } else if (("3").equals(state) && "2".equals(jobDetails.get(0).getValidFlag())) {
                    state = "全量迁移作业完成";
                    info1.put("jobState", state);
                } else if (("4").equals(state) && "2".equals(jobDetails.get(0).getValidFlag())) {
                    state = "全量迁移作业失败";
                    info1.put("jobState", state);
                } else if (("5").equals(state) && "2".equals(jobDetails.get(0).getValidFlag())) {
                    state = "全量迁移暂停作业";
                    info1.put("jobState", state);
                }
                info1.put("ip", jobDetails.get(0).getIp());
                info1.put("validFlag", jobDetails.get(0).getValidFlag());
                if ("1".equals(jobDetails.get(0).getValidFlag())) {
                    info1.put("dealTime", jobDetails.get(0).getDealTime());
                }
            }
            //获取作业详情中的任务日志
            List<ExSyncLog> jobLogs = exJobInfoService.getJobLogs(id);
            //作业名称，描述，状态，所属交换节点，ip，增量作业的开始时间信息
            model.addAttribute("info", info1);
            //作业中的任务信息
            model.addAttribute("param", jobDetails);
            //作业中任务的执行日志
            model.addAttribute("jobLogs", jobLogs);
        }
        return "/page/ex_job_info/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/jobPage/updatePage")
    public String updatePage(Model model, Integer id) {
        if (id != null) {
            JobInfos jobById = exJobInfoService.getJobById(id);
            model.addAttribute("param", jobById);
        }
        return "/page/ex_job_info/update";
    }

    /**
     * 分页数据
     *
     * @param request
     * @param page    [current, size]
     * @return Response
     */
    @GetMapping("/pageData")
    @ResponseBody
    public Response pageData(HttpServletRequest request, Page<ExJobInfo> page) {
        QueryWrapper<ExJobInfo> wrapper = new QueryWrapper<>();

        String jobId = request.getParameter("jobId");
        wrapper.eq(StringUtil.isNotBlank(jobId), "job_id", jobId);
        //通过作业名称搜索
        String key = request.getParameter("jobName");
        if (com.sdy.common.utils.StringUtil.isNotBlank(key)) {
            List<JobInfos> jobInfos = exJobInfoService.getInfo(key);
            if (!jobInfos.isEmpty()) {
                for (JobInfos info : jobInfos) {
                    String state = info.getJobState();
                    getJobStatus(info, state);
                }
                return Response.success(new Page<JobInfos>(page.getCurrent(), page.getSize(), jobInfos.size()).setRecords(jobInfos));
            } else {
                return Response.success(new Page<JobInfos>(page.getCurrent(), page.getSize(), jobInfos.size()).setRecords(new ArrayList<>()));
            }
        }

        wrapper.eq(StringUtil.isNotBlank(key), "job_name", key);

        String createTime = request.getParameter("createTime");
        if (StringUtil.isNotBlank(createTime)) {
            wrapper.between("create_time",
                    DateUtil.getDate(createTime + " 00:00:00", DateUtil.DATETIME_FORMAT),
                    DateUtil.getDate(createTime + " 23:59:59", DateUtil.DATETIME_FORMAT));
        }

        String createTimeRange = request.getParameter("createTimeRange");
        if (StringUtil.isNotBlank(createTimeRange)) {
            String[] timeRangeStrs = createTimeRange.split(" - ");
            wrapper = wrapper.between("create_time",
                    DateUtil.getDate(timeRangeStrs[0], DateUtil.DATETIME_FORMAT),
                    DateUtil.getDate(timeRangeStrs[1], DateUtil.DATETIME_FORMAT));
        }
        //获取作业首页的分页信息
        Integer totle = exJobInfoService.getTotle();
        List<JobInfos> infos = exJobInfoService.getJob(page);
        if (!infos.isEmpty()) {
            for (JobInfos info : infos) {
                String state = info.getJobState();
                getJobStatus(info, state);
            }
        }
        return Response.success(new Page<JobInfos>(page.getCurrent(), page.getSize(), totle).setRecords(infos));

    }

    //返回给前端作业状态及操作作业状态的按钮状态码
    private void getJobStatus(JobInfos info, String state) {
        if (("1").equals(state) && "1".equals(info.getValidFlag())) {
            state = "增量迁移待作业";
            info.setJobState(state);
            info.setJobStatus(2);
        } else if (("2").equals(state) && "1".equals(info.getValidFlag())) {
            state = "增量迁移正在作业";
            info.setJobState(state);
            info.setJobStatus(1);
        } else if (("3").equals(state) && "1".equals(info.getValidFlag())) {
            state = "增量迁移作业完成";
            info.setJobState(state);
        } else if (("4").equals(state) && "1".equals(info.getValidFlag())) {
            state = "增量迁移作业失败";
            info.setJobState(state);
            info.setJobStatus(2);
        } else if (("5").equals(state) && "1".equals(info.getValidFlag())) {
            state = "增量迁移暂停作业";
            info.setJobState(state);
            info.setJobStatus(3);
        } else if (("1").equals(state) && "2".equals(info.getValidFlag())) {
            state = "全量迁移待作业";
            info.setJobState(state);
            info.setJobStatus(2);
        } else if (("2").equals(state) && "2".equals(info.getValidFlag())) {
            state = "全量迁移正在作业";
            info.setJobState(state);
            info.setJobStatus(1);
        } else if (("3").equals(state) && "2".equals(info.getValidFlag())) {
            state = "全量迁移作业完成";
            info.setJobState(state);
        } else if (("4").equals(state) && "2".equals(info.getValidFlag())) {
            state = "全量迁移作业失败";
            info.setJobState(state);
            info.setJobStatus(2);
        } else if (("5").equals(state) && "2".equals(info.getValidFlag())) {
            state = "全量迁移暂停作业";
            info.setJobState(state);
            info.setJobStatus(3);
        }
    }

    /**
     * 保存数据
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(TaskNames taskNames) throws BizException {
        return exJobInfoService.save(taskNames);
    }

    /**
     * 更新数据
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(Integer jobId, String jobName, String jobDesc, String ip) {
        if (jobId != null && StringUtils.isNotBlank(jobName) && StringUtils.isNotBlank(jobDesc)) {
            ExJobInfo exJobInfo = exJobInfoService.getOne(new LambdaQueryWrapper<ExJobInfo>()
                    .eq(ExJobInfo::getJobId, jobId));
            if (exJobInfo != null && exJobInfo.getJobState() != null) {
                //作业运行中或者暂停中不允许修改
                if (exJobInfo.getJobState() == 2 || exJobInfo.getJobState() == 5) {
                    return Response.error("请等待作业完成再进行编辑");
                } else {
                    if (!exJobInfo.getJobId().equals(jobId)) {
                        return Response.error("该作业名已存在，请重新修改");
                    } else {
                        Boolean updateResult = exJobInfoService.updateInfo(jobId, jobName, jobDesc, ip);
                        if (!updateResult) {
                            return Response.error();
                        } else {
                            return Response.success("更新成功");
                        }
                    }
                }
            } else {
                return Response.error("无法获取作业状态，请联系管理员");
            }
        } else {
            return Response.error("请填写完整信息");
        }
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer id) throws BizException {
        if (id == null) {
            return Response.error();
        }
        exJobInfoService.removeJob(Arrays.asList(id));
        return Response.success();
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] id) throws BizException {
        if (id == null) {
            return Response.error();
        }
        exJobInfoService.removeJob(Arrays.asList(id));
        return Response.success();
    }

    /**
     * 开启作业
     *
     * @param jobId
     * @param flag
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public Response start(Integer jobId, String flag, String dealTime) {
        if (jobId != null && flag != null) {
            List<ExJobTask> tasks = exJobTaskService.list(new LambdaQueryWrapper<ExJobTask>()
                    .eq(ExJobTask::getJobId, jobId));
            List<String> taskIds = new ArrayList<>();
            if (!tasks.isEmpty()) {
                for (ExJobTask task : tasks) {
                    String taskId = String.valueOf(task.getJobtaskId());
                    if (taskId != null) {
                        taskIds.add(taskId);
                    }
                }
                //全量开启
                if ("2".equals(flag)) {
                    boolean b = JobContainer.getInstance().postJobStart(jobId.toString(), taskIds, PluginConfig.SyncType.FULL);
                    if (b) {
                        //将作业状态改为全量
                        boolean jobResult = exJobInfoService.updateFullJobFlag(flag, jobId);
                        //将作业下的任务状态改为全量
                        boolean taskResult = exJobInfoService.updateTaskFlag(flag, jobId);
                        //将作业状态改为开启
                        boolean startResult = exJobInfoService.changeCode(2, jobId);
                        if (!jobResult || !taskResult || !startResult) {
                            return Response.error("启动全量作业失败，请稍后重试");
                        } else {
                            return Response.success("全量作业启动成功");
                        }
                    } else {
                        return Response.error("启动全量作业失败，请稍后重试");
                    }
                } else if ("1".equals(flag)) {
                    //增量开启
                    boolean b = JobContainer.getInstance().postJobStart(jobId.toString(), taskIds, PluginConfig.SyncType.APPEND);
                    if (b) {
                        if ("".equals(dealTime) || dealTime == null) {
                            boolean jobResult = exJobInfoService.updateFullJobFlag(flag, jobId);
                            //将作业状态改为增量
                            boolean taskResult = exJobInfoService.updateTaskFlag(flag, jobId);
                            //将作业状态改为开启
                            boolean startResult = exJobInfoService.changeCode(2, jobId);
                            if (!jobResult || !taskResult || !startResult) {
                                return Response.error("启动增量作业失败，请稍后重试");
                            } else {
                                return Response.success("增量作业启动成功");
                            }
                        } else if (StringUtils.isNotBlank(dealTime)) {
                            //将作业状态改为增量
                            boolean jobResult = exJobInfoService.updateJobFlag(flag, jobId, dealTime);
                            //将作业状态改为增量
                            boolean taskResult = exJobInfoService.updateTaskFlag(flag, jobId);
                            //将作业状态改为开启
                            boolean startResult = exJobInfoService.changeCode(2, jobId);
                            if (!jobResult || !taskResult || !startResult) {
                                return Response.error("启动增量作业失败，请稍后重试");
                            } else {
                                return Response.success("增量作业启动成功");
                            }
                        }
                    } else {
                        return Response.error("启动增量作业失败，请稍后重试");
                    }
                }
            }
        } else {
            return Response.error("启动作业出错，请联系管理员");
        }
        return Response.success();
    }

    /**
     * 暂停作业
     *
     * @param jobId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/suspend", method = RequestMethod.POST)
    public Response suspend(Integer jobId) {
        if (jobId != null) {
            if (exJobInfoService.changeCode(5, jobId)) {
                return Response.success();
            } else {
                return Response.error("暂停作业出错，请联系管理员");
            }
        } else {
            return Response.error("无法获取该作业id，请联系管理员");
        }
    }

    /**
     * 继续作业
     *
     * @param jobId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/proceed", method = RequestMethod.POST)
    public Response proceed(Integer jobId) {
        if (jobId != null) {
            if (exJobInfoService.changeCode(2, jobId)) {
                return Response.success();
            } else {
                return Response.error("继续作业出错，请联系管理员");
            }
        } else {
            return Response.error("无法获取该作业id，请联系管理员");
        }
    }

    /**
     * 停止作业
     *
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    public Response stop(HttpServletRequest request) {
        Integer jobId = Integer.valueOf(request.getParameter("jobId"));
        if (jobId != null) {
            if (exJobInfoService.changeCode(1, jobId)) {
                return Response.success();
            } else {
                return Response.error("停止作业出错，请联系管理员");
            }
        } else {
            return Response.error("无法获取该作业id，请联系管理员");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/getAllIp", method = RequestMethod.GET)
    public Response getAllIp() {
        List<Map<String, Object>> ips = new ArrayList();
        try {
            List<Instance> instances = nacosService.listInstance();
            if (!instances.isEmpty()) {
                for (Instance instance : instances) {
                    Map<String, Object> ip = new HashMap<>(16);
                    ip.put("ip", instance.getIp() + ":" + instance.getPort());
                    ip.put("enabled", instance.isEnabled());
                    ips.add(ip);
                }
            }
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return Response.success(ips);
    }
}
