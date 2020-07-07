package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExFieldMapping;
import com.sdy.dataexchange.biz.model.FieldMappingResult;
import com.sdy.dataexchange.biz.model.TableMappingResult;
import com.sdy.dataexchange.biz.service.ExFieldMappingService;
import com.sdy.dataexchange.biz.service.ExTableMappingService;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
 * @since 2019-07-30
 */
@Slf4j
@Controller
@RequestMapping("/exFieldMapping")
public class ExFieldMappingController extends BaseController {
    @Autowired
    private ExFieldMappingService exFieldMappingService;
    @Autowired
    private ExTableMappingService exTableMappingService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_field_mapping/page";
    }

    /**
     * 新建页面
     */
    @GetMapping("/newPage")
    public String newPage() {
        return "/page/ex_field_mapping/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer id) {
        setFieldMappingResult(model, id);
        return "/page/ex_field_mapping/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/updatePage")
    public String updatePage(Model model, Integer id) {
        setFieldMappingResult(model, id);
        return "/page/ex_field_mapping/update";
    }

    private void setFieldMappingResult(Model model, Integer id) {
        FieldMappingResult fieldMappingResult = exFieldMappingService.getInfo(id);
        if("0".equals(fieldMappingResult.getValidFlag())){
            fieldMappingResult.setValidFlag("映射失败");
        }else if("1".equals(fieldMappingResult.getValidFlag())){
            fieldMappingResult.setValidFlag("映射正常");
        }
        model.addAttribute("param", fieldMappingResult);
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
    public Response pageData(HttpServletRequest request, Page<ExFieldMapping> page) {
        QueryWrapper<ExFieldMapping> wrapper = new QueryWrapper<>();
        String sourceTable = request.getParameter("sourceTable");
        if (sourceTable != null) {
            List<FieldMappingResult> list = new ArrayList<>();
            Integer sourceTableId = exTableMappingService.getTableId(sourceTable);
            if (sourceTableId != null) {
                List<ExFieldMapping> exFieldMapping = exFieldMappingService.getExFieldMapping(sourceTableId);
                if (!exFieldMapping.isEmpty()){
                    for (ExFieldMapping s : exFieldMapping) {
                        String sourceTable1 = exFieldMappingService.getFieldName(s.getSourceSyncid());
                        String destTable = exFieldMappingService.getFieldName(s.getDestSyncid());
                        FieldMappingResult fieldMappingResult = new FieldMappingResult();
                        fieldMappingResult.setId(s.getId());
                        fieldMappingResult.setSourceTable(sourceTable1);
                        fieldMappingResult.setSourceSyncName(s.getSourceSyncname());
                        fieldMappingResult.setSourceFunc(s.getSourceFunc());
                        fieldMappingResult.setDestTable(destTable);
                        fieldMappingResult.setDestSyncName(s.getDestSyncname());
                        fieldMappingResult.setDestFunc(s.getDestFunc());
                        fieldMappingResult.setValidFlag(s.getValidFlag());
                        list.add(fieldMappingResult);
                    }
            }
                return Response.success(new Page<FieldMappingResult>(page.getCurrent(), page.getSize(), list.size())
                        .setRecords(list));
            } else {
                return Response.success(new Page<TableMappingResult>(page.getCurrent(), page.getSize(), list.size())
                        .setRecords(new ArrayList<>()));
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

        wrapper.orderByDesc("source_syncid");

        IPage<ExFieldMapping> result = exFieldMappingService.page(page, wrapper);

        Integer startIndex = (int) ((page.getCurrent() - 1) * page.getSize());
        Integer pageSize = (int) page.getSize();
        List<FieldMappingResult> infos = exFieldMappingService.getMapping(startIndex, pageSize);
        for (FieldMappingResult fieldMappingResult: infos
             ) {
            if("0".equals(fieldMappingResult.getValidFlag())){
                fieldMappingResult.setValidFlag("映射失败");
            }else if("1".equals(fieldMappingResult.getValidFlag())){
                fieldMappingResult.setValidFlag("映射正常");
            }
        }
        return Response.success(new Page<FieldMappingResult>(page.getCurrent(), page.getSize(), result.getTotal()).setRecords(infos));
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExFieldMapping exFieldMapping) {
        if (exFieldMapping == null) {
            return Response.error();
        }
        return Response.success(exFieldMappingService.save(exFieldMapping));
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExFieldMapping exFieldMapping) {
        if (exFieldMapping == null) {
            return Response.error();
        }
        return Response.success(exFieldMappingService.updateById(exFieldMapping));
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
        return Response.success(exFieldMappingService.removeById(id));
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
            exFieldMappingService.removeByIds(Arrays.asList(id));
        }
        return Response.success();
    }
}
