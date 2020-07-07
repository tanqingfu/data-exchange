package com.sdy.dataexchange.plugin.cmdb;

import com.sdy.common.utils.Assert;
import com.sdy.common.utils.DateUtil;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.constants.MqConstants;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.constants.SyncLogConstants;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.redis.CacheNames;
import com.sdy.dataexchange.biz.service.JobService;
import com.sdy.dataexchange.biz.service.MonitorResultService;
import com.sdy.dataexchange.core.DataJob;
import com.sdy.dataexchange.core.JobContainer;
import com.sdy.dataexchange.core.JobStatistic;
import com.sdy.dataexchange.core.util.CacheUtil;
import com.sdy.dataexchange.core.util.SyncRateUtil;
import com.sdy.dataexchange.plugin.common.*;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.dataexchange.plugin.config.PluginConfig;
import com.sdy.mq.base.BaseBatchMessageConsumer;
import com.sdy.mvc.utils.JsonUtil;
import com.sdy.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 任务分发接收处理
 *
 * @author zhouziqiang
 */
@Slf4j
@Component
public class CommonDbResolveComsumer extends BaseBatchMessageConsumer {
    @Autowired
    private JobService jobService;
    @Autowired
    private MonitorResultService monitorResultService;
    @Autowired
    private MqConsumerOnce mqConsumerOnce;
    @Autowired
    private MqConsumerSplit mqConsumerSplit;
    @Autowired
    private RedisService redisService;

    @Override
    protected boolean consume(List<MessageExt> messageList) {
        try {
            mqConsumerOnce.consume(messageList, msgList0 -> mqConsumerSplit.consume(msgList0, msgList -> {
                List<List<MessageExt>> splitList = splitMsgBySyncType(msgList);
                List<MessageExt> l1 = splitList.get(0);
                List<MessageExt> l2 = splitList.get(1);
                doConsume(l1, PluginConfig.SyncType.FULL);
                doConsume(l2, PluginConfig.SyncType.APPEND);
            }));
        } catch (Exception e) {
            log.error("", e);
            doConsumeFailed(Integer.valueOf(messageList.get(0).getTags().split("###")[0]), SqlParserUtil.throwableToString(e));
        }
        return true;
    }

    private void doConsumeFailed(Integer taskId, String errorMessage) {
        try {
            JobContainer.getInstance().setJobStateFailed(taskId.toString());
            jobService.setJobFailedByTaskId(taskId);
            DbType srcDbType = getSrcDbType(taskId);

            DefaultMQPushConsumer consumer = (DefaultMQPushConsumer) JobContainer.getInstance().getConsumerMap().get(taskId.toString());
            if (consumer != null) {
                consumer.shutdown();
                String topic = MqConstants.Topics.TOPIC_DATAEXCHANGE_PREFIX + taskId;
                log.info("Consumer-1 shutdown, topic: {}", topic);
                redisService.del(RedisConstants.REDIS_LAST_SYNC_KEY + taskId);
            }
            JobContainer.getInstance().getConsumerMap().remove(taskId.toString());
            if (srcDbType.equals(DbType.MYSQL)) {
                // 停止mysql采集
                jobService.removeCanalConfig(taskId);
            }
            jobService.saveLog(
                    taskId,
                    SyncLogConstants.LogType.ERROR,
                    String.format("任务[%s][%s]失败[入库]，原因：%s",
                            SyncLogConstants.LogPlaceHolder.JOB_INFO,
                            SyncLogConstants.LogPlaceHolder.JOB_TASK,
                            errorMessage));
        } catch (Exception e) {
            log.error("Fatal error! Please stop the task manually - 1!");
            log.error("Fatal error! Please stop the task manually - 2!");
            log.error("Fatal error! Please stop the task manually - 3!");
            log.error("Fatal error! Please stop the task manually - 4!");
            log.error("Fatal error! Please stop the task manually - 5!");
            log.error("Fatal error! Please stop the task manually - 6!");
            log.error("Fatal error! Please stop the task manually - 7!");
            log.error("Fatal error! Please stop the task manually - 8!");
            log.error("Fatal error! Please stop the task manually - 9!", e);
            // send sms TODO
        }
    }

    /**
     * 获取源库类型
     */
    private DbType getSrcDbType(Integer jobtaskId) throws Exception {
        ExDbDict exSrcDbDicts = CacheUtil.cacheProcessing(DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.queryExDbDictSrc + jobtaskId,
                () -> monitorResultService.queryExDbDictSrc(jobtaskId));
        DbType srcDbType = DbType.getDbType(exSrcDbDicts.getDbType());
        if (DbType.OTHER.equals(srcDbType)) {
            throw new Exception("Unknown db type: " + exSrcDbDicts.getDbType());
        }
        return srcDbType;
    }

    private void doConsume(List<MessageExt> messageList, String syncType) throws Exception {

        if (messageList.isEmpty()) {
            return;
        }
        boolean finished = false;
        long t1 = System.currentTimeMillis();
        String[] tag = messageList.get(0).getTags().split("###");
        Integer jobtaskId = Integer.valueOf(tag[0]);
        if (JobContainer.getInstance().checkJobFailed(jobtaskId.toString())) {
            // 任务已经标记为失败，直接消费掉多余的消息
            return;
        }
        // 获取源库信息
        DbType srcDbType = getSrcDbType(jobtaskId);
        // 获取sql解析器
        SqlParser parser = PluginConfig.getParserMap().get(srcDbType).get(syncType);
        // 解析消息队列中的原始sql
        List<SqlRedoObj> list = new ArrayList<>();
        // 获取源表
        ExTableDict exSrcTableDicts = CacheUtil.cacheProcessing(
                DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.queryExTableDictSrc + jobtaskId,
                () -> monitorResultService.queryExTableDictSrc(jobtaskId));
        // 获取源表字段类型
        Map<String, String> srcFieldDict = CacheUtil.cacheProcessing(
                DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.getSrcFieldDict + jobtaskId,
                () -> monitorResultService.getFieldDict(exSrcTableDicts.getSyncId()));
        srcFieldDict.forEach((k, v) -> srcFieldDict.put(k, SqlParserUtil.convertRawtypeToStandard(v)));
        for (MessageExt messageExt : messageList) {
            // 过滤因失败导致的旧消息
            String tsStr = CacheUtil.cacheProcessing(CacheNames.CACHE_BUCKET_CONSUME_TS, CacheNames.consumeFailedTsMap + jobtaskId,
                    () -> redisService.getOrDefault(RedisConstants.REDIS_CONSUME_FAILED_TS_MAP + jobtaskId, String.class, "0"));
            long ts = Long.valueOf(tsStr);
            if (messageExt.getBornTimestamp() >= ts) {
                String msg = new String(messageExt.getBody(), Charset.forName("UTF-8"));
                try {
                    List<SqlRedoObj> sqlRedoObj0 = parser.parse(msg, srcFieldDict);
                    list.addAll(sqlRedoObj0);
                } catch (Exception e) {
                    log.error("解析Mq消息出错, TsThreshold:{}, MsgBorn:{}, MsgContent:{}",
                            DateUtil.formatTime(new Date(ts)) + " # " + ts,
                            DateUtil.formatTime(new Date(messageExt.getBornTimestamp())) + " # " + messageExt.getBornTimestamp(),
                            msg);
                    throw e;
                }
            }
        }
        if (list.isEmpty()) {
            return;
        }
        long t2 = System.currentTimeMillis();
        // 构建目标库连接
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("allowMultiQueries", "true");
        ExDbDict exTargetDbDicts = CacheUtil.cacheProcessing(DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.queryExDbDictDest + jobtaskId,
                () -> monitorResultService.queryExDbDictDest(jobtaskId));
        DbType targetDbType = DbType.getDbType(exTargetDbDicts.getDbType());
        if (DbType.OTHER.equals(targetDbType)) {
            throw new Exception("Unknown db type: " + exTargetDbDicts.getDbType());
        }
        DataAdapter dataAdapter = new DataAdapter(new RawDataSource(targetDbType,
                exTargetDbDicts.getDbIp(),
                exTargetDbDicts.getDbPort(),
                exTargetDbDicts.getDbName(),
                exTargetDbDicts.getDbUser(),
                exTargetDbDicts.getDbPasswd(),
                paramMap));
        List<String> sqlBatch = new ArrayList<>();
        // 获取映射关系
        ExTableMapping tableMapping = CacheUtil.cacheProcessing(
                DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.queryTableMapping + jobtaskId,
                () -> monitorResultService.queryTableMapping(jobtaskId));
        ExTableDict exTableDicts = CacheUtil.cacheProcessing(
                DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.queryExTableDictDest + jobtaskId,
                () -> monitorResultService.queryExTableDictDest(jobtaskId));
        List<ExFieldMapping> exFieldMappings = CacheUtil.cacheProcessing(
                DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.queryExFieldMapping + jobtaskId,
                () -> monitorResultService.queryExFieldMapping(
                        tableMapping.getSourceSyncid(),
                        tableMapping.getDestSyncid()));
        Map<String, String> fieldMap = new HashMap<>(exFieldMappings.size() << 1);
        exFieldMappings.forEach(exFieldMapping -> fieldMap.put(exFieldMapping.getSourceSyncname(), exFieldMapping.getDestSyncname()));
        DbFmtResolver rsv = PluginConfig.getDbResolverMap().get(targetDbType);
        SqlRedoFactory sqlRedoFactory = PluginConfig.getRedoFactoryMap().get(targetDbType);
        // 固定ROWID字段映射
        fieldMap.put(parser.getPkName(), "ROWSEQ");
        int syncCount = 0;
        // 获取源表字段类型
        Map<String, String> destFieldDict = CacheUtil.cacheProcessing(
                DataJob.CACHE_BUCKET_DATASYNC + jobtaskId,
                CacheNames.getDestFieldDict + jobtaskId,
                () -> monitorResultService.getFieldDict(exTableDicts.getSyncId()));
        destFieldDict.forEach((k, v) -> destFieldDict.put(k, SqlParserUtil.convertRawtypeToStandard(v)));
        // 构建目标sql
        for (SqlRedoObj sqlRedoObj : list) {
            if (sqlRedoObj.getEnd() != null && sqlRedoObj.getEnd()) {
                finished = true;
            }
            if (sqlRedoObj.getEmpty()) {
                continue;
            }
            sqlRedoObj.setTargetDbType(targetDbType.getDb().toUpperCase());
            try {
                String redoSql = sqlRedoFactory.build(
                        sqlRedoObj,
                        exTargetDbDicts.getDbName(),
                        rsv.parserSchemaName(exTargetDbDicts.getDbName(), exTargetDbDicts.getDbUser()),
                        exTableDicts.getDbTable(),
                        fieldMap,
                        destFieldDict);
                if (redoSql == null) {
                    log.info("Unknown sqlRedoObj: {}", JsonUtil.toJson(sqlRedoObj));
                } else if ("".equals(redoSql)) {
                    log.info("Unnecessary sqlRedoObj: {}", JsonUtil.toJson(sqlRedoObj));
                } else {
                    ++syncCount;
                    sqlBatch.add(redoSql);
                }
            } catch (Exception e) {
                log.error("目标语句生成错误, sqlRedoObj: " + JsonUtil.toJson(sqlRedoObj));
                throw new Exception("目标语句生成错误，源数据对象：" + sqlRedoObj.toIdentifiedString(), e);
            }
        }
        long t3 = System.currentTimeMillis();
        // 批量执行sql
        if (sqlBatch.size() > 0) {
            String url = "D:/opt/";
            String writeName = url + "sql_" + jobtaskId + ".txt";
            Boolean exists = redisService.exists(RedisConstants.REDIS_FILE_WRITE + jobtaskId);
            if (!exists) {
                redisService.set(RedisConstants.REDIS_FILE_WRITE + jobtaskId, writeName);
            }

            //全量同步
            fileWrite(sqlBatch, jobtaskId, dataAdapter,finished);
//            doSqlBatch(jobtaskId, dataAdapter, sqlBatch, syncType, rsv);
            long t4 = System.currentTimeMillis();
            log.info("Complete, task-{}, size: {}, sync count: {}, total time: {} s, parse time: {}%, build time: {}%, write time: {}%",
                    jobtaskId,
                    String.format("%4d", list.size()),
                    String.format("%4d", syncCount),
                    String.format("%.2f", (t4 - t1) / 1000.0),
                    String.format("%.2f", (t2 - t1) * 100.0 / (t4 - t1)),
                    String.format("%.2f", (t3 - t2) * 100.0 / (t4 - t1)),
                    String.format("%.2f", (t4 - t3) * 100.0 / (t4 - t1)));
            doCommitStat(jobtaskId, syncCount);
            // 同步写入数量
            redisService.incr(RedisConstants.REDIS_SYNC_COUNT + jobtaskId, (long) syncCount);
            if (PluginConfig.SyncType.FULL.equals(syncType)) {
                redisService.incr(RedisConstants.REDIS_FULL_SYNC_COUNT + jobtaskId, (long) syncCount);
            }
        }
        //
        if (finished) {
            fileWrite(sqlBatch, jobtaskId, dataAdapter,finished);
            Long startMilli = redisService.get(RedisConstants.REDIS_SYNC_START_MILLI + jobtaskId, Long.class);
            Integer syncCountTotal = redisService.getOrDefault(RedisConstants.REDIS_SYNC_COUNT + jobtaskId, Integer.class, -1);
            String costStr = startMilli == null ? "Unknown" : String.format("%.2f秒", (System.currentTimeMillis() - startMilli) / 1000F);
            log.info("全量数据同步完成, taskId={}, 写入数量:{}, 耗时:{}",
                    jobtaskId,
                    syncCountTotal,
                    costStr);
            jobService.saveLog(
                    jobtaskId,
                    SyncLogConstants.LogType.INFO,
                    String.format("任务[%s][%s]全量数据同步完成, 写入数量:%s, 耗时:%s",
                            SyncLogConstants.LogPlaceHolder.JOB_INFO,
                            SyncLogConstants.LogPlaceHolder.JOB_TASK,
                            syncCountTotal,
                            costStr));

        }
///        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASYNC + jobtaskId);
    }

    /**
     * 创建文件
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    private static boolean createFile(File fileName) throws Exception {
        boolean flag = false;
        try {
            if (!fileName.exists()) {
                fileName.createNewFile();
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public void fileWrite(List<String> list, Integer id, DataAdapter dataAdapter,Boolean finished) throws Exception {
        //获取文件地址

        String fileUrl = redisService.get(RedisConstants.REDIS_FILE_WRITE + id, String.class);
        //创建 File
        File file = new File(fileUrl);
        //判断文件是否存在
        boolean exists1 = file.exists();
        //不存在 创建
        if (!exists1) {
            boolean file1 = createFile(file);
            Assert.notTrue(file1, "SQL文件创建失败!");
        }
        //计算文件大小
        double fileSizeMb = new BigDecimal(file.length() / 1024 / 1024).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
//        log.info("当前文件大小: " + fileSizeMb);
        //获取文件时间
        SimpleDateFormat simple = new SimpleDateFormat(DateUtil.DATETIME_FORMAT);
        Date historyDate = new Date(file.lastModified());
//        log.info("文件创建时间: " + simple.format(historyDate));
//        DateFormat df = new SimpleDateFormat(DateUtil.DATETIME_FORMAT);
        Date nowDate = new Date();
//        log.info("当前时间: " + simple.format(nowDate));
        //时间对比
        long history = historyDate.getTime();
        long now = nowDate.getTime();
        int minutes = (int) ((now - history) / (1000 * 60));
//        log.info("时间差: " + minutes);
        //脚本大小超过20MB 修改时间超过1分钟 则执行
        boolean a = true;
        try (PrintStream ps = new PrintStream(new FileOutputStream(file, true))) {
            if (fileSizeMb >= 10 || minutes >= 1 || finished){
                ps.append(";");
            }else {
                if (file.length() == 0) {
                    String oneSql = list.get(0);
                    String values = oneSql.substring(0, oneSql.indexOf("values"));
                    ps.append(values).append(" values ");
                    a = false;
                }
                for (String s : list) {
                    String values = s.substring(s.indexOf("values") + 7, s.indexOf(";"));
                    if (a) {
                        ps.append(",");
                    }
                    ps.append(values).append("\r\n");
                    a = true;
                }
            }
        }
        if (fileSizeMb >= 10 || minutes >= 1 || finished) {
            Boolean aBoolean = mysqlImport(dataAdapter, fileUrl);
            if (aBoolean) {
                file.delete();
            }
            Assert.notTrue(aBoolean, "SQL脚本执行错误!");
        }
    }

    /**
     * mysql 远程导入
     *
     * @param dataAdapter
     * @return
     */
    public Boolean mysqlImport(DataAdapter dataAdapter, String fileUrl) throws SQLException, IOException {
        DataSource dataSource = dataAdapter.getDataSource();
        Connection connection = dataSource.getConnection();
        ScriptRunner runner = new ScriptRunner(connection);
        Resources.setCharset(Charset.forName("UTF-8")); //设置字符集,不然中文乱码插入错误
        runner.setLogWriter(null);//设置是否输出日志
        // 绝对路径读取
        log.info("开始读取!");
//            FileReader read = new FileReader(new File("D:\\opt\\sql100w.sql"));
        try (FileReader read = new FileReader(new File(fileUrl))) {
            runner.runScript(read);
            runner.closeConnection();
            connection.close();
        }
        log.info("SQL脚本执行完成!");
        return true;
    }


    /**
     * Oracle 远程导入
     *
     * @param dataAdapter
     * @return
     */
    public Boolean oracleImport(DataAdapter dataAdapter) {
        return null;
    }

    /**
     * 批量执行sql
     */
    private void doSqlBatch(Integer jobtaskId, DataAdapter dataAdapter, List<String> sqlBatch, String syncType, DbFmtResolver rsv) {
        String procedureName = "de_consumer_submit_task_" + jobtaskId;
        try {
            List<String> proc = rsv.createProcedure(procedureName, sqlBatch, syncType);
            proc.forEach(dataAdapter::execute);
            dataAdapter.doTransaction(() -> {
                dataAdapter.execute(rsv.createProcedureCaller(procedureName));
                return null;
            });
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    /**
     * 发送统计数据
     */
    private void doCommitStat(Integer jobtaskId, Integer size) {
        // 发送统计数据
        ExJobInfo jobInfo = CacheUtil.cacheProcessing(DataJob.CACHE_BUCKET_DATASYNC + jobtaskId, CacheNames.getJobInfoByTaskId + jobtaskId,
                () -> jobService.getJobInfoByTaskId(jobtaskId));
        if (jobInfo != null) {
            int listSize = size;
            PluginConfig.getJobListenerList().forEach(jobListener -> jobListener.run(
                    new JobStatistic()
                            .setGroupId(jobInfo.getJobId().toString())
                            .setJobId(jobtaskId.toString())
                            .setCount(listSize)
            ));
        }
        // 发送实时速率统计
        SyncRateUtil.addImmeStat(jobtaskId.toString(), System.currentTimeMillis(), size, 2);
    }

    /**
     * 拆分为全量消息和增量消息
     */
    private List<List<MessageExt>> splitMsgBySyncType(List<MessageExt> messageList) {
        int appendTypeIdx = 0;
        for (MessageExt msg : messageList) {
            String[] tag = msg.getTags().split("###");
            String syncType = PluginConfig.SyncType.APPEND;
            if (tag.length > 1) {
                syncType = tag[1];
            }
            if (syncType.equals(PluginConfig.SyncType.APPEND)) {
                break;
            }
            appendTypeIdx++;
        }
        return Arrays.asList(
                appendTypeIdx == 0 ? Collections.emptyList() : messageList.subList(0, appendTypeIdx),
                appendTypeIdx == messageList.size() ? Collections.emptyList() : messageList.subList(appendTypeIdx, messageList.size())
        );
    }
}
