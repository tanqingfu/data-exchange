package com.sdy.dataexchange.biz.mapper;

import com.sdy.dataexchange.biz.model.ExOrganizationDict;
import com.sdy.mvc.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
public interface ExOrganizationDictMapper extends BaseMapper<ExOrganizationDict> {

    /**
     * 拿到所有局信息
     * @param id
     * @return  List<Map<String, Object>>
     */
    List<Map<String, Object>> getAllOffice(Integer id);

    /**
     * 拿到所有省信息
     * @return  List<Map<String, Object>>
     */
    List<Map<String, Object>> getAllProvince();
    /**
     * 拿到所有市信息
     * @return  List<Map<String, Object>>
     */
    List<Map<String, Object>> getAllCity();
    /**
     * 拿到所有区信息
     * @return  List<Map<String, Object>>
     */
    List<Map<String, Object>> getAllArea();
}
