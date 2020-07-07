package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.BO.FieldBO;
import com.sdy.dataexchange.biz.model.ExFieldDict;
import com.sdy.dataexchange.biz.model.TableNameResult;
import com.sdy.dataexchange.biz.service.ExFieldDictService;
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
 * @since 2019-07-22
 */
@Slf4j
@Controller
@RequestMapping("/exFieldDict")
public class ExFieldDictController extends BaseController {
    @Autowired
    private ExFieldDictService exFieldDictService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_field_dict/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_field_dict/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer syncSeqno) {
        TableNameResult tableNameResult = exFieldDictService.getInfo(syncSeqno);
        model.addAttribute("param", tableNameResult);
        return "/page/ex_field_dict/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer syncSeqno) {
        TableNameResult tableNameResult = exFieldDictService.getInfo(syncSeqno);
        model.addAttribute("param", tableNameResult);
        return "/page/ex_field_dict/update";
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
    public Response pageData(HttpServletRequest request, Page<ExFieldDict> page) {
        QueryWrapper<ExFieldDict> wrapper = new QueryWrapper<>();
        String syncSeqno = request.getParameter("syncId");
        wrapper.eq(StringUtil.isNotBlank(syncSeqno), "sync_id", syncSeqno);

        String key = request.getParameter("syncId");
        wrapper.eq(StringUtil.isNotBlank(key), "sync_id", key);

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

        wrapper.orderByDesc("sync_id");

        Integer totle=exFieldDictService.getTotle();
        List<TableNameResult> infos = exFieldDictService.getTableName(page);
        return Response.success(new Page<TableNameResult>(page.getCurrent(), page.getSize(), totle).setRecords(infos));
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExFieldDict exFieldDict) {
        if (exFieldDict == null) {
            return Response.error();
        }
        return Response.success(exFieldDictService.save(exFieldDict));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExFieldDict exFieldDict) {
        if (exFieldDict == null) {
            return Response.error();
        }
        return Response.success(exFieldDictService.updateById(exFieldDict));
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer syncSeqno) {
        if (syncSeqno == null) {
            return Response.error();
        }
        ExFieldDict exFieldDict = exFieldDictService.getOne(new QueryWrapper<ExFieldDict>().eq("sync_seqno", syncSeqno));
        if (exFieldDict == null) {
            return Response.error();
        }
        return Response.success(exFieldDictService.removeById(syncSeqno));
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] syncSeqnos) {
        if (syncSeqnos == null) {
            return Response.error();
        }
        for (Integer syncSeqno : syncSeqnos) {
            ExFieldDict exFieldDict = exFieldDictService.getOne(new QueryWrapper<ExFieldDict>().eq("sync_seqno", syncSeqno));
            if (exFieldDict == null) {
                return Response.error();
            }
            exFieldDictService.removeById(exFieldDict);
        }

        return Response.success();
    }

    /**
     * 获取所有字段信息
     * @param fieldBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getAllSyncFields", method = RequestMethod.GET)
    public Response getAllSyncFields(FieldBO fieldBO) {
        return exFieldDictService.getAllSyncFields(fieldBO);

    }

}
