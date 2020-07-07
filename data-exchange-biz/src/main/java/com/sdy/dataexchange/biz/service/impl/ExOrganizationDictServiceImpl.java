package com.sdy.dataexchange.biz.service.impl;

import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.mapper.ExOrganizationDictMapper;
import com.sdy.dataexchange.biz.model.ExOrganizationDict;
import com.sdy.dataexchange.biz.service.ExOrganizationDictService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
@Slf4j
@Service
public class ExOrganizationDictServiceImpl extends BaseServiceImpl<ExOrganizationDict> implements ExOrganizationDictService {
    @Autowired
    private ExOrganizationDictMapper exOrganizationDictMapper;

    @Override
    public List<Map<String, Object>> getAllOffice(Integer id) {

        List<Map<String, Object>> allOffice = exOrganizationDictMapper.getAllOffice(id);
        allOffice.forEach(office -> {
            office.put("text", office.get("org_name"));
            office.put("value", office.get("org_id"));
        });
        return allOffice;
    }
    @Override
    public Response<List> getAllOrganization() {
        List<Object> infos = new ArrayList();
        List<Map<String, Object>> allProvince = exOrganizationDictMapper.getAllProvince();
        List<Map<String, Object>> allCity = exOrganizationDictMapper.getAllCity();
        List<Map<String, Object>> allArea = exOrganizationDictMapper.getAllArea();
        if (allProvince.size() != 0 && allCity.size() != 0 && allArea.size() != 0) {
            //将省市区的信息修改成前端需要的id,text,value
            for (Map<String,Object> province : allProvince) {
                Integer provinceId = (Integer) province.get("org_id");
                province.put("id", province.get("org_id"));
                province.put("text", province.get("org_name"));
                province.put("value", province.get("org_name"));
                List<Map> citys = new ArrayList();
                for (Map<String,Object> city : allCity) {
                    Integer cityId = (Integer) city.get("org_id");
                    if (city.get("org_parentid").equals(provinceId)) {
                        city.put("id", city.get("org_id"));
                        city.put("text", city.get("org_name"));
                        city.put("value", String.valueOf(province.get("org_name")) + String.valueOf(city.get("org_name")));
                        citys.add(city);
                        List<Map> areas = new ArrayList();
                        for (Map<String,Object> area : allArea) {
                            if (area.get("org_parentid").equals(cityId)) {
                                Map<String,Object> tempArea = new HashMap(16);
                                tempArea.put("id", area.get("org_id"));
                                tempArea.put("text", area.get("org_name"));
                                tempArea.put("value", String.valueOf(province.get("org_name")) + String.valueOf(city.get("org_name")) + String.valueOf(area.get("org_name")));
                                areas.add(tempArea);
                            }
                        }
                        city.put("children", areas);
                        province.put("children", citys);
                    }
                }
                infos.add(province);
            }
        }
        return Response.success(infos);

    }

}
