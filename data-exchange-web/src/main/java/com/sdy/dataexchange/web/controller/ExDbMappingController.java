package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.BO.DbMappingBO;
import com.sdy.dataexchange.biz.model.DbMappingResult;
import com.sdy.dataexchange.biz.model.ExDbMapping;
import com.sdy.dataexchange.biz.model.ExJobTask;
import com.sdy.dataexchange.biz.service.*;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
@Slf4j
@Controller
@RequestMapping("/exDbMapping")
public class ExDbMappingController extends BaseController {
    @Autowired
    private ExDbMappingService exDbMappingService;
    @Autowired
    ExGatherDictService exGatherDictService;
    @Autowired
    ExDbDictService exDbDictService;
    @Autowired
    JobService jobService;
    @Autowired
    ExJobTaskService exJobTaskService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_db_mapping/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_db_mapping/new";
    }

    @GetMapping("/page/addDbMappingPage")
    public String addDbMapping() {
        return "/page/ex_db_mapping/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        DbMappingResult dbMappingResult = exDbMappingService.getInfo(id);
        //获取库映射状态
        if ("0".equals(dbMappingResult.getValidFlag())) {
            dbMappingResult.setValidFlag("映射失败");
        } else if ("1".equals(dbMappingResult.getValidFlag())) {
            dbMappingResult.setValidFlag("映射正常");
        }
        model.addAttribute("param", dbMappingResult);
        return "/page/ex_db_mapping/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        DbMappingResult dbMappingResult = exDbMappingService.getInfo(id);
        //获取库映射状态
        if ("0".equals(dbMappingResult.getValidFlag())) {
            dbMappingResult.setValidFlag("映射失败");
        } else if ("1".equals(dbMappingResult.getValidFlag())) {
            dbMappingResult.setValidFlag("映射正常");
        }
        model.addAttribute("param", dbMappingResult);
        return "/page/ex_db_mapping/update";
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
    public Response pageData(HttpServletRequest request, Page<ExDbMapping> page) {
        QueryWrapper<ExDbMapping> wrapper = new QueryWrapper<>();

        String dbName = request.getParameter("dbName");
        if (StringUtils.isNotBlank(dbName)) {
            Integer result = exDbMappingService.getResult(dbName);
            List<DbMappingResult> infos = exDbMappingService.getByDbName(dbName);
            if (!infos.isEmpty()) {
                return Response.success(new Page<DbMappingResult>(page.getCurrent(), page.getSize(), result).setRecords(infos));
            } else {
                return Response.success(new Page<DbMappingResult>(page.getCurrent(), page.getSize(), 1).setRecords(new ArrayList<>()));
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

        wrapper.orderByDesc("gather_id");

        Integer result = exDbMappingService.getTotle();
        List<DbMappingResult> infos = exDbMappingService.getMapping(page);
        if(!infos.isEmpty()) {
            for (DbMappingResult info : infos) {
                if ("0".equals(info.getValidFlag())) {
                    info.setValidFlag("映射失败");
                } else if ("1".equals(info.getValidFlag())) {
                    info.setValidFlag("映射正常");
                }
            }
        }
        return Response.success(new Page<DbMappingResult>(page.getCurrent(), page.getSize(), result).setRecords(infos));
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExDbMapping exDbMapping) {
        if (exDbMapping == null) {
            return Response.error();
        }
        return Response.success(exDbMappingService.save(exDbMapping));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExDbMapping exDbMapping) {
        if (exDbMapping == null) {
            return Response.error();
        }
        return Response.success(exDbMappingService.updateById(exDbMapping));
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
        List<ExJobTask> jobTasks=exJobTaskService.list(new LambdaQueryWrapper<ExJobTask>().eq(ExJobTask::getDbmapId,id));
        if (!jobTasks.isEmpty()){
            return Response.error("该库映射下存在任务，请删除该库映射下的所有任务再尝试");
        }else {
            exDbMappingService.removeDbMapping(Arrays.asList(id));
            return Response.success();
        }
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
            for (Integer dbId:id) {
                List<ExJobTask> jobTasks=exJobTaskService.list(new LambdaQueryWrapper<ExJobTask>().eq(ExJobTask::getDbmapId,dbId));
                if (!jobTasks.isEmpty()){
                    return Response.error("所选库映射下存在任务，请删除库映射下的所有任务再尝试");
                }
            }
        }
        exDbMappingService.removeDbMapping(Arrays.asList(id));
        return Response.success();
    }

    /**
     * 新增库映射
     *
     * @param dbMappingBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addDbMapping", method = RequestMethod.POST)
    public Response addDbMapping(DbMappingBO dbMappingBO) {
        if (dbMappingBO.getGatherDesc() == null || Integer.parseInt(dbMappingBO.getGatherDesc()) <= 0) {
            return Response.error("请选择交换节点");
        }
        return exDbMappingService.addDbMapping(dbMappingBO);

    }
    /**
     * 编辑库映射
     *
     * @param dbMappingBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/updateDbMapping", method = RequestMethod.POST)
    public Response updateDbMapping(DbMappingBO dbMappingBO) {
        return exDbMappingService.updateDbMapping(dbMappingBO);

    }
}
