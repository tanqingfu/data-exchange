package com.sdy.dataexchange.plugin.mysql;

import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.redis.CacheNames;
import com.sdy.dataexchange.biz.service.ExMonitorMysqlService;
import com.sdy.dataexchange.biz.service.MonitorResultService;
import com.sdy.dataexchange.core.JobConstant;
import com.sdy.dataexchange.plugin.common.Constants;
import com.sdy.dataexchange.plugin.common.MqConsumerOnce;
import com.sdy.dataexchange.plugin.common.MqConsumerSplit;
import com.sdy.dataexchange.plugin.common.SqlParserUtil;
import com.sdy.dataexchange.plugin.common.entity.SqlRedoObj;
import com.sdy.dataexchange.core.util.CacheUtil;
import com.sdy.mq.base.BaseBatchMessageConsumer;
import com.sdy.mq.base.BaseMessageConsumer;
import com.sdy.mvc.utils.JsonUtil;
import com.sdy.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 消费canal产生的数据
 * @author zhouziqiang 
 */
@Slf4j
@Component
public class MysqlOriginConsumer extends BaseBatchMessageConsumer {
    @Autowired
    private ExMonitorMysqlService exMonitorMysqlService;
    @Autowired
    private MonitorResultService monitorResultService;
    @Autowired
    private MqConsumerOnce mqConsumerOnce;
    @Autowired
    private MqConsumerSplit mqConsumerSplit;
    /**
     * 1秒承载量100w数据，超过这个数据的时候更换作业服务器，可能会导致初始化时间小于表中的最新时间
     */
    private static AtomicLong mysqlTbIncreasedLong = new AtomicLong(0L);
    /**
     * MEDIUM BLOB 存储最大字节数 16M
     */
    private static final int MAX_DATA_FIELD_SIZE = (1 << 24) - 1;
    /**
     * 超过此长度，使用BLOB存储，否则使用varchar(21000)
     */
    private static final int MAX_BLOB_THRESHOLD = 21000;

    @Override
    protected boolean consume(List<MessageExt> messageList) {
        try {
            mqConsumerOnce.consume(messageList, msgList0 -> mqConsumerSplit.consume(msgList0, msgList -> {
                // TODO 时间戳增加采集点ID，防止多个采集点造成主键重复
                long ts = System.currentTimeMillis() * Constants.CANAL_TIMESTAMP_UNIT;
                if (mysqlTbIncreasedLong.get() < ts) {
                    mysqlTbIncreasedLong.set(ts);
                }
                List<ExMonitorMysql> result = new ArrayList<>();
                msgList.forEach(msg -> {
                    String[] tags = msg.getTags().split("###");
                    String tableName;
                    String msgBody = new String(msg.getBody(), Charset.forName(RemotingHelper.DEFAULT_CHARSET));
                    if (tags.length >= 2) {
                        tableName = tags[0];
                    } else {
                        SqlRedoObj sqlRedoObj = JsonUtil.fromJson(msgBody, SqlRedoObj.class);
                        tableName = sqlRedoObj.getTable();
                    }
                    String topic = msg.getTopic();
                    Integer dbId = Integer.valueOf(topic.substring(topic.lastIndexOf("_") + 1));
                    ExTableDict exTableDict = CacheUtil.cacheProcessing(CacheNames.CACHE_BUCKET_DATA_MYSQL_ORIGIN,
                            CacheNames.queryExTableDictDest + dbId + "#" + tableName,
                            () -> monitorResultService.queryExTableDict(dbId, tableName));
                    if (exTableDict == null) {
                        return;
                    }
                    if (msg.getBody().length > MAX_BLOB_THRESHOLD) {
                        // BLOB存储
                        List<byte[]> splitList = SqlParserUtil.splitMysqlMonitorByte(msg.getBody(), MAX_DATA_FIELD_SIZE);
                        for (int i = 0; i < splitList.size(); i++) {
                            byte[] byteData = splitList.get(i);
                            ExMonitorMysql exMonitorMysql = new ExMonitorMysql();
                            exMonitorMysql.setId(mysqlTbIncreasedLong.incrementAndGet() + "");
                            exMonitorMysql.setDbId(dbId);
                            exMonitorMysql.setTableId(exTableDict.getSyncId());
                            exMonitorMysql.setByteData(byteData);
                            exMonitorMysql.setNextFlag(i == splitList.size() - 1 ? 0 : 1);
                            result.add(exMonitorMysql);
                        }
                    } else {
                        // VARCHAR 存储
                        List<String> splitList = SqlParserUtil.splitMysqlMonitorStr(msgBody, MAX_BLOB_THRESHOLD);
                        for (int i = 0; i < splitList.size(); i++) {
                            String strData = splitList.get(i);
                            ExMonitorMysql exMonitorMysql = new ExMonitorMysql();
                            exMonitorMysql.setId(mysqlTbIncreasedLong.incrementAndGet() + "");
                            exMonitorMysql.setDbId(dbId);
                            exMonitorMysql.setTableId(exTableDict.getSyncId());
                            exMonitorMysql.setData(strData);
                            exMonitorMysql.setNextFlag(i == splitList.size() - 1 ? 0 : 1);
                            result.add(exMonitorMysql);
                        }
                    }
                });
                exMonitorMysqlService.saveBatch(result);
            }));
        } catch (Exception e) {
            log.error("Mysql落地失败！", e);
            // TODO send sms
        }
        CacheUtil.removeBucket(CacheNames.CACHE_BUCKET_DATA_MYSQL_ORIGIN);
        return true;
    }
}
