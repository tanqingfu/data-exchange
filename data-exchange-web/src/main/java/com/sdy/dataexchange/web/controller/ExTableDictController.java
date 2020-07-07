package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.DbNameResult;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.service.ExTableDictService;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author zzq
 * @since 2019-07-23
 */
@Slf4j
@Controller
@RequestMapping("/exTableDict")
public class ExTableDictController extends BaseController {
    @Autowired
    private ExTableDictService exTableDictService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_table_dict/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_table_dict/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer syncId) {
        DbNameResult dbNameResult = exTableDictService.getInfo(syncId);
        model.addAttribute("param", dbNameResult);
        return "/page/ex_table_dict/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer syncId) {
        DbNameResult dbNameResult = exTableDictService.getInfo(syncId);
        model.addAttribute("param", dbNameResult);
        return "/page/ex_table_dict/update";
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
    public Response pageData(HttpServletRequest request, Page<ExTableDict> page) {
        QueryWrapper<ExTableDict> wrapper = new QueryWrapper<>();

        String syncId = request.getParameter("dbTable");
        wrapper.eq(StringUtil.isNotBlank(syncId), "db_table", syncId);

        String key = request.getParameter("dbTable");
        wrapper.eq(StringUtil.isNotBlank(key), "db_table", key);

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

        wrapper.orderByDesc("db_table");

        Integer totle = exTableDictService.getTotle();
        List<DbNameResult> infos = exTableDictService.getDbName(page);
        return Response.success(new Page<DbNameResult>(page.getCurrent(), page.getSize(), totle).setRecords(infos));
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExTableDict exTableDict) {
        if (exTableDict == null) {
            return Response.error();
        }
        return Response.success(exTableDictService.save(exTableDict));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExTableDict exTableDict) {
        if (exTableDict == null) {
            return Response.error();
        }
        return Response.success(exTableDictService.updateById(exTableDict));
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer syncId) {
//        ExTableDict exTableDict = exTableDictService.getOne(new QueryWrapper<ExTableDict>().eq("sync_id", syncId));
        return Response.success(exTableDictService.removeById(syncId));
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] syncIds) {
        if (syncIds == null) {
            return Response.error();
        }
        for (Integer syncId : syncIds) {
            ExTableDict exTableDict = exTableDictService.getOne(new QueryWrapper<ExTableDict>().eq("sync_id", syncId));
            if (exTableDict == null) {
                return Response.error();
            }
            exTableDictService.removeById(exTableDict);
        }

        return Response.success();
    }

    /**
     * 获取目标表及字段信息
     * @param destDbId
     * @return
     */

    @ResponseBody
    @RequestMapping(value = "getDestTablesAndFields", method = RequestMethod.GET)
    public Response getDestTablesAndFields(Integer destDbId) {
        return exTableDictService.getDestTablesAndFields(destDbId);

    }

    /**
     * 获取源表及字段信息
     * @param sourceDbId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSourceTablesAndFields",method = RequestMethod.GET)
    public Response getSourceTablesAndFields(Integer sourceDbId,Integer gatherId){
        return exTableDictService.getSourceTablesAndFields(sourceDbId,gatherId);

    }

}
