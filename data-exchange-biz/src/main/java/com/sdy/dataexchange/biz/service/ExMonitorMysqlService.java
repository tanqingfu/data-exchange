package com.sdy.dataexchange.biz.service;

import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.mvc.service.BaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyy
 * @since 2019-10-10
 */
public interface ExMonitorMysqlService extends BaseService<ExMonitorMysql> {
    /**
     * 删除过期增量日志
     * @param ts
     */
    void deleteExpiredAppendLog(Long ts);
    
    int countDataSize(Integer tableId, Long startId, Integer count);
}
