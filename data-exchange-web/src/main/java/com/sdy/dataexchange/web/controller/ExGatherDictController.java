package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.BO.GatherBO;
import com.sdy.dataexchange.biz.model.DbMappingResult;
import com.sdy.dataexchange.biz.model.ExGatherDict;
import com.sdy.dataexchange.biz.model.GatherDictResult;
import com.sdy.dataexchange.biz.service.ExGatherDictService;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
@RequestMapping("/exGatherDict")
public class ExGatherDictController extends BaseController {
    @Autowired
    private ExGatherDictService exGatherDictService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_gather_dict/page";
    }
    /**
     * 新建页面
     */
    @GetMapping("/addGatherDictPage")
    public String addGatherDict() {
        return "/page/ex_gather_dict/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer gatherId) {
        model.addAttribute("param", exGatherDictService.getOneInfo(gatherId));
        return "/page/ex_gather_dict/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer gatherId) {
        model.addAttribute("param", exGatherDictService.getOneInfo(gatherId));
        return "/page/ex_gather_dict/update";
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
    public Response pageData(HttpServletRequest request, Page<ExGatherDict> page) {
        QueryWrapper<ExGatherDict> wrapper = new QueryWrapper<>();
        // 通过交换节点描述搜索
        String gatherDesc = request.getParameter("gatherDesc");
        if (com.sdy.common.utils.StringUtil.isNotBlank(gatherDesc)) {
            List<GatherDictResult> exGatherDict = exGatherDictService.getInfo(gatherDesc);
            if (exGatherDict != null) {
                return Response.success(new Page<GatherDictResult>(1, exGatherDict.size(), exGatherDict.size())
                        .setRecords(exGatherDict));
            } else {
                return Response.success(new Page<DbMappingResult>(1, 1, 1).setRecords(new ArrayList<>()));
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

        wrapper.orderByDesc("gather_desc");
        //获取交换节点首页数据
        Integer totle = exGatherDictService.getTotle();
        List<GatherDictResult> infos = exGatherDictService.getGatherInfo(page);
        return Response.success(new Page<GatherDictResult>(page.getCurrent(), page.getSize(), totle).setRecords(infos));
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(GatherDictResult gatherDictResult) {
        if (gatherDictResult == null) {
            return Response.error();
        }
        exGatherDictService.saveGather(gatherDictResult);
        return Response.success(exGatherDictService.saveDbMapping(gatherDictResult));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExGatherDict exGatherDict) {
        if (exGatherDict == null) {
            return Response.error();
        }
        Integer gatherId = null;
        ExGatherDict oneExGatherDict = exGatherDictService.getOne(new LambdaQueryWrapper<ExGatherDict>()
                .eq(ExGatherDict::getGatherDesc, exGatherDict.getGatherDesc()));
        if (oneExGatherDict != null) {
            gatherId = oneExGatherDict.getGatherId();
        }
        //如果该交换节点名称已存在则不能编辑，否则编辑成功
        if (gatherId != null) {
            return Response.error("该交换节点名称已存在");
        } else {
            return Response.success(exGatherDictService.updateInfo(exGatherDict));
        }
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer gatherId) {
        if (gatherId == null) {
            return Response.error();
        }
        ExGatherDict exGatherDict = exGatherDictService.getOne(new QueryWrapper<ExGatherDict>()
                .eq("gather_id", gatherId));
        if (exGatherDict == null) {
            return Response.error();
        }
        return Response.success(exGatherDictService.removeById(exGatherDict));
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] gatherId) {
        if (gatherId != null) {
            exGatherDictService.removeByIds(Arrays.asList(gatherId));
        }
        return Response.success();
    }

    /**
     * 新建交换节点
     * @param gatherBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addGather", method = RequestMethod.POST)
    public Response addGather(GatherBO gatherBO) {
        return exGatherDictService.addGather(gatherBO);

    }


    /**
     * 获取所有交换节点信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getAllGather", method = RequestMethod.GET)
    public List<GatherDictResult> showAllGather() {
        return exGatherDictService.getAllGatherPage();
    }


    /**
     * 获取所有交换节点描述
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getAllGatherDesc", method = RequestMethod.GET)
    public List<Map> showAllGatherDesc() {
        return exGatherDictService.getAllGather();
    }
}
