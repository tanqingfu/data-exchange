package com.sdy.dataexchange.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.model.BO.UserInfoBO;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExSyncmonUserinfo;
import com.sdy.dataexchange.biz.model.UserInfo;
import com.sdy.dataexchange.biz.service.ExDbDictService;
import com.sdy.dataexchange.biz.service.ExSyncmonUserinfoService;
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
 * @since 2019-08-26
 */
@Slf4j
@Controller
@RequestMapping("/exSyncmonUserinfo")
public class ExSyncmonUserinfoController extends BaseController {
    @Autowired
    private ExSyncmonUserinfoService exSyncmonUserinfoService;
    @Autowired
    private ExDbDictService exDbDictService;

    /**
     * 页面
     */
    @GetMapping("/page")
    public String page() {
        return "/page/ex_syncmon_userinfo/page";
    }


    @GetMapping("/page/addUserInfo")
    public String addUserInfo() {
        return "/page/ex_syncmon_userinfo/new";
    }

    /**
     * 新建页面
     */
    @GetMapping("/page/newPage")
    public String newPage() {
        return "/page/ex_syncmon_userinfo/new";
    }

    /**
     * 详情页面
     */
    @GetMapping("/page/detailPage")
    public String detailPage(Model model, Integer syncdbId) {
        UserInfo userInfoDetail = exSyncmonUserinfoService.getUserInfoDetail(syncdbId);
        userInfoDetail.setSyncdbPasswd("");
        model.addAttribute("param", userInfoDetail);
        return "/page/ex_syncmon_userinfo/detail";
    }

    /**
     * 修改页面
     */
    @GetMapping("/page/updatePage")
    public String updatePage(Model model, Integer syncdbId) {
        model.addAttribute("param", exSyncmonUserinfoService.getUserInfoDetail(syncdbId));
        return "/page/ex_syncmon_userinfo/update";
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
    public Response pageData(HttpServletRequest request, Page<ExSyncmonUserinfo> page) {
        QueryWrapper<ExSyncmonUserinfo> wrapper = new QueryWrapper<>();

        String syncdbId = request.getParameter("syncdb_id");
        wrapper.eq(StringUtil.isNotBlank(syncdbId), "syncdb_id", syncdbId);
            //通过同步账号搜索
        String key = request.getParameter("account");
        if (StringUtil.isNotBlank(key)) {
            List<UserInfo> userInfo = exSyncmonUserinfoService.getInfo(key, page);
            if (!userInfo.isEmpty()) {
                return Response.success(new Page<UserInfo>(1, userInfo.size(), userInfo.size()).setRecords(userInfo));
            } else {
                return Response.success(new Page<UserInfo>(1, 1, 1).setRecords(new ArrayList<>()));
            }
        }
        wrapper.eq(StringUtil.isNotBlank(key), "syncdb_id", key);

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

        wrapper.orderByDesc("syncdb_id");
        //获取同步账号首页的分页数据
        Integer totle = exSyncmonUserinfoService.getTotle();
        List<UserInfo> userInfos = exSyncmonUserinfoService.getUserInfos(page);
        if (!userInfos.isEmpty()) {
            for (UserInfo info : userInfos
                    ) {
                String state = info.getValidFlag();
                if (state != null) {
                    if ("1".equals(state)) {
                        state = "启用中";
                        info.setValidFlag(state);
                    } else if ("0".equals(state)) {
                        state = "未启用";
                        info.setValidFlag(state);
                    }
                }
            }
            return Response.success(new Page<UserInfo>(page.getCurrent(), page.getSize(), totle).setRecords(userInfos));
        } else {
            return Response.success(new Page<UserInfo>(page.getCurrent(), page.getSize(), 0).setRecords(new ArrayList<>()));
        }

    }

    /**
     * 保存数据
     *
     * @param
     * @return
     */
    @PostMapping("/save")
    @ResponseBody
    public Response save(ExSyncmonUserinfo exSyncmonUserinfo) {
        return null;
    }

    /**
     * 更新数据
     *
     * @param
     * @return
     */
    @PostMapping("/update")
    @ResponseBody
    public Response update(ExSyncmonUserinfo exSyncmonUserinfo) {
        return exSyncmonUserinfoService.updateUserInfo(exSyncmonUserinfo);
    }



    /**
     * 删除数据
     *
     * @param syncdbId 主键
     * @return
     */
    @GetMapping("/delete")
    @ResponseBody
    public Response delete(Integer syncdbId) {
        if (syncdbId == null) {
            return Response.error();
        }
        List<ExDbDict> dbDicts=exDbDictService.list(new LambdaQueryWrapper<ExDbDict>().eq(ExDbDict::getSyncdbId,syncdbId));
        if (!dbDicts.isEmpty()){
            return Response.error("该同步账号下存在数据源，请先删除该同步账号下所有数据源");
        }
        return Response.success(exSyncmonUserinfoService.removeOne(syncdbId));
    }

    /**
     * 删除数据
     *
     * @param syncdbId 主键
     * @return
     */
    @GetMapping("/deleteBatch")
    @ResponseBody
    public Response deleteBatch(Integer[] syncdbId) {
        if (syncdbId==null){
            return Response.error();
        }
        for (Integer syncId:syncdbId) {
            List<ExDbDict> dbDicts=exDbDictService.list(new LambdaQueryWrapper<ExDbDict>().eq(ExDbDict::getSyncdbId,syncId));
            ExSyncmonUserinfo userinfo=exSyncmonUserinfoService.getById(syncId);
            if (!dbDicts.isEmpty()&&userinfo!=null){
                return Response.error("同步账号'"+userinfo.getSyncdbUser()+"(IP:"+userinfo.getSyncdbIp()+";PORT:"
                        +userinfo.getSyncdbPort() +")'下存在数据源，请先删除该同步账号所有数据源");
            }
        }
            exSyncmonUserinfoService.removeByIds(Arrays.asList(syncdbId));
        return Response.success();
    }

    /**
     * 新建同步账号
     *
     * @param userInfoBO
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/addUserInfo", method = RequestMethod.POST)
    public Response addUserInfo(UserInfoBO userInfoBO) {
        return exSyncmonUserinfoService.addUserInfo(userInfoBO);
    }

    /**
     * 获取所有同步账号信息
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getAllUserInfo", method = RequestMethod.GET)
    public Response getAllUserInfo() {
        List<Map<String, Object>> allUserInfos = new ArrayList<>();
        allUserInfos = exSyncmonUserinfoService.getAllUserInfo();
        if (!allUserInfos.isEmpty()) {
            for (Map<String,Object> userInfo : allUserInfos
                    ) {
                userInfo.put("syncdbUser", userInfo.get("syncdbUser") + "&nbsp;&nbsp(IP:" + userInfo.get("syncdbIp")
                        + ";&nbsp;&nbsp;PORT:" + userInfo.get("syncdbPort") + ")");
            }
        }
        return Response.success(allUserInfos);
    }

    /**
     * 开启同步账号
     *
     * @param syncdbId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    //启用同步账号
    public Response start(Integer syncdbId) {
        if (syncdbId != null) {
            exSyncmonUserinfoService.changeCode(1, syncdbId);
        }
        return Response.success();
    }

    /**
     * 禁用同步账号
     *
     * @param syncdbId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    //停用同步账号
    public Response stop(Integer syncdbId) {
        if (syncdbId != null) {
            exSyncmonUserinfoService.changeCode(0, syncdbId);
        }
        return Response.success();
    }
}
