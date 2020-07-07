package com.sdy.dataexchange.plugin.mysql;

import com.sdy.common.utils.Assert;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.model.DTO.TaskRowRecord;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.core.JobContainer;
import com.sdy.dataexchange.core.util.GcUtil;
import com.sdy.dataexchange.core.util.SyncRateUtil;
import com.sdy.dataexchange.plugin.common.Constants;
import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.Reader;
import com.sdy.dataexchange.plugin.common.converts.MySqlTypeConvert;
import com.sdy.dataexchange.plugin.config.PluginConfig;
import com.sdy.mvc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.context.ApplicationContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MysqlReader extends Reader {
    public static class Task extends Reader.AbstractCommonDbJobFragment {

        public Task(ApplicationContext applicationContext, Integer taskId, Boolean fullSync) {
            super(applicationContext, taskId, fullSync);
            this.typeConvert = new MySqlTypeConvert();
        }

        @Override
        public boolean run() throws Exception {
            String lastKey = RedisConstants.REDIS_LAST_SYNC_KEY + taskId;
            if (fullSync) {
                ExDbDict dbDict = monitorResultService.queryExDbDictSrc(taskId);
                ExTableDict tbDict = monitorResultService.queryExTableDictSrc(taskId);
                DataAdapter dataAdapter = monitorResultService.getSrcDbDataAdapterByTaskId(taskId);
                DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.MYSQL);
                fullSyncReadOriginCount = dataAdapter.count(String.format("select count(*) from %s.%s",
                        resolver.parserSchemaName(dbDict.getDbName(), dbDict.getDbUser()), resolver.decorateTableName(tbDict.getDbTable())));
                doPrepareFullSync(jobService, dbDict, tbDict);
                // 记录增量起始时间
                redisService.set(lastKey, new TaskRowRecord((System.currentTimeMillis() - 300 * 1000L) * Constants.CANAL_TIMESTAMP_UNIT + "", null));
                dataAdapter.query(con -> {
                    PreparedStatement preparedStatement =
                            con.prepareStatement(String.format("select t.* from %s.%s t",
                                    resolver.parserSchemaName(dbDict.getDbName(), dbDict.getDbUser()), resolver.decorateTableName(tbDict.getDbTable())),
                                    ResultSet.TYPE_FORWARD_ONLY,
                                    ResultSet.CONCUR_READ_ONLY);
                    preparedStatement.setFetchSize(2000);
                    preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
                    return preparedStatement;
                }, rs -> {
                    fullSyncRsProcess(rs, dbDict.getDbName(), dbDict.getDbUser(), tbDict.getDbTable(), DbType.MYSQL);
                });
                doFinishFullSync(jobService, dbDict, tbDict);
                return true;
            } else {
                synchronized (Reader.class) {
                    int byteSize = 0;
                    TaskRowRecord rowRecord = redisService.get(lastKey, TaskRowRecord.class);
                    if (rowRecord == null) {
                        rowRecord = new TaskRowRecord(appendStartTimestamp * Constants.CANAL_TIMESTAMP_UNIT + "", null);
                    }
                    ExTableMapping tbMapping = monitorResultService.queryTableMapping(taskId);
                    Assert.isNull(tbMapping, "找不到表映射,taskId=" + taskId);
                    AtomicBoolean gcFlag = new AtomicBoolean(false);
                    List<ExMonitorMysql> monitorResults = monitorResultService.pageExMonitorMysql(taskId, tbMapping.getSourceSyncid(), 10000, rowRecord, gcFlag);
                    monitorResults = dealSemicolon(monitorResults);
                    if (!monitorResults.isEmpty()) {
                        ExMonitorMysql lastResult = null;
                        log.info("Get old row record: {}", JsonUtil.toJson(rowRecord));
                        for (ExMonitorMysql m : monitorResults) {
                            if (!JobContainer.getInstance().checkJobFailed(taskId.toString()) && !JobContainer.getInstance().getLock().get()) {
                                try {
                                    byte[] byteData = StringUtil.isBlank(m.getData()) ? m.getByteData() : m.getData().getBytes(RemotingHelper.DEFAULT_CHARSET);
                                    sendByteWithSplit(topic, taskId, byteData, PluginConfig.SyncType.APPEND);
                                    byteSize += byteData.length;
                                    lastResult = m;
                                } catch (Exception e) {
                                    log.error("Send monitorResult error, maybe mq server shutdown: ", e);
                                    break;
                                }
                            }
                        }
                        if (lastResult != null) {
                            TaskRowRecord newRowRecord = new TaskRowRecord(lastResult.getId(), null);
                            redisService.set(lastKey, newRowRecord);
                            log.info("Set new row record: {}", JsonUtil.toJson(newRowRecord));
                            log.info("task-{}, mq发送数量{}", taskId, monitorResults.size());
                        }
                    }
                    // 发送实时速率统计
                    SyncRateUtil.addImmeStat(taskId.toString(), System.currentTimeMillis(), monitorResults.size(), 1);
                    // 回收对象
                    monitorResults = null;
                    GcUtil.byteAddUp(byteSize);
                }
            }
            return false;
        }

        /**
         * 处理mysql数据分块
         */
        private List<ExMonitorMysql> dealSemicolon(List<ExMonitorMysql> originMonitorResultList) {
            List<ExMonitorMysql> targetMonitorResult = new ArrayList<>();
            String sqlTmp = "";
            byte[] sqlTmpByte = null;
            for (ExMonitorMysql monitorResult : originMonitorResultList) {
                if (monitorResult.getData() != null) {
                    sqlTmp = sqlTmp.concat(monitorResult.getData());
                }
                sqlTmpByte = sqlTmpByte == null ? monitorResult.getByteData() : ArrayUtils.addAll(sqlTmpByte, monitorResult.getByteData());
                if (monitorResult.getNextFlag() == null || monitorResult.getNextFlag().equals(0)) {
                    monitorResult.setData(sqlTmp);
                    monitorResult.setByteData(sqlTmpByte);
                    targetMonitorResult.add(monitorResult);
                    sqlTmp = "";
                    sqlTmpByte = null;
                }
            }
            return targetMonitorResult;
        }

        @Override
        public void init() throws Exception {
            super.init();
            jobService.addCanalConfig(taskId);
            // canal获取最新配置需要5s，所以这里等它10s
            Thread.sleep(10000L);
        }

        @Override
        public void destroy(boolean clear) throws Exception {
            if (clear) {
                jobService.removeCanalConfig(taskId);
            }
        }
    }
}
