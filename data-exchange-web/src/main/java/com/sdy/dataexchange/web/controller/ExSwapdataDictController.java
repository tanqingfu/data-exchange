package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.BizException;
import com.sdy.common.model.Response;
import com.sdy.common.utils.Assert;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.MapUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExJobInfo;
import com.sdy.dataexchange.biz.model.ExJobTask;
import com.sdy.dataexchange.biz.model.ExSwapdataDict;
import com.sdy.dataexchange.biz.service.ExJobInfoService;
import com.sdy.dataexchange.biz.service.ExJobTaskService;
import com.sdy.dataexchange.biz.service.ExSwapdataDictService;
import com.sdy.dataexchange.biz.service.JobService;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
@Slf4j
@Controller
@RequestMapping("/exSwapdataDict")
public class ExSwapdataDictController extends BaseController {
    @Autowired
    private ExSwapdataDictService exSwapdataDictService;
    @Autowired
    private ExJobInfoService exJobInfoService;
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    private JobService jobService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_swapdata_dict/page";
    }

    @GetMapping("/jobData")
    public String page2() {
        return "/page/ex_swapdata_dict/jobData";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_swapdata_dict/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", exSwapdataDictService.getById(id));
        return "/page/ex_swapdata_dict/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", exSwapdataDictService.getById(id));
        return "/page/ex_swapdata_dict/update";
    }


    /**
     * 分页数据
     *
     * @param request
     * @param page    [current, size]
     * @return
     */
    @GetMapping("/pageData2")
    @ResponseBody
    public Response pageData2(HttpServletRequest request, Page<Map<String, Object>> page) {
        String jobId = request.getParameter("jobId");
        Integer integer = exSwapdataDictService.querySwapdataSize2(jobId);
        List<Map> querySwapdata = exSwapdataDictService.querySwapdata2(jobId, page);
        return Response.success(new Page(page.getCurrent(), page.getSize(), integer).setRecords(querySwapdata));
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
    public Response pageData(HttpServletRequest request, Page<Map<String, Object>> page) throws ParseException {
        String jobtaskId = request.getParameter("jobtaskName");
        String createTimeRange = request.getParameter("createTimeRange");
        String sourceTabName = request.getParameter("sourceTabName");
        String liftCreateTime = "";
        String rightCreateTime = "";
        if (createTimeRange != null && !"".equals(createTimeRange)) {
            List list = new ArrayList();
            if (StringUtil.isNotBlank(createTimeRange)) {
                list = Arrays.asList(createTimeRange.split(" - "));
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            liftCreateTime = DateUtil.dateToString(simpleDateFormat.parse((String) list.get(0)), "yyyy_MM_dd_HH");
            rightCreateTime = DateUtil.dateToString(simpleDateFormat.parse((String) list.get(1)), "yyyy_MM_dd_HH");
        }

        Integer integer = exSwapdataDictService.querySwapdataSize(jobtaskId, sourceTabName, liftCreateTime, rightCreateTime);
        List<Map> querySwapdata = exSwapdataDictService.querySwapdata(jobtaskId, sourceTabName, liftCreateTime, rightCreateTime, page);
        return Response.success(new Page(page.getCurrent(), page.getSize(), integer).setRecords(querySwapdata));
    }


    /**
     * 下拉框选择作业名
     */

    @PostMapping("/queryJobAllInfo")
    @ResponseBody
    public Response queryJobAllInfo() {
        List<ExJobInfo> exJobInfos = exJobInfoService.queryAllJob();
        return Response.success(exJobInfos);
    }

    /**
     * 下拉框选择转换名
     */
    @PostMapping("/queryAllTask")
    @ResponseBody
    public Response queryAllTask() {
        List<ExJobTask> exJobTasks = exJobTaskService.queryAllTask();
        return Response.success(exJobTasks);
    }


    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExSwapdataDict exSwapdataDict) {
        return Response.success(exSwapdataDictService.save(exSwapdataDict));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExSwapdataDict exSwapdataDict) {
        return Response.success(exSwapdataDictService.updateById(exSwapdataDict));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer id) {
        return Response.success(exSwapdataDictService.removeById(id));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] id) {
        if (id != null) {
            exSwapdataDictService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }

    /**
     * 作业图页面
     *
     * @param model
     * @return
     */
    @GetMapping("/graph")
    public String graph(Model model) {
        List<ExJobInfo> jobInfoList = jobService.listJobs(null, null);
        model.addAttribute("jobInfoList", jobInfoList);
        return "/page/ex_swapdata_dict/graph";
    }

    /**
     * 获取作业统计信息
     * @param jobInfoId 作业id
     * @return
     * @throws BizException
     */
    @GetMapping("/graph/jobInfo")
    @ResponseBody
    public Response graphJobInfo(Integer jobInfoId) throws BizException {
        ExJobInfo jobInfo = jobService.getJobInfo(jobInfoId);
        Assert.isNull(jobInfo, "作业不存在");
        List<ExJobTask> taskList = exJobTaskService.lambdaQuery()
                .eq(ExJobTask::getJobId, jobInfoId)
                .list();
        jobInfo.setTaskList(taskList);
        List<ExSwapdataDict> stats = jobService.listByTimeRange(null, null, jobInfoId);
        Map<Integer, List<ExSwapdataDict>> statsmap = MapUtil.collectionToMapList(stats, ExSwapdataDict::getJobId);
        Set<String> hourSet = new HashSet<>();
        List<ExSwapdataDict> l = statsmap.getOrDefault(jobInfo.getJobId(), Collections.emptyList());
        Map<Integer, List<ExSwapdataDict>> m = MapUtil.collectionToMapList(l, ExSwapdataDict::getTaskId);
        jobInfo.getTaskList().forEach(task -> {
            List<ExSwapdataDict> lt = m.getOrDefault(task.getJobtaskId(), Collections.emptyList());
            task.setTaskStats(lt);
            hourSet.addAll(lt.stream().map(ExSwapdataDict::getSwaData).collect(Collectors.toSet()));
        });
        List<String> hourList = new ArrayList<>(hourSet);
        hourList.sort(Comparator.naturalOrder());
        jobInfo.setTaskHourList(hourList);
        jobInfo.getTaskList().forEach(task -> {
            List<ExSwapdataDict> ts = task.getTaskStats();
            Map<String, ExSwapdataDict> mp = MapUtil.collectionToMap(ts, ExSwapdataDict::getSwaData);
            hourList.forEach(hour -> mp.putIfAbsent(hour, new ExSwapdataDict().setSwaData(hour).setSwaGross(0).setTaskId(task.getJobtaskId())));
            List<ExSwapdataDict> ts2 = new ArrayList<>(mp.values());
            ts2.sort(Comparator.comparing(ExSwapdataDict::getSwaData));
            task.setTaskStats(ts2);
        });
        return Response.success(jobInfo);
    }
}
