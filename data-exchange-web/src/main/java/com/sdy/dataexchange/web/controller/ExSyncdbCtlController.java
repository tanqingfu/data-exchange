package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.service.ExSyncdbCtlService;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExSyncdbCtl;
import com.sdy.common.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.stereotype.Controller;
import com.sdy.mvc.controller.BaseController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

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
@RequestMapping("/exSyncdbCtl")
public class ExSyncdbCtlController extends BaseController {
    @Autowired
    private ExSyncdbCtlService exSyncdbCtlService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_syncdb_ctl/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_syncdb_ctl/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", exSyncdbCtlService.getById(id));
        return "/page/ex_syncdb_ctl/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", exSyncdbCtlService.getById(id));
        return "/page/ex_syncdb_ctl/update";
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
    public Response pageData(HttpServletRequest request, Page<ExSyncdbCtl> page) {
        QueryWrapper<ExSyncdbCtl> wrapper = new QueryWrapper<>();

        String syncSeqno = request.getParameter("syncSeqno");
        wrapper.eq(StringUtil.isNotBlank(syncSeqno), "sync_seqno", syncSeqno);

        String key = request.getParameter("key");
        wrapper.eq(StringUtil.isNotBlank(key), "sync_seqno", key);

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

        wrapper.orderByDesc("sync_seqno");

        IPage<ExSyncdbCtl> result = exSyncdbCtlService.page(page, wrapper);
        return Response.success(result);
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExSyncdbCtl exSyncdbCtl) {
        return Response.success(exSyncdbCtlService.save(exSyncdbCtl));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExSyncdbCtl exSyncdbCtl) {
        return Response.success(exSyncdbCtlService.updateById(exSyncdbCtl));
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
        return Response.success(exSyncdbCtlService.removeById(id));
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
            exSyncdbCtlService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }
}
