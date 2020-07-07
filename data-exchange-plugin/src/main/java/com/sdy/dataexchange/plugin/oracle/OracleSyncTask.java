package com.sdy.dataexchange.plugin.oracle;

import com.sdy.common.utils.DateUtil;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.model.ExGatherDict;
import com.sdy.dataexchange.biz.model.ExSyncmonUserinfo;
import com.sdy.dataexchange.biz.service.ExGatherDictService;
import com.sdy.dataexchange.biz.service.ExParamService;
import com.sdy.dataexchange.biz.service.ExSyncmonUserinfoService;
import com.sdy.dataexchange.biz.service.MonitorResultService;
import com.sdy.dataexchange.plugin.common.Constants;
import com.sdy.mvc.annotation.TaskSync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OracleSyncTask {
    @Autowired
    private ExGatherDictService exGatherDictService;
    @Autowired
    private ExSyncmonUserinfoService exSyncmonUserinfoService;
    @Autowired
    private ExParamService exParamService;

    /**
     * 删除过期日志
     * 批量删除，防止数据库卡死
     */
    @TaskSync
    @Scheduled(cron = "10 10 2 * * ?")
    public void clearExpiredAppendLog() {
        String exp = exParamService.getParamOrDefault("append_expire_date", Constants.APPEND_EXPIRED_DATE + "");
        Date now = new Date();
        Date expireDate = DateUtil.addDate(now, -Integer.valueOf(exp));
        long ts = expireDate.getTime() * Constants.CANAL_TIMESTAMP_UNIT;
        log.info("删除{}[ts={}]前的日志[ORACLE]", DateUtil.formatTime(expireDate), ts);
        List<ExGatherDict> gatherList = exGatherDictService.lambdaQuery().select(ExGatherDict::getGatherId).list();
        long tout = Long.parseLong(exParamService.getParamOrDefault("oracle_conn_timeout", "2000"));
        if (!gatherList.isEmpty()) {
            List<ExSyncmonUserinfo> syncUserList = exSyncmonUserinfoService.lambdaQuery()
                    .in(ExSyncmonUserinfo::getGatherId, gatherList.stream().map(ExGatherDict::getGatherId).collect(Collectors.toList()))
                    .eq(ExSyncmonUserinfo::getSyncdbType, "ORACLE")
                    .list();
            syncUserList.forEach(syncInfo -> {
                DataAdapter dataAdapter = new DataAdapter(
                        new RawDataSource(
                                DbType.ORACLE,
                                syncInfo.getSyncdbIp(),
                                syncInfo.getSyncdbPort(),
                                syncInfo.getSyncdbName(),
                                syncInfo.getSyncdbUser(),
                                syncInfo.getSyncdbPasswd(),
                                null));
                if (dataAdapter.checkConnection(tout)) {
                    String ds = DateUtil.getDate(expireDate, "yyyy-MM-dd HH:mm:ss.10000001    ");
                    dataAdapter.execute("delete from MONITOR_RESULT where dealtime <= '" + ds + "'");
                } else {
                    log.error("采集点[{}]增量日志数据库连接超时", syncInfo.getGatherId());
                }
            });
        }
    }
}
