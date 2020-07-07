package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.BO.UserInfoBO;
import com.sdy.dataexchange.biz.model.ExSyncmonUserinfo;
import com.sdy.dataexchange.biz.model.UserInfo;
import com.sdy.mvc.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyy
 * @since 2019-08-27
 */
public interface ExSyncmonUserinfoService extends BaseService<ExSyncmonUserinfo> {
    /**
     * 通过同步账号信息
     * @param page
     * @return  List<UserInfo>
     */
    List<UserInfo> getUserInfos(Page page);
    /**
     * 通过id获取同步账号详情
     * @param id
     * @return  UserInfo
     */
    UserInfo getUserInfoDetail(Integer id);
    /**
     * 删除同步账号
     * @param syncdbId
     * @return  boolean
     */
    boolean removeOne(Integer syncdbId);
    /**
     * 获取同步账号信息
     * @param key
     * @return  List<UserInfo>
     */
    List<UserInfo> getInfo(String key,Page page);

    /**
     * 获取同步账号总数
     * @return
     */
    Integer getTotle();

    /**
     * 更新同步账号
     * @param exSyncmonUserinfo
     * @return
     */
    Response updateUserInfo(ExSyncmonUserinfo exSyncmonUserinfo);

    /**
     * 将同步账号信息加入到中间库中
     * @param userInfoBO
     */
    Response addUserInfo(UserInfoBO userInfoBO);

    /**
     * 获取所有用户信息
     * @return  List<Map>
     */
    List<Map<String,Object>> getAllUserInfo();
    /**
     * 改变同步账号状态
     * @param i
     * @param syncdbId
     */
    void changeCode(int i, Integer syncdbId);
}
