package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.service.ExIdDictService;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExIdDict;
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
@RequestMapping("/exIdDict")
public class ExIdDictController extends BaseController {
    @Autowired
    private ExIdDictService exIdDictService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_id_dict/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_id_dict/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        model.addAttribute("param", exIdDictService.getById(id));
        return "/page/ex_id_dict/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        model.addAttribute("param", exIdDictService.getById(id));
        return "/page/ex_id_dict/update";
    }

    /**
     * 分页数据
     *
     * @param request
     * @param page    [current, size]
     * @return Response
     */
    @GetMapping("/pageData")
    @ResponseBody
    public Response pageData(HttpServletRequest request, Page<ExIdDict> page) {
        QueryWrapper<ExIdDict> wrapper = new QueryWrapper<>();

        String seqNo = request.getParameter("seqNo");
        wrapper.eq(StringUtil.isNotBlank(seqNo), "seq_no", seqNo);

        String key = request.getParameter("key");
        wrapper.eq(StringUtil.isNotBlank(key), "seq_no", key);

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

        wrapper.orderByDesc("seq_no");

        IPage<ExIdDict> result = exIdDictService.page(page, wrapper);
        return Response.success(result);
    }

    /**
     * 保存数据
     *
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExIdDict exIdDict) {
        return Response.success(exIdDictService.save(exIdDict));
    }

    /**
     * 更新数据
     *
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExIdDict exIdDict) {
        return Response.success(exIdDictService.updateById(exIdDict));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return Response
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer id) {
        return Response.success(exIdDictService.removeById(id));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return Response
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] id) {
        if (id != null) {
            exIdDictService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }
}
