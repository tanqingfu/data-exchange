package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.BO.DatabaseBO;
import com.sdy.dataexchange.biz.model.DbDetails;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExDbMapping;
import com.sdy.dataexchange.biz.service.ExDbDictService;
import com.sdy.dataexchange.biz.service.ExDbMappingService;
import com.sdy.dataexchange.biz.service.ExOrganizationDictService;
import com.sdy.mvc.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/exDbDict")
public class ExDbDictController extends BaseController {
    @Autowired
    private ExDbDictService exDbDictService;
    @Autowired
    ExOrganizationDictService organizationDictService;
    @Autowired
    ExDbMappingService exDbMappingService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_db_dict/page";
    }

    /**
     * 详情页面
     */
    @GetMapping("/detailPage")
    public String detailPage(Model model, Integer dbId) {
        DbDetails dbDetails = exDbDictService.getOneInfo(dbId);
        dbDetails.setDbPasswd("");
        dbDetails.setSyncdbUser(dbDetails.getSyncdbUser() + "&nbsp;&nbsp(IP:" + dbDetails.getSyncdbIp() + ";&nbsp;&nbsp;PORT:"
                + dbDetails.getSyncdbPort() + ")");
        model.addAttribute("param", dbDetails);
        return "/page/ex_db_dict/detail";
    }


    /**
     * 新建页面
     * @return
     */
    @GetMapping("/page/addDbDict")
    public String addDbDict() {
        return "/page/ex_db_dict/new";
    }

    /**
     * 修改页面
     */
    @GetMapping("/page/updatePage")
    public String updatePage(Model model, Integer dbId) {
        DbDetails oneInfo = exDbDictService.getOneInfo(dbId);
        oneInfo.setSyncdbUser(oneInfo.getSyncdbUser() + "&nbsp;&nbsp(IP:" + oneInfo.getSyncdbIp() + ";&nbsp;&nbsp;PORT:"
                + oneInfo.getSyncdbPort() + ")");
        model.addAttribute("param", oneInfo);
        return "/page/ex_db_dict/update";
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
    public Response pageData(HttpServletRequest request, Page<ExDbDict> page) {
        QueryWrapper<ExDbDict> wrapper = new QueryWrapper<>();

        String dbName = request.getParameter("dbName");
        if(StringUtils.isNotBlank(dbName)){
            Integer totle= exDbDictService.getTotleByName(dbName,page);
            List<ExDbDict> exDbDict=exDbDictService.list(new LambdaQueryWrapper<ExDbDict>()
            .eq(ExDbDict::getDbName,dbName));
            if (!exDbDict.isEmpty()){
                for (ExDbDict db:exDbDict) {
                    db.setDbPasswd(null);
                }
                return Response.success(new Page<ExDbDict>(page.getCurrent(), page.getSize(), totle).setRecords(exDbDict));
            }else {
                return Response.success(new Page<ExDbDict>(page.getCurrent(), page.getSize(), totle).setRecords(new ArrayList<>()));
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

        wrapper.orderByDesc("db_name");

        Integer totle = exDbDictService.getTotle();
        Page<DbDetails> page1 = new Page(page.getCurrent(), page.getSize());
        List<DbDetails> infos = exDbDictService.getDb(page1);
        if (!infos.isEmpty()) {
            for (DbDetails info : infos) {
                if (info.getSyncdbIp() == null || info.getSyncdbPort() == null) {
                    info.setSyncdbUser("");
                } else {
                    info.setSyncdbUser(info.getDbUser() + "&nbsp;&nbsp;(IP:" + info.getSyncdbIp() + ";PORT:" + info.getSyncdbPort());
                }
            }
        }
        return Response.success(new Page<DbDetails>(page.getCurrent(), page.getSize(), totle).setRecords(infos));
    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExDbDict exDbDict) {
        if (exDbDict == null) {
            return Response.error();
        }
        exDbDictService.save(exDbDict);
        return Response.success("新增成功");
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(Integer dbId, String dbDesc, String dbIp, Integer dbPort, String dbUser, String dbPasswd
            , String dbName, String dbType, Integer office, String userInfo) {
        return exDbDictService.updateDb(dbId, dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, office, userInfo);

    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer dbId) {
        if (dbId == null) {
            return Response.error();
        }
        ExDbDict exDbDict = exDbDictService.getOne(new QueryWrapper<ExDbDict>().eq("db_id", dbId));
        if (exDbDict == null) {
            return Response.error();
        }
        List<ExDbMapping> sourceDbMappings=exDbMappingService.list(new LambdaQueryWrapper<ExDbMapping>()
                .eq(ExDbMapping::getSourceDbid,dbId));
        List<ExDbMapping> destDbMappings=exDbMappingService.list(new LambdaQueryWrapper<ExDbMapping>()
                .eq(ExDbMapping::getDestDbid,dbId));
        if (sourceDbMappings.isEmpty()&&destDbMappings.isEmpty()){
            return Response.success(exDbDictService.removeById(exDbDict));
        }else {
            return Response.error("该库源正在使用中，禁止删除");
        }
    }

    /**
     * 删除数据
     *
     * @param
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] dbIds) {
        if (dbIds == null) {
            return Response.error();
        }
        for (Integer dbId : dbIds) {
            ExDbDict exDbDict = exDbDictService.getOne(new QueryWrapper<ExDbDict>().eq("db_id", dbId));
            if (exDbDict == null) {
                return Response.error();
            }
            List<ExDbMapping> sourceDbMappings=exDbMappingService.list(new LambdaQueryWrapper<ExDbMapping>()
                    .eq(ExDbMapping::getSourceDbid,dbId));
            List<ExDbMapping> destDbMappings=exDbMappingService.list(new LambdaQueryWrapper<ExDbMapping>()
                    .eq(ExDbMapping::getDestDbid,dbId));
            if (!sourceDbMappings.isEmpty()||!destDbMappings.isEmpty()){
                return Response.error("库源'"+exDbDict.getDbName()+"(IP:"+exDbDict.getDbIp()+";PORT:"+exDbDict.getDbPort()+";用户名:"+exDbDict.getDbUser()+")'正在使用中，禁止删除");
            }
        }
        exDbDictService.removeByIds(Arrays.asList(dbIds));
        return Response.success();
    }

    /**
     * 新建数据源
     *
     * @param databaseBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addDb", method = RequestMethod.POST)
    public Response addDb(DatabaseBO databaseBO) {
        return exDbDictService.addDatabase(databaseBO);
    }

    /**
     * 获取采集点下所有数据源
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getAllDatabase", method = RequestMethod.GET)
    public List getAllDatabase() {
        return exDbDictService.getAllDatabases();
    }

    /**
     * 获取交换节点下所有库信息
     * @param gatherDesc
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getDbByDesc", method = RequestMethod.GET)
    public Response getDbByDesc(String gatherDesc) {
        return exDbDictService.getDbByDesc(gatherDesc);
    }

    /**
     * 获取交换节点下的源库和目标库
     * @param gatherId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSourceDbsAndDestDbs", method = RequestMethod.GET)
    public Map getSourceDbsAndDestDbs(Integer gatherId) {
        return exDbDictService.getSourceDbsAndDestDbs(gatherId);

    }

    /**
     * 获取库下面的所有用户名
     * @param dbDesc
     * @return
     */
//    @ResponseBody
//    @RequestMapping(value = "/getUserByDbDesc", method = RequestMethod.GET)
//    public List getUserByDbDesc(String dbDesc) {
//        return exDbDictService.getUserByDbDesc(dbDesc);
//
//    }
}
