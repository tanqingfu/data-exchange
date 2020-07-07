package com.sdy.dataexchange.plugin.oracle;

import com.sdy.common.utils.Assert;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.model.DTO.TaskRowRecord;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.biz.model.MonitorResult;
import com.sdy.dataexchange.biz.redis.CacheNames;
import com.sdy.dataexchange.core.DataJob;
import com.sdy.dataexchange.core.JobContainer;
import com.sdy.dataexchange.core.JobState;
import com.sdy.dataexchange.core.util.CacheUtil;
import com.sdy.dataexchange.core.util.GcUtil;
import com.sdy.dataexchange.core.util.SyncRateUtil;
import com.sdy.dataexchange.plugin.common.DbFmtResolver;
import com.sdy.dataexchange.plugin.common.ITypeConvert;
import com.sdy.dataexchange.plugin.common.Reader;
import com.sdy.dataexchange.plugin.common.SqlParserUtil;
import com.sdy.dataexchange.plugin.common.converts.OracleTypeConvert;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.dataexchange.plugin.config.PluginConfig;
import com.sdy.mvc.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.context.ApplicationContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Slf4j
public class OracleReader extends Reader {
    public static class Task extends Reader.AbstractCommonDbJobFragment {
        OracleProcedureParser oracleProcedureParser = OracleProcedureParser.getInstance();
        DbFmtResolver resolver = PluginConfig.getDbResolverMap().get(DbType.ORACLE);

        public Task(ApplicationContext applicationContext, Integer taskId, Boolean fullSync) {
            super(applicationContext, taskId, fullSync);
            this.typeConvert = new OracleTypeConvert();
        }

        /**
         * 查询增量
         */
        private List<MonitorResult> pagingSelectOracleMonitorResult(Integer taskId, Integer tableId, Integer row, TaskRowRecord taskRowRecord) throws InstantiationException, IllegalAccessException {
            DataAdapter dataAdapter = monitorResultService.getSyncDataAdapterByTaskId(taskId);
            if (taskRowRecord == null) {
                String sql = String.format(
                        "select * from (select * from MONITOR_RESULT where TABLE_ID='%s' and OPER_CODE <> 10 and OPER_CODE <> 11 order by DEALTIME, SCN_NO) " +
                                "where ROWNUM <= %d",
                        tableId, row);
                return dataAdapter.executeQuery(sql, MonitorResult.class);
            } else {
                String sql = String.format(
                        "select * from (select * from MONITOR_RESULT where TABLE_ID='%s' and DEALTIME>'%s' and OPER_CODE <> 10 and OPER_CODE <> 11 order by DEALTIME, SCN_NO) " +
                                "where ROWNUM <= %d",
                        tableId, taskRowRecord.getDealtime(), row);
                return dataAdapter.executeQuery(sql, MonitorResult.class);
            }
        }

        @Override
        public boolean run() throws Exception {
            String lastKey = RedisConstants.REDIS_LAST_SYNC_KEY + taskId;
            ExDbDict dbDict = monitorResultService.queryExDbDictSrc(taskId);
            ExTableDict tbDict = monitorResultService.queryExTableDictSrc(taskId);
            DataAdapter dataAdapter = monitorResultService.getSrcDbDataAdapterByTaskId(taskId);
            // 获取源表字段类型
            Map<String, String> srcFieldDict = CacheUtil.cacheProcessing(
                    DataJob.CACHE_BUCKET_DATASYNC + taskId,
                    CacheNames.getSrcFieldDict + taskId,
                    () -> monitorResultService.getFieldDict(tbDict.getSyncId()));
            srcFieldDict.forEach((k, v) -> srcFieldDict.put(k, SqlParserUtil.convertRawtypeToStandard(v)));
            if (fullSync) {
                fullSyncReadOriginCount = dataAdapter.count(String.format("select count(*) from %s.%s",
                        resolver.decorateSchemaName(resolver.parserSchemaName(dbDict.getDbName(), dbDict.getDbUser())),
                        resolver.decorateTableName(tbDict.getDbTable())));
                doPrepareFullSync(jobService, dbDict, tbDict);
                // 记录增量起始时间
                redisService.set(lastKey, new TaskRowRecord(DateUtil.getDate(DateUtil.addSecond(new Date(), -300), "yyyy-MM-dd HH:mm:ss.10000001    "), null));
                dataAdapter.query(con -> {
                    PreparedStatement preparedStatement =
                            con.prepareStatement(String.format("select %s, ROWID from %s.%s",
                                    srcFieldDict.keySet().stream().map(resolver::decorateColumnName).collect(Collectors.joining(",")),
                                    resolver.decorateSchemaName(resolver.parserSchemaName(dbDict.getDbName(), dbDict.getDbUser())),
                                    resolver.decorateTableName(tbDict.getDbTable())),
                                    ResultSet.TYPE_FORWARD_ONLY,
                                    ResultSet.CONCUR_READ_ONLY);
                    preparedStatement.setFetchSize(1000);
                    preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
                    return preparedStatement;
                }, rs -> {
                    fullSyncRsProcess(rs, dbDict.getDbName(), dbDict.getDbUser(), tbDict.getDbTable(), DbType.ORACLE);
                });
                doFinishFullSync(jobService, dbDict, tbDict);
                return true;
            } else {
                synchronized (Reader.class) {
                    int byteSize = 0;
                    TaskRowRecord rowRecord = redisService.get(lastKey, TaskRowRecord.class);
                    if (rowRecord == null) {
                        rowRecord = new TaskRowRecord(DateUtil.getDate(new Date(appendStartTimestamp), "yyyy-MM-dd HH:mm:ss.10000001    "), null);
                    }
                    ExTableMapping tbMapping = monitorResultService.queryTableMapping(taskId);
                    Assert.isNull(tbMapping, "找不到表映射,taskId=" + taskId);
                    List<MonitorResult> monitorResults = pagingSelectOracleMonitorResult(taskId, tbMapping.getSourceSyncid(), 10000, rowRecord);
                    monitorResults = dealSemicolon(monitorResults);
                    monitorResults = dealBlob(monitorResults, srcFieldDict, tbDict.getSyncId(), resolver.parserSchemaName(dbDict.getDbName(), dbDict.getDbUser()),
                            dbDict.getDbName(), tbDict.getDbTable(), dataAdapter);
                    if (!monitorResults.isEmpty()) {
                        MonitorResult lastResult = null;
                        log.info("Get old row record: {}", JsonUtil.toJson(rowRecord));
                        for (MonitorResult m : monitorResults) {
                            if (!JobContainer.getInstance().checkJobFailed(taskId.toString()) && !JobContainer.getInstance().getLock().get()) {
                                try {
                                    byte[] byteData = JsonUtil.toJson(m).getBytes(RemotingHelper.DEFAULT_CHARSET);
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
                            TaskRowRecord newRowRecord = new TaskRowRecord(lastResult.getDealtime(), lastResult.getScnNo());
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
         * 处理oracle语句中不是分号结尾的情况
         */
        private List<MonitorResult> dealSemicolon(List<MonitorResult> originMonitorResultList) {
            List<MonitorResult> targetMonitorResult = new ArrayList<>();
            List<String> sqlTmp = new ArrayList<>();
            for (MonitorResult monitorResult : originMonitorResultList) {
                // 25 select for update
                // 255 unsupport
                // filter
                if (monitorResult.getOperCode().equals(255)
                        || monitorResult.getOperCode().equals(25)
                        || "BBBBBBBBBBBBBBBBBB".equals(monitorResult.getRowseq())) {
                    continue;
                }
                sqlTmp.add(monitorResult.getSqlRedo());
                if ((monitorResult.getIsComplete() == null || monitorResult.getIsComplete().equals(0))
                        && isValidRowId(monitorResult.getRowseq())) {
                    String statement;
                    if (oracleProcedureParser.check(sqlTmp)) {
                        statement = oracleProcedureParser.parse(sqlTmp);
                    } else {
                        statement = String.join("", sqlTmp);
                    }
                    monitorResult.setSqlRedo(statement);
                    targetMonitorResult.add(monitorResult);
                    sqlTmp.clear();
                }
            }
            return targetMonitorResult;
        }

        /**
         * 处理BLOB、CLOB字段
         */
        private List<MonitorResult> dealBlob(List<MonitorResult> monitorResultList, Map<String, String> srcFieldDict, Integer tableId, String schemaName, String dbName, String tableName,
                              DataAdapter dataAdapter) {
            if (monitorResultList.isEmpty()) {
                return monitorResultList;
            }
            List<MonitorResult> result = new ArrayList<>();
            String querySqlTemplate = String.format("select %s, ROWID from %s.%s",
                    srcFieldDict.keySet().stream().map(resolver::decorateColumnName).collect(Collectors.joining(",")),
                    resolver.decorateSchemaName(schemaName),
                    resolver.decorateTableName(tableName))
                    + " where %s";
            // 主键字段
            String pkName = "ROWID";
            Set<String> rowIdSet = new HashSet<>();
            monitorResultList.forEach(monitorResult -> {
                if (monitorResult.isBlobData()) {
                    if (monitorResult.getOperCode().equals(3)) {
                        // TODO 对于只更新非BLOB字段的情况，不需要整个记录重新查询
                    }
                    if (!rowIdSet.contains(monitorResult.getRowseq())) {
                        BlobTmpObject blobTmpObject = new BlobTmpObject();
                        {
                            SqlRedoObj sqlRedoObj = new SqlRedoObj();
                            sqlRedoObj.setType("DELETE");
                            sqlRedoObj.setEmpty(false);
                            sqlRedoObj.setIsDdl(false);
                            sqlRedoObj.setSchema(schemaName);
                            sqlRedoObj.setTable(tableName);
                            sqlRedoObj.setDatabase(dbName);
                            sqlRedoObj.setDbType(DbType.ORACLE.getDb().toUpperCase());
                            Map<String, Integer> sqlTypeMap = new HashMap<>();
                            Map<String, Object> old = new HashMap<>();
                            old.put(pkName, monitorResult.getRowseq());
                            sqlTypeMap.put(pkName, Types.CHAR);
                            sqlRedoObj.setData(Collections.emptyList());
                            sqlRedoObj.setOld(Collections.singletonList(old));
                            sqlRedoObj.setPkNames(Collections.singletonList(pkName));
                            sqlRedoObj.setSqlType(sqlTypeMap);
                            blobTmpObject.setObj1(sqlRedoObj);
                        }
                        SqlRedoObj objectData = queryByRowId(querySqlTemplate, monitorResult.getRowseq(), dataAdapter);
                        if (objectData != null) {
                            objectData.setSchema(schemaName);
                            objectData.setTable(tableName);
                            objectData.setDatabase(dbName);
                            objectData.setDbType(DbType.ORACLE.getDb().toUpperCase());
                            blobTmpObject.setObj2(objectData);
                        }
                        monitorResult.setSqlRedo(null);
                        result.add(MonitorResultPlus.fromBase(monitorResult).setBlobData(blobTmpObject));
                        rowIdSet.add(monitorResult.getRowseq());
                    } else {
                        result.get(result.size() - 1)
                                .setScnNo(monitorResult.getScnNo())
                                .setDealtime(monitorResult.getDealtime());
                    }
                } else {
                    result.add(monitorResult);
                }
            });
            return result;
        }
        
        private PreparedStatement createPreparedStatement(Connection con, String querySqlTemplate, String rowId, String whereStatement) throws SQLException {
            PreparedStatement preparedStatement =
                    con.prepareStatement(String.format(querySqlTemplate, String.format("ROWID='%s'", rowId)),
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(1);
            preparedStatement.setFetchDirection(ResultSet.FETCH_FORWARD);
            return preparedStatement;
        }
        
        private SqlRedoObj queryByRowId(String querySqlTemplate, String rowId, DataAdapter dataAdapter) {
            SqlRedoObj sqlRedoObj = new SqlRedoObj();
            AtomicBoolean exist = new AtomicBoolean(false);
            dataAdapter.query(con -> createPreparedStatement(con, querySqlTemplate, rowId, ""), rs -> {
                // 校验任务是否出错
                if (JobContainer.getInstance().checkJobFailed(taskId.toString())) {
                    throw new RuntimeException("任务被标记为失败, 全量同步中止. TaskId=" + taskId);
                }
                ResultSetMetaData metaData = rs.getMetaData();
                int count = metaData.getColumnCount();
                String[] fieldNames = new String[count];
                Map<String, Integer> columnTypeMap = new HashMap<>();
                Map<String, String> columnTypeNameMap = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    fieldNames[i] = metaData.getColumnName(i + 1);
                    String typeName = metaData.getColumnTypeName(i + 1);
                    if (typeName == null) {
                        typeName = "VARCHAR";
                    }
                    columnTypeMap.put(fieldNames[i], typeConvert.processSqlTypeConvert(typeName));
                    columnTypeNameMap.put(fieldNames[i], SqlParserUtil.convertRawtypeToStandard(typeName));
                }
                Map<String, Object> data = new HashMap<>(fieldNames.length << 1);
                for (String fieldName : fieldNames) {
                    String type = columnTypeNameMap.get(fieldName);
                    Object obj;
                    if ("TIMESTAMP".equals(type)
                            || "DATE".equals(type)
                            || "TIME".equals(type)
                            || "TIMESTAMP WITH LOCAL TIME ZONE".equals(type)
                            || "TIMESTAMP WITH TIME ZONE".equals(type)) {
                        obj = rs.getTimestamp(fieldName);
                    } else if ("DOUBLE".equals(type)
                            || "FLOAT".equals(type)
                            || "NUMBER".equals(type)) {
                        obj = rs.getString(fieldName);
                    } else {
                        obj = rs.getObject(fieldName);
                    }
                    Object dataObj = typeConvert.formatValue(obj, type, 1);
                    data.put(fieldName, dataObj);
                }
                sqlRedoObj.setData(Collections.singletonList(data));
                sqlRedoObj.setIsDdl(false);
                sqlRedoObj.setOld(null);
                sqlRedoObj.setType("INSERT");
                sqlRedoObj.setPkNames(null);
                sqlRedoObj.setTaskId(taskId);
                sqlRedoObj.setEmpty(false);
                sqlRedoObj.setSqlType(columnTypeMap);
                sqlRedoObj.setMysqlType(Collections.emptyMap());
                exist.set(true);
            });
            return exist.get() ? sqlRedoObj : null;
        }

        /**
         * AAAAAAAAAAAAAAAAAB TRUNCATE
         * BBBBBBBBBBBBBBBBBB BLOB
         * @param rowId
         * @return
         */
        private boolean isValidRowId(String rowId) {
            if (rowId == null) {
                return false;
            }
            if ("AAAAAAAAAAAAAAAAAB".equals(rowId)) {
                throw new RuntimeException("TRUNCATE语句，任务中止");
            }
            /**
             * TODO truncate B结尾
             */
            return rowId.length() > 0 && !rowId.startsWith("AAAAAAAAAAAAAAAA");
        }
        
        @Override
        public synchronized void init() throws Exception {
            super.init();
        }

        @Override
        public synchronized void destroy(boolean clear) throws Exception {
        }
    }
    
    public static class OracleProcedureParser {
        private static OracleProcedureParser procedureParser = new OracleProcedureParser();
        public static OracleProcedureParser getInstance() {
            return procedureParser;
        }
        public boolean check(List<String> statements) {
            return false;
//            if (statements.size() < 3) {
//                return false;
//            }
//            boolean tokenStart = statements.get(0).startsWith("insert into");
//            boolean tokenDeclare = false;
//            boolean tokenForUpdate = false;
//            for (String statement : statements) {
//                if (statement.startsWith("DECLARE \n")) {
//                    tokenDeclare = true;
//                }
//                if (statement.endsWith(" for update;")) {
//                    tokenForUpdate = true;
//                }
//                if (tokenStart && tokenDeclare && tokenForUpdate) {
//                    return true;
//                }
//            }
//            return false;
        }
        public String parse(List<String> statements) {
            return "";
        }
    }
}
