package com.sdy.dataexchange.core.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 内存清理
 * @author zhouziqiang
 */
@Slf4j
public class GcUtil {
    /**
     * 总数据量达到100M手动触发GC
     */
    private static final int MEM_GC_BYTE_SIZE = 1048576 * 100;
    private static int memByteCount = 0;
    
    public static synchronized void byteAddUp(int byteSize) {
        memByteCount += byteSize;
        if (memByteCount > MEM_GC_BYTE_SIZE) {
            log.info("开始GC，当前数据总量：{}", memByteCount);
            System.gc();
            memByteCount = 0;
        }
    }
}
