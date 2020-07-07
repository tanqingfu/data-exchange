package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExSyncLog;
import com.sdy.dataexchange.biz.mapper.ExSyncLogMapper;
import com.sdy.dataexchange.biz.service.ExSyncLogService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyy
 * @since 2019-10-28
 */
@Slf4j
@Service
public class ExSyncLogServiceImpl extends BaseServiceImpl<ExSyncLog> implements ExSyncLogService {
    @Autowired
    private ExSyncLogMapper exSyncLogMapper;

    @Override
    public void saveLog(Integer jobId, Integer taskId, Integer logType, String msg) {
        msg = msg == null ? "" : msg;
        save(new ExSyncLog().setJobId(jobId).setTaskId(taskId).setType(logType).setMsg(msg.substring(0, Math.min(2000, msg.length()))).setCreateTime(new Date()));
    }
}
