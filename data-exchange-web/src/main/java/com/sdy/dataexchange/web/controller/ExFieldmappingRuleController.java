package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.service.ExFieldmappingRuleService;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExFieldmappingRule;
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
@RequestMapping("/exFieldmappingRule")
public class ExFieldmappingRuleController extends BaseController {
    @Autowired
    private ExFieldmappingRuleService exFieldmappingRuleService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_fieldmapping_rule/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_fieldmapping_rule/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", exFieldmappingRuleService.getById(id));
        return "/page/ex_fieldmapping_rule/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", exFieldmappingRuleService.getById(id));
        return "/page/ex_fieldmapping_rule/update";
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
    public Response pageData(HttpServletRequest request, Page<ExFieldmappingRule> page) {
        QueryWrapper<ExFieldmappingRule> wrapper = new QueryWrapper<>();

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

        IPage<ExFieldmappingRule> result = exFieldmappingRuleService.page(page, wrapper);
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
    public Response save(ExFieldmappingRule exFieldmappingRule) {
        if (exFieldmappingRule == null) {
            return Response.error();
        }
        return Response.success(exFieldmappingRuleService.save(exFieldmappingRule));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExFieldmappingRule exFieldmappingRule) {
        if (exFieldmappingRule == null) {
            return Response.error();
        }
        return Response.success(exFieldmappingRuleService.updateById(exFieldmappingRule));
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
        if (id == null) {
            return Response.error();
        }
        return Response.success(exFieldmappingRuleService.removeById(id));
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
            exFieldmappingRuleService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }
}
