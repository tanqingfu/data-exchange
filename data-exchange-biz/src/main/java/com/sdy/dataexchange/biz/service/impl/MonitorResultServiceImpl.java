package com.sdy.dataexchange.biz.service.impl;


import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.model.DTO.TaskRowRecord;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.redis.CacheNames;
import com.sdy.dataexchange.biz.service.*;
import com.sdy.dataexchange.core.DataJob;
import com.sdy.dataexchange.core.util.CacheUtil;
import com.sdy.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * @author 王越洋
 * @date 2019年6月26日-下午5:00:26
 * @info
 */

@Service
@Slf4j
public class MonitorResultServiceImpl implements MonitorResultService {
    @Autowired
    private ExTableMappingService exTableMappingService;
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    private ExTableDictService exTableDictService;
    @Autowired
    private ExDbMappingService exDbMappingService;
    @Autowired
    private ExDbDictService exDbDictService;
    @Autowired
    private ExFieldMappingService exFieldMappingService;
    @Autowired
    private ExSyncmonUserinfoService exSyncmonUserinfoService;
    @Autowired
    private ExMonitorMysqlService exMonitorMysqlService;
    @Autowired
    private ExFieldDictService exFieldDictService;
    @Autowired
    private RedisService redisService;
    private static final int MAX_MONITOR_SIZE = 50000000;
    private static final int MIN_MINOTOR_RECORD_COUNT = MAX_MONITOR_SIZE / 16777216 + 1;

    @Override
    public ExJobTask queryTask(Integer taskId) {
        return exJobTaskService.getById(taskId);
    }

    @Override
    public ExDbDict queryExDbDictSrc(Integer taskId) {
        ExJobTask task = exJobTaskService.getById(taskId);
        ExDbMapping dbmap = exDbMappingService.getById(task.getDbmapId());
        return exDbDictService.getById(dbmap.getSourceDbid());
    }

    @Override
    public ExDbDict queryExDbDictDest(Integer taskId) {
        ExJobTask task = exJobTaskService.getById(taskId);
        ExDbMapping dbmap = exDbMappingService.getById(task.getDbmapId());
        return exDbDictService.getById(dbmap.getDestDbid());
    }

    @Override
    public ExTableDict queryExTableDictSrc(Integer taskId) {
        ExJobTask task = exJobTaskService.getById(taskId);
        ExTableMapping exmap = exTableMappingService.getById(task.getTbmapId());
        return exTableDictService.getById(exmap.getSourceSyncid());
    }

    @Override
    public ExTableDict queryExTableDictDest(Integer taskId) {
        ExJobTask task = exJobTaskService.getById(taskId);
        ExTableMapping exmap = exTableMappingService.getById(task.getTbmapId());
        return exTableDictService.getById(exmap.getDestSyncid());
    }

    @Override
    public ExTableDict queryExTableDict(Integer dbId, String tableName) {
        return exTableDictService.lambdaQuery().eq(ExTableDict::getDbId, dbId).eq(ExTableDict::getDbTable, tableName).one();
    }

    @Override
    public List<ExFieldMapping> queryExFieldMapping(Integer sourceTabId, Integer destTableId) {
        List<ExFieldMapping> l = exFieldMappingService.lambdaQuery()
//                .in(ExFieldMapping::getSourceSyncname, columnsName)
                .eq(ExFieldMapping::getSourceSyncid, sourceTabId)
                .eq(ExFieldMapping::getValidFlag, "1")
                .eq(ExFieldMapping::getDestSyncid, destTableId)
                .list();
        return l;
    }

    @Override
    public ExTableMapping queryTableMapping(Integer taskId) {
        ExJobTask task = exJobTaskService.getById(taskId);
        return exTableMappingService.getById(task.getTbmapId());
    }

    @Override
    public List<ExMonitorMysql> pageExMonitorMysql(Integer taskId, Integer tableId, Integer row, TaskRowRecord taskRowRecord, AtomicBoolean gcFlag) {
        Integer blobCnt = exFieldDictService.lambdaQuery().like(ExFieldDict::getSyncType, "blob").eq(ExFieldDict::getSyncId, tableId).count();
        Integer count = blobCnt > 0 ? this.getAdaptiveMonitorCount(taskId, tableId, Long.valueOf(taskRowRecord.getDealtime()), row, gcFlag) : row;
        return exMonitorMysqlService.lambdaQuery()
                .eq(ExMonitorMysql::getTableId, tableId)
                .gt(ExMonitorMysql::getId, taskRowRecord.getDealtime())
                .orderByAsc(ExMonitorMysql::getId)
                .last(" limit " + count)
                .list();
    }

    /**
     * 计算需要取出的监控数据条数，防止数据太大
     */
    private Integer getAdaptiveMonitorCount(Integer taskId, Integer tableId, Long startId, Integer count, AtomicBoolean gcFlag) {
        Integer currentCount = redisService.get(RedisConstants.REDIS_MONITOR_MYSQL_COUNT + taskId, Integer.class);
        if (currentCount != null && currentCount == MIN_MINOTOR_RECORD_COUNT) {
            gcFlag.set(true);
            return currentCount;
        }
        currentCount = count;
        int tick = 20;
        while (tick-- > 0 && currentCount > MIN_MINOTOR_RECORD_COUNT) {
            int size = exMonitorMysqlService.countDataSize(tableId, startId, currentCount);
            if (size > MAX_MONITOR_SIZE) {
                currentCount = currentCount >> 2;
                gcFlag.set(true);
            } else {
                break;
            }
        }
        currentCount = Math.max(currentCount, MIN_MINOTOR_RECORD_COUNT);
        if (currentCount == MIN_MINOTOR_RECORD_COUNT) {
            redisService.set(RedisConstants.REDIS_MONITOR_MYSQL_COUNT + taskId, currentCount, 180);
        }
        return currentCount;
    }

    @Override
    public List<ExMonitorMysql> pageFullExMonitorMysql(Integer taskId, Integer tableId, Integer row, TaskRowRecord taskRowRecord) {
        return new ArrayList<>();
    }

    @Override
    public DataAdapter getSyncDataAdapterByTaskId(Integer taskId) {
        ExJobTask jobTask = exJobTaskService.getById(taskId);
        ExDbMapping dbmapping = exDbMappingService.getById(jobTask.getDbmapId());
        ExDbDict db = exDbDictService.getById(dbmapping.getSourceDbid());
        ExSyncmonUserinfo syncInfo = exSyncmonUserinfoService.getById(db.getSyncdbId());
        DbType dbType = DbType.getDbType(syncInfo.getSyncdbType());
        return new DataAdapter(
                new RawDataSource(
                        dbType,
                        syncInfo.getSyncdbIp(),
                        syncInfo.getSyncdbPort(),
                        syncInfo.getSyncdbName(),
                        syncInfo.getSyncdbUser(),
                        syncInfo.getSyncdbPasswd(),
                        null)
        );

    }

    @Override
    public DataAdapter getSrcDbDataAdapterByTaskId(Integer taskId) {
        ExJobTask jobTask = exJobTaskService.getById(taskId);
        ExDbMapping dbmapping = exDbMappingService.getById(jobTask.getDbmapId());
        ExDbDict db = exDbDictService.getById(dbmapping.getSourceDbid());
        DbType dbType = DbType.getDbType(db.getDbType());
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("useCursorFetch", "true");
        paramMap.put("defaultFetchSize", "1000");
        return new DataAdapter(
                new RawDataSource(
                        dbType,
                        db.getDbIp(),
                        db.getDbPort(),
                        db.getDbName(),
                        db.getDbUser(),
                        db.getDbPasswd(),
                        paramMap)
        );

    }

    @Override
    public Map<String, String> getFieldDict(Integer srcTbId) {
        List<ExFieldDict> fieldDict = exFieldDictService.lambdaQuery().eq(ExFieldDict::getSyncId, srcTbId).list();
        Map<String, String> map = new HashMap<>(fieldDict.size() + 1);
        fieldDict.forEach(exFieldDict -> map.put(exFieldDict.getSyncField(), exFieldDict.getSyncType()));
        return map;
    }
}
