package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.mapper.InsertInfoMapper;
import com.sdy.dataexchange.biz.service.InsertInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 将映射的库，表，字段信息插入应用库
 * </p>
 *
 * @author 高连明
 * @since 2019-07-26
 */
@Service
public class InsertInfoServiceImpl implements InsertInfoService {
    @Autowired
    InsertInfoMapper insertInfoMapper;

    @Override
    public void insertIntoExTableDict(Integer leftDbId, String leftTableName, String createTime) {
        insertInfoMapper.insertIntoExTableDict(leftDbId, leftTableName, createTime);
    }

    @Override
    public void insertIntoExTableMapping(Integer sourceSyncId, Integer destSyncId, String createTime) {
        insertInfoMapper.insertIntoExTableMapping(sourceSyncId, destSyncId, createTime);
    }


    @Override
    public Integer getSyncSeqno(Integer sourceSyncid, String field) {
        Integer syncSeqno = insertInfoMapper.getSyncSeqno(sourceSyncid, field);
        return syncSeqno;
    }

    @Override
    public Integer getId(Integer sourceSyncId, Integer destSyncId) {
        return insertInfoMapper.getId(sourceSyncId, destSyncId);
    }

    @Override
    public Integer getSyncIds(Integer dbId, String tableName) {
        return insertInfoMapper.getSyncIds(dbId, tableName);
    }


    @Override
    public List<String> getSyncField(Integer syncId) {
        return insertInfoMapper.getSyncField(syncId);
    }

}
