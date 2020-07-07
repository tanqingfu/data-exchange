package com.sdy.dataexchange.core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheUtil {
    private static ConcurrentHashMap<String, Map<String, Object>> cache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T> T cacheProcessing(String bucket, String key, CacheProc<T> proc) {
        Map<String, Object> bizMap = cache.computeIfAbsent(bucket, k -> new ConcurrentHashMap<>(16));
        return (T) bizMap.computeIfAbsent(key, k -> proc.doProc());
    }
    
    @FunctionalInterface
    public interface CacheProc<T> {
        T doProc();
    }

    public static void clearCache(String bucket) {
        cache.remove(bucket);
    }

    public static void removeCache(String bucket, String key) {
        Map<String, Object> bkt = cache.get(bucket);
        if (bkt != null) {
            bkt.remove(key);
        }
    }
    
    public static void removeBucket(String bucket) {
        cache.remove(bucket);
    }
    
    public static void clearAllCache() {
        cache.clear();
    }
}
