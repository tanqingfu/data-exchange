package com.sdy.dataexchange.plugin.common;

import com.sdy.common.utils.DateUtil;
import com.sdy.dataadapter.DbType;
import com.sdy.dataexchange.biz.constants.MqConstants;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.constants.SyncLogConstants;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExJobInfo;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.service.JobService;
import com.sdy.dataexchange.biz.service.MonitorResultService;
import com.sdy.dataexchange.core.DataJob;
import com.sdy.dataexchange.core.JobContainer;
import com.sdy.dataexchange.core.JobFragment;
import com.sdy.dataexchange.core.JobState;
import com.sdy.dataexchange.core.JobStateChecker;
import com.sdy.dataexchange.core.util.GcUtil;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.dataexchange.plugin.config.PluginConfig;
import com.sdy.mq.service.MqProducerService;
import com.sdy.mvc.utils.JsonUtil;
import com.sdy.redis.service.RedisService;
import javafx.util.Pair;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.context.ApplicationContext;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public abstract class Reader {
    
    public static abstract class AbstractJobFragment implements JobFragment {
        @Setter
        protected JobStateChecker jobStateChecker;
    }
    
    public static abstract class AbstractCommonDbJobFragment extends AbstractJobFragment {
        protected Integer taskId;
        protected String topic;
        protected Boolean fullSync;
        /**
         * 增量开始时间
         */
        protected Long appendStartTimestamp;
        protected ITypeConvert typeConvert;
        protected JobService jobService;
        protected MonitorResultService monitorResultService;
        protected MqProducerService mqProducerService;
        protected RedisService redisService;

        protected long fullSyncReadOriginCount = 0L;
        private long fullSyncReadCount = 0L;
        private long fullSyncReadMilli = 0L;
        private static final int ROCKETMQ_MAX_PACKET_SIZE = 4000000;

        @Override
        public Pair<Long, Long> getSyncProcess() {
            return new Pair<>(fullSyncReadCount, fullSyncReadOriginCount);
        }

        public AbstractCommonDbJobFragment(ApplicationContext applicationContext, Integer taskId, Boolean fullSync) {
            monitorResultService = applicationContext.getBean(MonitorResultService.class);
            mqProducerService = applicationContext.getBean(MqProducerService.class);
            redisService = applicationContext.getBean(RedisService.class);
            jobService = applicationContext.getBean(JobService.class);
            this.taskId = taskId;
            this.topic = MqConstants.Topics.TOPIC_DATAEXCHANGE_PREFIX + taskId;
            this.fullSync = fullSync;
        }

        @Override
        public void init() throws Exception {
            ExJobInfo jobInfo = jobService.getJobInfoByTaskId(taskId);
            this.appendStartTimestamp = jobInfo.getDealTime() == null ? 0 : jobInfo.getDealTime().getTime();
            jobService.updateStartTimestamp(this.appendStartTimestamp, taskId);
            jobService.saveLog(
                    taskId,
                    SyncLogConstants.LogType.INFO,
                    String.format("任务[%s][%s]启动%s. %s",
                            SyncLogConstants.LogPlaceHolder.JOB_INFO,
                            SyncLogConstants.LogPlaceHolder.JOB_TASK,
                            fullSync ? "全量" : "增量",
                            jobInfo.getDealTime() == null ? "" : "增量起始时间:" + DateUtil.formatTime(jobInfo.getDealTime())));
        }

        @Override
        public boolean bFull() {
            return this.fullSync;
        }

        /**
         * 发送sql消息
         */
        private void send(String topic, Integer taskId, String message, String syncType, String hasNext) throws Exception {
            mqProducerService.tSend(topic, taskId.toString().concat("###").concat(syncType).concat("###").concat(hasNext), message, true);
        }

        /**
         * 发送sql消息
         */
        private void send(String topic, Integer taskId, byte[] message, String syncType, String hasNext) throws Exception {
            mqProducerService.tSend(topic, taskId.toString().concat("###").concat(syncType).concat("###").concat(hasNext), message, true);
        }

        /**
         * 发送sql消息
         */
        protected void sendByteWithSplit(String topic, Integer taskId, byte[] message, String syncType) throws Exception {
            List<byte[]> splitList = SqlParserUtil.splitMysqlMonitorByte(message, ROCKETMQ_MAX_PACKET_SIZE);
            for (int i = 0; i < splitList.size(); i++) {
                send(topic, taskId, splitList.get(i), syncType, i == splitList.size() - 1 ? "0" : "1");
            }
        }
        
        protected void validateJobFullSyncFinished(String jobId) {
            jobStateChecker.convertToAppendeMode(jobId);
        }
        
        private void waitForMillis(long millis) {
            try {
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                log.error("Sleep error！");
                throw new RuntimeException("", e);
            }
        }
        
        protected void fullSyncRsProcess(ResultSet rs, String dbName, String dbUser, String dbTable, DbType dbType) throws SQLException {
            DataJob job = JobContainer.getInstance().getJob(taskId.toString());
            // 校验任务是否出错
            if (JobContainer.getInstance().checkJobFailed(taskId.toString())) {
                // 等待mq消费完成
                waitForMillis(10000);
                throw new RuntimeException("任务被标记为失败, 全量同步中止. TaskId=" + taskId);
            }
            // 每10000条数据检查一次任务数据库状态
            if (job != null && fullSyncReadCount % 10000 == 0) {
                JobState.DbState jobState = job.getJobStateChecker().getRunningState(job);
                if (!jobState.equals(JobState.DbState.RUNNING)) {
                    JobState.State tmp = job.getJobState().getState();
                    job.getJobState().setState(JobState.State.FAILED);
                    // 等待mq消费完成
                    waitForMillis(10000);
                    job.getJobState().setState(tmp);
                    throw new RuntimeException("任务状态标记异常, 全量同步中止. TaskId=" + taskId);
                }
                // 让CPU缓一下
                waitForMillis(1000);
            }
            // 校验任务是否停止
            if (job == null || job.getJobState().getState().equals(JobState.State.DESTROYED)) {
                // 等待mq消费完成
                if (job != null) {
                    job.getJobState().setState(JobState.State.FAILED);
                    waitForMillis(10000);
                    job.getJobState().setState(JobState.State.DESTROYED);
                }
                throw new RuntimeException("任务状态标记异常, 全量同步中止. TaskId=" + taskId);
            }
//            // 每1000条检查进度，如果读写差距10000条，则等待2s
//            if (fullSyncReadCount % 1000 == 0) {
//                Pair<Long, Long> syncWriteProcess = JobContainer.getInstance().getFullSyncWriteProcess(taskId.toString(), jobId -> new Pair<>(
//                        redisService.getOrDefault(RedisConstants.REDIS_FULL_SYNC_COUNT + jobId, Integer.class, -1).longValue(),
//                        redisService.getOrDefault(RedisConstants.REDIS_FULL_SYNC_COUNT_TOTAL + jobId, Integer.class, -1).longValue()
//                ));
//                if (fullSyncReadCount - syncWriteProcess.getKey() > 10000) {
//                    waitForMillis(2000);
//                }
//            }
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
            int byteSize = 0;
            for (String fieldName : fieldNames) {
                String type = columnTypeNameMap.get(fieldName);
                Object obj;
                if (DbType.SQL_SERVER.equals(dbType) && "TIMESTAMP".equals(type)) {
                    obj = rs.getObject(fieldName);
                } else if ("TIMESTAMP".equals(type)
                        || "DATE".equals(type)
                        || "TIME".equals(type)
                        || DbType.ORACLE.equals(dbType) && "TIMESTAMP WITH LOCAL TIME ZONE".equals(type)
                        || DbType.ORACLE.equals(dbType) && "TIMESTAMP WITH TIME ZONE".equals(type)
                        || DbType.SQL_SERVER.equals(dbType) && "DATETIMEOFFSET".equals(type)) {
                    obj = rs.getTimestamp(fieldName);
                } else if ("DOUBLE".equals(type)
                        || "FLOAT".equals(type)
                        || DbType.ORACLE.equals(dbType) && "NUMBER".equals(type)
                        || DbType.SQL_SERVER.equals(dbType) && "REAL".equals(type)
                        || DbType.SQL_SERVER.equals(dbType) && "SMALLMONEY".equals(type)
                        || DbType.SQL_SERVER.equals(dbType) && "MONEY".equals(type)
                        || DbType.SQL_SERVER.equals(dbType) && "NUMERIC".equals(type)) {
                    obj = rs.getString(fieldName);
                } else {
                    obj = rs.getObject(fieldName);
                }
                Object dataObj = typeConvert.formatValue(obj, type, 1);
                data.put(fieldName, dataObj);
                if (dataObj instanceof String) {
                    byteSize += ((String) dataObj).length();
                }
            }
            SqlRedoObj sqlRedoObj = new SqlRedoObj();
            sqlRedoObj.setData(Collections.singletonList(data));
            sqlRedoObj.setIsDdl(false);
            sqlRedoObj.setOld(null);
            sqlRedoObj.setType("INSERT");
            sqlRedoObj.setDatabase(dbName);
            sqlRedoObj.setPkNames(null);
            sqlRedoObj.setSchema(dbUser);
            sqlRedoObj.setTable(dbTable);
            sqlRedoObj.setTaskId(taskId);
            sqlRedoObj.setEmpty(false);
            sqlRedoObj.setSqlType(columnTypeMap);
            sqlRedoObj.setMysqlType(Collections.emptyMap());
            try {
                sendByteWithSplit(topic, taskId, JsonUtil.toJson(sqlRedoObj).getBytes(RemotingHelper.DEFAULT_CHARSET), PluginConfig.SyncType.FULL);
            } catch (Exception e) {
                log.error("Full sync error!", e);
                throw new RuntimeException(e);
            }
            ++fullSyncReadCount;
            data = null;
            sqlRedoObj = null;
            GcUtil.byteAddUp(byteSize);
        }
        
        protected void doPrepareFullSync(JobService jobService, ExDbDict dbDict, ExTableDict tbDict) {
            fullSyncReadCount = 0;
            fullSyncReadMilli = System.currentTimeMillis();
            log.info("开始全量数据抽取, taskId={}, 源:[{}.{}.{}]", taskId, dbDict.getDbUser(), dbDict.getDbName(), tbDict.getDbTable());
            jobService.saveLog(
                    taskId,
                    SyncLogConstants.LogType.INFO,
                    String.format("任务[%s][%s]开始全量数据抽取, 数据源:[%s.%s.%s], 数据总量:[%s]",
                            SyncLogConstants.LogPlaceHolder.JOB_INFO,
                            SyncLogConstants.LogPlaceHolder.JOB_TASK,
                            dbDict.getDbUser(), dbDict.getDbName(), tbDict.getDbTable(), fullSyncReadOriginCount));
            if (fullSync) {
                redisService.set(RedisConstants.REDIS_FULL_SYNC_COUNT_TOTAL + taskId.toString(), fullSyncReadOriginCount);
            }
        }
        
        protected void doFinishFullSync(JobService jobService, ExDbDict dbDict, ExTableDict tbDict) throws InterruptedException {
            try {
                // 发送一条消息，标记全量结束
                sendByteWithSplit(topic, taskId, JsonUtil.toJson(new SqlRedoObj().setEmpty(true).setEnd(true)).getBytes(RemotingHelper.DEFAULT_CHARSET), PluginConfig.SyncType.FULL);
            } catch (Exception e) {
                log.error("Full sync end error!", e);
                throw new RuntimeException(e);
            }
            long costMilli = System.currentTimeMillis() - fullSyncReadMilli;
            log.info("全量数据抽取完成, taskId={}, 抽取数量:{}, 耗时:{}, 源:[{}.{}.{}]",
                    taskId,
                    fullSyncReadCount,
                    String.format("%.2f秒", costMilli / 1000F),
                    dbDict.getDbUser(),
                    dbDict.getDbName(),
                    tbDict.getDbTable());
            jobService.saveLog(
                    taskId,
                    SyncLogConstants.LogType.INFO,
                    String.format("任务[%s][%s]全量数据抽取完成, 抽取数量:%s, 耗时:%s, 数据源:[%s.%s.%s]",
                            SyncLogConstants.LogPlaceHolder.JOB_INFO,
                            SyncLogConstants.LogPlaceHolder.JOB_TASK,
                            fullSyncReadCount,
                            String.format("%.2f秒", costMilli / 1000F),
                            dbDict.getDbUser(), dbDict.getDbName(), tbDict.getDbTable()));
            validateJobFullSyncFinished(taskId.toString());
            log.info("开始增量数据抽取, taskId={}, 源:[{}.{}.{}], 等待1S", taskId, dbDict.getDbUser(), dbDict.getDbName(), tbDict.getDbTable());
            jobService.saveLog(
                    taskId,
                    SyncLogConstants.LogType.INFO,
                    String.format("任务[%s][%s]开始增量数据抽取, 数据源:[%s.%s.%s]",
                            SyncLogConstants.LogPlaceHolder.JOB_INFO,
                            SyncLogConstants.LogPlaceHolder.JOB_TASK,
                            dbDict.getDbUser(), dbDict.getDbName(), tbDict.getDbTable()));
            redisService.set(RedisConstants.REDIS_FULL_SYNC_COUNT_READ + taskId.toString(), fullSyncReadOriginCount);
            Thread.sleep(1000);
        }
    }
    
    public static abstract class AbstractSqlParser implements SqlParser {
    }
}
