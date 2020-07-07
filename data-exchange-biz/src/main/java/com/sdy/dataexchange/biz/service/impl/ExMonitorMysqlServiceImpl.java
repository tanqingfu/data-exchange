package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.dataexchange.biz.mapper.ExMonitorMysqlMapper;
import com.sdy.dataexchange.biz.service.ExMonitorMysqlService;
import com.sdy.mvc.service.BaseService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyy
 * @since 2019-10-10
 */
@Slf4j
@Service
public class ExMonitorMysqlServiceImpl extends BaseServiceImpl<ExMonitorMysql> implements ExMonitorMysqlService {
    @Autowired
    private ExMonitorMysqlMapper exMonitorMysqlMapper;

    @Override
    protected Class<ExMonitorMysql> currentModelClass() {
        return (Class<ExMonitorMysql>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseService.class);
    }

    @Override
    public void deleteExpiredAppendLog(Long ts) {
        exMonitorMysqlMapper.deleteExpiredAppendLog(ts);
    }

    @Override
    public int countDataSize(Integer tableId, Long startId, Integer count) {
        return exMonitorMysqlMapper.countDataSize(tableId, startId, count);
    }
}
