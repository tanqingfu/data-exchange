package com.sdy.dataexchange.core;

import javafx.util.Pair;

/**
 * 任务分片
 */
public interface JobFragment {
    /**
     * 执行任务
     * @return 是否完成
     * @exception Exception 执行失败时抛出异常
     */
    boolean run() throws Exception;

    /**
     * 初始化
     * @throws Exception
     */
    void init() throws Exception;

    /**
     * 销毁
     * @throws Exception
     */
    void destroy(boolean clear) throws Exception;

    /**
     * 是否全量
     * @return
     */
    boolean bFull();

    /**
     * 同步进度
     */
    Pair<Long, Long> getSyncProcess();
}
