package com.sdy.dataexchange.biz.service;

import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.model.ExOrganizationDict;
import com.sdy.mvc.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
public interface ExOrganizationDictService extends BaseService<ExOrganizationDict> {
    /**
     * 拿到所有局信息
     * @param id
     * @return  List<Map<String, Object>>
     */
    List<Map<String, Object>> getAllOffice(Integer id);



    /**
     * 获取所有省名
     * @return
     */
    Response<List> getAllOrganization();
}
