package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.mapper.ShowInfoMapper;
import com.sdy.dataexchange.biz.service.ShowInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * <p>
 * 映射展示
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
@Service
public class ShowInfoServiceImpl implements ShowInfoService {
    @Autowired
    ShowInfoMapper showInfoMapper;

    @Override
    public Map getSyncId(Integer dbId, String dbTable) {
        return showInfoMapper.getSyncId(dbId, dbTable);
    }

    @Override
    public Integer getSyncSeqno(Integer syncId, String syncField) {
        return showInfoMapper.getSyncSeqno(syncId, syncField);
    }

    @Override
    public List<Map<String, Object>> getCreateTime() {
        return showInfoMapper.getCreateTime();
    }

    @Override
    public List<Map<String, Object>> getSyncIds(Integer destDbid) {
        return showInfoMapper.getSyncIds(destDbid);
    }
}
