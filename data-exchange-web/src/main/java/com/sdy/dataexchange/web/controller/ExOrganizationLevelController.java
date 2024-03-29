package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.service.ExOrganizationLevelService;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExOrganizationLevel;
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
@RequestMapping("/exOrganizationLevel")
public class ExOrganizationLevelController extends BaseController {
    @Autowired
    private ExOrganizationLevelService exOrganizationLevelService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_organization_level/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_organization_level/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", exOrganizationLevelService.getById(id));
        return "/page/ex_organization_level/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", exOrganizationLevelService.getById(id));
        return "/page/ex_organization_level/update";
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
    public Response pageData(HttpServletRequest request, Page<ExOrganizationLevel> page) {
        QueryWrapper<ExOrganizationLevel> wrapper = new QueryWrapper<>();

        String orgLevel = request.getParameter("orgLevel");
        wrapper.eq(StringUtil.isNotBlank(orgLevel), "orgLevel", orgLevel);

        String key = request.getParameter("key");
        wrapper.eq(StringUtil.isNotBlank(key), "orgLevel", key);

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

        wrapper.orderByDesc("orgLevel");

        IPage<ExOrganizationLevel> result = exOrganizationLevelService.page(page, wrapper);
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
    public Response save(ExOrganizationLevel exOrganizationLevel) {
        return Response.success(exOrganizationLevelService.save(exOrganizationLevel));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExOrganizationLevel exOrganizationLevel) {
        return Response.success(exOrganizationLevelService.updateById(exOrganizationLevel));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(String id) {
        return Response.success(exOrganizationLevelService.removeById(id));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(String[] id) {
        if (id != null) {
            exOrganizationLevelService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }
}
