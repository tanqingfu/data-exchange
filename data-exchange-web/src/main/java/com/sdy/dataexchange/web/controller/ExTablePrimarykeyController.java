package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.service.ExTablePrimarykeyService;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExTablePrimarykey;
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
 * @since 2019-08-26
 */
@Slf4j
@Controller
@RequestMapping("/exTablePrimarykey")
public class ExTablePrimarykeyController extends BaseController {
    @Autowired
    private ExTablePrimarykeyService exTablePrimarykeyService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_table_primarykey/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_table_primarykey/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", exTablePrimarykeyService.getById(id));
        return "/page/ex_table_primarykey/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", exTablePrimarykeyService.getById(id));
        return "/page/ex_table_primarykey/update";
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
    public Response pageData(HttpServletRequest request, Page<ExTablePrimarykey> page) {
        QueryWrapper<ExTablePrimarykey> wrapper = new QueryWrapper<>();

        String id = request.getParameter("id");
        wrapper.eq(StringUtil.isNotBlank(id), "id", id);

        String key = request.getParameter("key");
        wrapper.eq(StringUtil.isNotBlank(key), "id", key);

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

        wrapper.orderByDesc("id");

        IPage<ExTablePrimarykey> result = exTablePrimarykeyService.page(page, wrapper);
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
    public Response save(ExTablePrimarykey exTablePrimarykey) {
        return Response.success(exTablePrimarykeyService.save(exTablePrimarykey));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExTablePrimarykey exTablePrimarykey) {
        return Response.success(exTablePrimarykeyService.updateById(exTablePrimarykey));
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
        return Response.success(exTablePrimarykeyService.removeById(id));
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
            exTablePrimarykeyService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }
}
