package com.sdy.dataexchange.core.util;

import javafx.util.Pair;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 任务速率统计工具
 * @author zhouziqiang 
 */
public class SyncRateUtil {

    /**
     * 统计队列大小
     */
    private static final Integer MAX_QUEUE_SIZE = 10;
    /**
     * 统计时间范围
     */
    private static final Long MAX_TIME_RANGE = 10000L;
    private static final Map<String, Queue<Pair<Long, Long>>> RATE_MAP = new ConcurrentHashMap<>();

    /**
     * 任务速率
     * @param jobId 任务id
     * @param type 类型 1-发送 2-处理
     * @return 速率/秒
     */
    public static long immeRate(String jobId, Integer type) {
        Queue<Pair<Long, Long>> q = RATE_MAP.get(jobId.concat("#").concat(type.toString()));
        if (q == null || q.size() <= 1) {
            return 0;
        }
        final long[] mint = {Long.MAX_VALUE};
        final long[] maxt = {0L};
        final long[] sum = {0L};
        q.forEach(item -> {
            if (item.getKey() < mint[0]) {
                mint[0] = item.getKey();
            }
            if (item.getKey() > maxt[0]) {
                maxt[0] = item.getKey();
            }
            sum[0] = sum[0] + item.getValue();
        });
        long second = (maxt[0] - mint[0]) / 1000L;
        if (second == 0) {
            return 0;
        }
        return sum[0] / second;
    }

    /**
     * 添加任务速率统计
     * @param jobId 任务id
     * @param timestamp 时间戳
     * @param dataSize 数据量
     * @param type 类型 1-发送 2-处理
     */
    public static void addImmeStat(String jobId, long timestamp, long dataSize, Integer type) {
        Queue<Pair<Long, Long>> q = RATE_MAP.computeIfAbsent(jobId.concat("#").concat(type.toString()),
                k -> new LinkedBlockingQueue<>());
        q.offer(new Pair<>(timestamp, dataSize));
        if (q.size() > MAX_QUEUE_SIZE) {
            q.poll();
        }
        Pair<Long, Long> h = q.peek();
        while (h != null && System.currentTimeMillis() - h.getKey() > MAX_TIME_RANGE) {
            q.poll();
            h = q.peek();
        }
    }
}
