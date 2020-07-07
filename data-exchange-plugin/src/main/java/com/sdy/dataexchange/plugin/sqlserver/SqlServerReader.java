package com.sdy.dataexchange.plugin.sqlserver;

import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.model.DTO.TaskRowRecord;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.Reader;
import com.sdy.dataexchange.plugin.common.converts.SqlServerTypeConvert;
import com.sdy.dataexchange.plugin.config.PluginConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SqlServerReader extends Reader {
    public static class Task extends Reader.AbstractCommonDbJobFragment {

        public Task(ApplicationContext applicationContext, Integer taskId, Boolean fullSync) {
            super(applicationContext, taskId, fullSync);
            this.typeConvert = new SqlServerTypeConvert();
        }

        @Override
        public boolean run() throws Exception {
            String lastKey = RedisConstants.REDIS_LAST_SYNC_KEY + taskId;
            if (fullSync) {
                ExDbDict dbDict = monitorResultService.queryExDbDictSrc(taskId);
                ExTableDict tbDict = monitorResultService.queryExTableDictSrc(taskId);
                DataAdapter dataAdapter = monitorResultService.getSrcDbDataAdapterByTaskId(taskId);
                DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.SQL_SERVER);
                fullSyncReadOriginCount = dataAdapter.count(String.format("select count(*) from %s",
                        resolver.decorateTableName(tbDict.getDbTable())));
                doPrepareFullSync(jobService, dbDict, tbDict);
                // 记录增量起始时间
                redisService.set(lastKey, new TaskRowRecord((System.currentTimeMillis() - 300 * 1000L) + "", null));
                dataAdapter.query(con -> {
                    PreparedStatement preparedStatement =
                            con.prepareStatement(String.format("select t.* from %s t",
                                    resolver.decorateTableName(tbDict.getDbTable())),
                                    ResultSet.TYPE_FORWARD_ONLY,
                                    ResultSet.CONCUR_READ_ONLY);
                    preparedStatement.setFetchSize(1000);
                    preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
                    return preparedStatement;
                }, rs -> {
                    fullSyncRsProcess(rs, dbDict.getDbName(), dbDict.getDbUser(), tbDict.getDbTable(), DbType.SQL_SERVER);
                });
                doFinishFullSync(jobService, dbDict, tbDict);
                return true;
            } else {
//                TaskRowRecord rowRecord = redisService.get(lastKey, TaskRowRecord.class);
//                if (rowRecord == null) {
//                    rowRecord = new TaskRowRecord(appendStartTimestamp * Constants.CANAL_TIMESTAMP_UNIT + "", null);
//                }
//                ExTableMapping tbMapping = monitorResultService.queryTableMapping(taskId);
//                Assert.isNull(tbMapping, "找不到表映射,taskId=" + taskId);
//                List<ExMonitorMysql> monitorResults = monitorResultService.pageExMonitorMysql(taskId, tbMapping.getSourceSyncid(), 10000, rowRecord);
//                monitorResults = dealSemicolon(monitorResults);
//                if (!monitorResults.isEmpty()) {
//                    ExMonitorMysql lastResult = null;
//                    log.info("Get old row record: {}", JsonUtil.toJson(rowRecord));
//                    for (ExMonitorMysql m : monitorResults) {
//                        if (!JobContainer.getInstance().checkJobFailed(taskId.toString()) && !JobContainer.getInstance().getLock().get()) {
//                            try {
//                                sendWithSplit(topic, taskId, m.getData(), PluginConfig.SyncType.APPEND);
//                                lastResult = m;
//                            } catch (Exception e) {
//                                log.error("Send monitorResult error, maybe mq server shutdown: ", e);
//                                break;
//                            }
//                        }
//                    }
//                    if (lastResult != null) {
//                        TaskRowRecord newRowRecord = new TaskRowRecord(lastResult.getId(), null);
//                        redisService.set(lastKey, newRowRecord);
//                        log.info("Set new row record: {}", JsonUtil.toJson(newRowRecord));
//                        log.info("task-{}, mq发送数量{}", taskId, monitorResults.size());
//                    }
//                }
//                // 发送实时速率统计
//                SyncRateUtil.addImmeStat(taskId.toString(), System.currentTimeMillis(), monitorResults.size(), 1);
            }
            return false;
        }

        /**
         * 处理mysql数据分块
         */
        private List<ExMonitorMysql> dealSemicolon(List<ExMonitorMysql> originMonitorResultList) {
            List<ExMonitorMysql> targetMonitorResult = new ArrayList<>();
            String sqlTmp = "";
            for (ExMonitorMysql monitorResult : originMonitorResultList) {
                sqlTmp = sqlTmp.concat(monitorResult.getData());
                if (monitorResult.getNextFlag() == null || monitorResult.getNextFlag().equals(0)) {
                    monitorResult.setData(sqlTmp);
                    targetMonitorResult.add(monitorResult);
                    sqlTmp = "";
                }
            }
            return targetMonitorResult;
        }

        @Override
        public void init() throws Exception {
            super.init();
        }

        @Override
        public void destroy(boolean clear) throws Exception {
        }
    }
}
