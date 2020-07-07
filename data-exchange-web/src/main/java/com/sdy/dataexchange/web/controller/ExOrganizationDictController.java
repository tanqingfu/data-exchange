package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExOrganizationDict;
import com.sdy.dataexchange.biz.service.ExOrganizationDictService;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

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
@RequestMapping("/exOrganizationDict")
public class ExOrganizationDictController extends BaseController {
    @Autowired
    private ExOrganizationDictService exOrganizationDictService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_organization_dict/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_organization_dict/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", exOrganizationDictService.getById(id));
        return "/page/ex_organization_dict/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", exOrganizationDictService.getById(id));
        return "/page/ex_organization_dict/update";
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
    public Response pageData(HttpServletRequest request, Page<ExOrganizationDict> page) {
        QueryWrapper<ExOrganizationDict> wrapper = new QueryWrapper<>();

        String orgId = request.getParameter("orgId");
        wrapper.eq(StringUtil.isNotBlank(orgId), "orgId", orgId);

        String key = request.getParameter("key");
        wrapper.eq(StringUtil.isNotBlank(key), "orgId", key);

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

        wrapper.orderByDesc("orgId");

        IPage<ExOrganizationDict> result = exOrganizationDictService.page(page, wrapper);
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
    public Response save(ExOrganizationDict exOrganizationDict) {
        return Response.success(exOrganizationDictService.save(exOrganizationDict));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExOrganizationDict exOrganizationDict) {
        return Response.success(exOrganizationDictService.updateById(exOrganizationDict));
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
        return Response.success(exOrganizationDictService.removeById(id));
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
            exOrganizationDictService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }

    /**
     * 获取所有科室信息
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAllOffice", method = RequestMethod.GET)
    public List getAllOffice(Integer id) {
        return exOrganizationDictService.getAllOffice(id);
    }

    /**
     * 获取所有省市区信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAllOrganzitions", method = RequestMethod.GET)
    public Response<List> getAllOrganzitions() {
        return exOrganizationDictService.getAllOrganization();

    }
}
