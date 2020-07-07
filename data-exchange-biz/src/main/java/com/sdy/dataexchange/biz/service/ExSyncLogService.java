package com.sdy.dataexchange.biz.service;

import com.sdy.dataexchange.biz.model.ExSyncLog;
import com.sdy.mvc.service.BaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyy
 * @since 2019-10-28
 */
public interface ExSyncLogService extends BaseService<ExSyncLog> {
    /**
     * 记录日志
     * @param jobId 作业id
     * @param taskId 任务id
     * @param logType 日志类型
     * @param msg 日志消息
     */
    void saveLog(Integer jobId, Integer taskId, Integer logType, String msg);
}
