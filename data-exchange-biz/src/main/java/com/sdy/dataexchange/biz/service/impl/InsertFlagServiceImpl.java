package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.mapper.InsertFlagMapper;
import com.sdy.dataexchange.biz.service.InsertFlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 往库，表，字段映射中插入有效标志
 * </p>
 *
 * @author 高连明
 * @since 2019-07-26
 */
@Service
public class InsertFlagServiceImpl implements InsertFlagService {
    @Autowired
    InsertFlagMapper insertFlagMapper;

    @Override
    public Integer getGatherId(String leftDbId, String rightDbId) {
        return insertFlagMapper.getGatherId(leftDbId, rightDbId);
    }


    @Override
    public Integer getSyncId(String dbId, String tableName) {
        return insertFlagMapper.getSyncId(dbId, tableName);
    }

    @Override
    public Integer getId(Integer leftSyncId, Integer rightSyncId) {
        return insertFlagMapper.getId(leftSyncId, rightSyncId);
    }

    @Override
    public List getSyncField(Integer syncId) {
        return insertFlagMapper.getSyncField(syncId);
    }

}
