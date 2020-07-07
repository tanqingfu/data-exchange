package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.BO.TaskBO;
import com.sdy.dataexchange.biz.model.ExJobInfo;
import com.sdy.dataexchange.biz.model.ExJobTask;
import com.sdy.dataexchange.biz.model.TaskInfoDetails;
import com.sdy.dataexchange.biz.model.TaskInfos;
import com.sdy.dataexchange.biz.service.ExJobInfoService;
import com.sdy.dataexchange.biz.service.ExJobTaskService;
import com.sdy.dataexchange.biz.service.ExTableMappingService;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/exJobTask")
public class ExJobTaskController extends BaseController {
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    ExTableMappingService exTableMappingService;
    @Autowired
    ExJobInfoService exJobInfoService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_job_task/page";
    }

    @GetMapping("/page/additionalTasks")
    public String additionalTasks() {
        return "/page/additionalTasks";
    }

    /**
     * 新建页面
     */
    @GetMapping("/page/newPage")
    public String newPage() {
        return "/page/ex_job_task/new";
    }

    /**
     * 编辑页面
     */
    @GetMapping("/page/editTask")
    public String editTask(Integer jobtaskId, Model model) {
        model.addAttribute("param", exTableMappingService.getOneInfo(jobtaskId));
        return "/page/ex_job_task/update";
    }

    /**
     * 详情页面
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/page/detailPage")
    public String detail(Model model, Integer id) {
        if (id != null) {
            List<TaskInfoDetails> oneTasks = exJobTaskService.getOneTask(id);
            TaskInfoDetails oneTask = new TaskInfoDetails();
            Map<String, Object> info = new HashMap<>(16);
            if (!oneTasks.isEmpty()) {
                for (int i = 0; i < oneTasks.size(); i++) {
                    oneTask = oneTasks.get(i);
                    if (("1").equals(oneTask.getValidFlag()) && oneTask.getJobId() != null) {
                        oneTask.setValidFlag("增量任务正常");
                    } else if (("2").equals(oneTask.getValidFlag()) && oneTask.getJobId() != null) {
                        oneTask.setValidFlag("全量任务正常");
                    } else if (("0").equals(oneTask.getValidFlag())) {
                        oneTask.setValidFlag("任务异常");
                    } else if (("1").equals(oneTask.getValidFlag()) && oneTask.getJobId() == null) {
                        oneTask.setValidFlag("任务等待添加到作业中");
                    }
                    info.put("jobName", oneTask.getJobName());
                    info.put("jobtaskName", oneTask.getJobtaskName());
                    info.put("sourceDbName", oneTask.getSourceDbName());
                    info.put("sourceDbIp", oneTask.getSourceDbIp());
                    info.put("sourceDbPort", oneTask.getSourceDbPort());
                    info.put("sourceDbUser", oneTask.getSourceDbUser());
                    info.put("sourceDb", oneTask.getSourceDb());
                    info.put("sourceTb", oneTask.getSourceTb());
                    info.put("destDbName", oneTask.getDestDbName());
                    info.put("destDbIp", oneTask.getDestDbIp());
                    info.put("destDbPort", oneTask.getDestDbPort());
                    info.put("destDbUser", oneTask.getDestDbUser());
                    info.put("destDb", oneTask.getDestDb());
                    info.put("destTb", oneTask.getDestTb());
                    info.put("validFlag", oneTask.getValidFlag());
                    info.put("gatherDesc",oneTask.getGatherDesc());
                }
            }
            model.addAttribute("info", info);
            model.addAttribute("param", oneTasks);
            return "/page/ex_job_task/detail";
        } else {
            return "无法获取交换任务信息";
        }
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id, Page page) {
        if (id != null) {
            model.addAttribute("param", exJobTaskService.getOneTask(id));
        }
        return "/page/ex_job_task/update";
    }

    /**
     * 分页数据
     *
     * @param request
     * @param page    [current, size]
     * @return
     */
    @GetMapping("/pageData")
    @ResponseBody
    public Response pageData(HttpServletRequest request, Page<ExJobTask> page) {
        QueryWrapper<ExJobTask> wrapper = new QueryWrapper<>();

        String key = request.getParameter("taskName");
        if (StringUtil.isNotBlank(key)) {
            List<TaskInfos> taskInfos = exJobTaskService.getInfo(key);
            if (!taskInfos.isEmpty()) {
                forTaskInfos(taskInfos);
                return Response.success(new Page<TaskInfos>(page.getCurrent(), page.getSize(), taskInfos.size()).setRecords(taskInfos));
            } else {
                return Response.success(new Page<TaskInfos>(page.getCurrent(), page.getSize(), 1).setRecords(new ArrayList<>()));
            }
        }
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

        wrapper.orderByDesc("jobtask_id");
        //获取任务首页的分页信息
        Integer totle = exJobTaskService.getTotle();
        List<TaskInfos> infos = exJobTaskService.getTask(page);
        if (!infos.isEmpty()) {
            forTaskInfos(infos);
        }
        return Response.success(new Page<TaskInfos>(page.getCurrent(), page.getSize(), totle).setRecords(infos));
    }

    /**
     * 获取任务状态
     *
     * @param infos
     */
    private void forTaskInfos(List<TaskInfos> infos) {
        for (TaskInfos info : infos) {
            if (("1").equals(info.getValidFlag()) && info.getJobId() != null) {
                info.setValidFlag("增量任务正常");
            } else if (("2").equals(info.getValidFlag()) && info.getJobId() != null) {
                info.setValidFlag("全量任务正常");
            } else if (("0").equals(info.getValidFlag())) {
                info.setValidFlag("任务异常");
            } else if (("1").equals(info.getValidFlag()) && info.getJobId() == null) {
                info.setValidFlag("任务等待添加到作业中");
            }
        }
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExJobTask exJobTask) {
        if (exJobTask == null) {
            return Response.error();
        }
        return Response.success(exJobTaskService.save(exJobTask));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExJobTask exJobTask) {
        if (exJobTask == null) {
            return Response.error();
        }
        return Response.success(exJobTaskService.updateById(exJobTask));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer id) throws Exception {
        if (id == null) {
            return Response.error();
        }
        ExJobTask exJobTask = exJobTaskService.getById(id);
        if (exJobTask != null) {
            if (exJobTask.getJobId() != null) {
                ExJobInfo exJobInfo = exJobInfoService.getById(exJobTask.getJobId());
                if (exJobInfo != null) {
                    return Response.error("该任务存在于作业'" + exJobInfo.getJobName() + "'中，如需删除请先删除作业'"
                            + exJobInfo.getJobName() + "'");
                }
            } else {
                exJobTaskService.removeTask(Arrays.asList(id));
                return Response.success();
            }
        } else {
            return Response.error("无法找到该任务，请联系管理员");
        }
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
    public Response deleteBatch(Integer[] id) throws Exception {
        if (id != null) {
            for (Integer oneId:id) {
                ExJobTask exJobTask = exJobTaskService.getById(oneId);
                if (exJobTask != null) {
                    if (exJobTask.getJobId() != null) {
                        ExJobInfo exJobInfo = exJobInfoService.getById(exJobTask.getJobId());
                        if (exJobInfo != null) {
                            return Response.error("任务'"+exJobTask.getJobtaskName()+"'存在于作业'" + exJobInfo.getJobName() + "'中，如需删除请先删除作业'"
                                    + exJobInfo.getJobName() + "'");
                        }
                    }
                }
            }
            exJobTaskService.removeTask(Arrays.asList(id));
            return Response.success();
        }else {
            return Response.error();
        }
    }

    /**
     * 新增任务
     *
     * @param taskBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveTask", method = RequestMethod.POST)
    public Response saveTask(@RequestBody TaskBO taskBO) {
        return exJobTaskService.saveTask(taskBO);
    }

    /**
     * 编辑任务
     *
     * @param taskBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateTask", method = RequestMethod.POST)
    public Response updateTask(@RequestBody TaskBO taskBO) {
        return exJobTaskService.editTask(taskBO);
    }


    @ResponseBody
    @RequestMapping(value = "/getRowseq", method = RequestMethod.GET)
    public Response getRowseq(Integer destDbId, String destTableName) {
        return exJobTaskService.getRowseq(destDbId, destTableName);
    }
}
