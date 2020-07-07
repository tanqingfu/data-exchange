package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.mapper.ShowDataMapper;
import com.sdy.dataexchange.biz.service.ShowDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * <p>
 * 获取数据交换量
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
@Service
public class ShowDataServiceImpl implements ShowDataService {
    @Autowired
    ShowDataMapper showDataMapper;
    @Override
    public List<Map> getData() {
        return showDataMapper.getData();
    }

    @Override
    public List<Map> getDb() {
        return showDataMapper.getDb();
    }

}
