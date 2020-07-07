package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.service.ExMonitorMysqlService;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.common.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sdy.mvc.controller.BaseController;
import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wyy
 * @since 2019-10-10
 */
@Slf4j
@RestController
@RequestMapping("/exMonitorMysql")
public class ExMonitorMysqlController extends BaseController {
    @Autowired
    private ExMonitorMysqlService exMonitorMysqlService;

    /**
     * 查询单条数据
     * @param id 主键
     * @return
     * @throws Exception
     */
    @GetMapping("/get")
    public Response get(Long id) {
        return Response.success(exMonitorMysqlService.getById(id));
    }

    /**
     * 分页数据
     * @param request
     * @param page [current, size]
     * @return
     */
    @GetMapping("/pageData")
    public Response pageData(HttpServletRequest request, Page<ExMonitorMysql> page) {
        QueryWrapper<ExMonitorMysql> wrapper = new QueryWrapper<>();

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

        String createTimeRange0 = request.getParameter("createTimeRange[0]");
        String createTimeRange1 = request.getParameter("createTimeRange[1]");
        if (StringUtil.isNotBlank(createTimeRange0) && StringUtil.isNotBlank(createTimeRange1)) {
            wrapper.between("create_time",
                DateUtil.getDate(createTimeRange0, DateUtil.DATETIME_FORMAT),
                DateUtil.getDate(createTimeRange1, DateUtil.DATETIME_FORMAT));
        }

        wrapper.orderByDesc("id");

        IPage<ExMonitorMysql> result = exMonitorMysqlService.page(page, wrapper);
        return Response.success(result);
    }

    /**
     * 保存数据
     * @param
     * @return
     */
    @PostMapping("/save")
    public Response save(@RequestBody ExMonitorMysql exMonitorMysql) {
        return Response.success(exMonitorMysqlService.save(exMonitorMysql));
    }

    /**
     * 更新数据
     * @param
     * @return
     */
    @PostMapping("/update")
    public Response update(@RequestBody ExMonitorMysql exMonitorMysql) {
        return Response.success(exMonitorMysqlService.updateById(exMonitorMysql));
    }

    /**
     * 删除数据
     * @param id 主键
     * @return
     */
    @GetMapping("/delete")
    public Response delete(Long id) {
        return Response.success(exMonitorMysqlService.removeById(id));
    }
}
