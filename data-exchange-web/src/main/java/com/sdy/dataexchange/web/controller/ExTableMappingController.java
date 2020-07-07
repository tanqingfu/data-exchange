package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.biz.model.TableMappingResult;
import com.sdy.dataexchange.biz.model.TaskInfoResult;
import com.sdy.dataexchange.biz.service.ExTableMappingService;
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
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
@Slf4j
@Controller
@RequestMapping("/exTableMapping")
public class ExTableMappingController extends BaseController {
    @Autowired
    private ExTableMappingService exTableMappingService;


    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_table_mapping/new";
    }

    /**
     * 页面
     * @return
     */
    @GetMapping("/taskPage")
    public String taskPage() {
        return "/page/ex_job_task/page";
    }

    /**
     * 详情页面
     */
    @GetMapping("/taskPage/detailPage")
    public String detailPage(Model model,Integer id) {
        List<TaskInfoResult> taskInfoResult = exTableMappingService.getInfo(id);
        Map<String, Object> info = new HashMap<>(16);
        if (!taskInfoResult.isEmpty()) {
            info.put("taskName", taskInfoResult.get(0).getTaskName());
            info.put("sourceDbName", taskInfoResult.get(0).getSourceDbName());
            info.put("sourceUser", taskInfoResult.get(0).getSourceUser());
            info.put("sourceTable", taskInfoResult.get(0).getSourceTable());
            info.put("destDbName", taskInfoResult.get(0).getDestDbName());
            info.put("destUser", taskInfoResult.get(0).getDestUser());
            info.put("destTable", taskInfoResult.get(0).getDestTable());
        }
            model.addAttribute("info", info);
            model.addAttribute("param", taskInfoResult);
        return "/page/ex_table_mapping/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        TableMappingResult tableMappingResult = exTableMappingService.getOneInfo(id);
        if (tableMappingResult!=null) {
            if (("1").equals(tableMappingResult.getValidFlag())) {
                tableMappingResult.setValidFlag("映射正常");
            } else if (("0").equals(tableMappingResult.getValidFlag())) {
                tableMappingResult.setValidFlag("映射异常");
            }
        }
        model.addAttribute("param", tableMappingResult);
        return "/page/ex_table_mapping/update";
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
    public Response pageData(HttpServletRequest request, Page<ExTableMapping> page) {
        QueryWrapper<ExTableMapping> wrapper = new QueryWrapper<>();

        String key = request.getParameter("taskName");
        if (StringUtil.isNotBlank(key)) {
            List<TableMappingResult> taskInfos = exTableMappingService.getInfos(key,page);
            if (!taskInfos.isEmpty()) {
                forTaskInfos(taskInfos);
                return Response.success(new Page<TableMappingResult>(page.getCurrent(), page.getSize(), taskInfos.size()).setRecords(taskInfos));
            } else {
                return Response.success(new Page<TableMappingResult>(page.getCurrent(), page.getSize(), 1).setRecords(new ArrayList<>()));
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

        wrapper.orderByDesc("task_name");

        Integer totle=exTableMappingService.getAll();
        List<TableMappingResult> infos = exTableMappingService.getMapping(page);
        for (TableMappingResult info: infos
             ) {
            if(("1").equals(info.getValidFlag())){
                info.setValidFlag("映射正常");
            }else if(("0").equals(info.getValidFlag())){
                info.setValidFlag("映射异常");
            }
        }
        return Response.success(new Page<TableMappingResult>(page.getCurrent(), page.getSize(), totle).setRecords(infos));
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExTableMapping exTableMapping) {
        if (exTableMapping == null) {
            return Response.error();
        }
        return Response.success(exTableMappingService.save(exTableMapping));

    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(TableMappingResult tableMappingResult) {
        if (tableMappingResult == null) {
            return Response.error();
        }
        return Response.success(exTableMappingService.updateInfo(tableMappingResult));
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
        exTableMappingService.removeTbMapping(Arrays.asList(id));
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
            exTableMappingService.removeTbMapping(Arrays.asList(id));
        }
        return Response.success();
    }
    private void forTaskInfos(List<TableMappingResult> infos) {
        for (TableMappingResult info: infos) {
            if(("1").equals(info.getValidFlag())){
                info.setValidFlag("任务正常");
            }else if(("0").equals(info.getValidFlag())){
                info.setValidFlag("任务异常");
            }
        }
    }
}
