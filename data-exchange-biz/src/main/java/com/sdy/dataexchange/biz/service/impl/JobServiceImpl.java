package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.BizException;
import com.sdy.common.utils.Assert;
import com.sdy.common.utils.DateUtil;
import com.sdy.common.utils.MapUtil;
import com.sdy.common.utils.StringUtil;
import com.sdy.dataexchange.biz.constants.RedisConstants;
import com.sdy.dataexchange.biz.constants.SyncLogConstants;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.core.CanalProperty;
import com.sdy.dataexchange.biz.core.CanalTool;
import com.sdy.dataexchange.biz.model.vo.JobProcessVO;
import com.sdy.dataexchange.biz.redis.CacheNames;
import com.sdy.dataexchange.biz.service.*;
import com.sdy.dataexchange.core.util.CacheUtil;
import com.sdy.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;
/**
 * <p>
 * 作业控制
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
@Slf4j
@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private ExJobInfoService exJobInfoService;
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    private ExSwapdataDictService exSwapdataDictService;
    @Autowired
    private ExDbMappingService exDbMappingService;
    @Autowired
    private ExTableMappingService exTableMappingService;
    @Autowired
    private ExTableDictService exTableDictService;
    @Autowired
    private ExGatherDictService exGatherDictService;
    @Autowired
    private ExDbDictService exDbDictService;
    @Autowired
    private ExSyncmonUserinfoService exSyncmonUserinfoService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private ExSyncLogService exSyncLogService;
    @Autowired
    private RedisLockRegistry redisLockRegistry;
    @Autowired
    private MonitorResultService monitorResultService;
    private static final String SYNC_CANAL_CONFIG_LOCK_KEY = "sync_canal_config_lock_key";

    @Override
    public List<ExJobInfo> listJobs(Integer state, String validFlag) {
        return listJobs(state, validFlag, null);
    }

    @Override
    public List<ExJobInfo> listJobs(Integer state, String validFlag, String ip) {
        List<ExJobInfo> jobList = exJobInfoService.lambdaQuery()
                .eq(state != null, ExJobInfo::getJobState, state)
                .eq(ip != null, ExJobInfo::getIp, ip)
                .list();
        if (jobList.isEmpty()) {
            return Collections.emptyList();
        }
        List<ExJobTask> taskList = exJobTaskService.lambdaQuery()
                .in(ExJobTask::getJobId, jobList.stream().map(ExJobInfo::getJobId).collect(Collectors.toList()))
                .eq(StringUtil.isNotBlank(validFlag), ExJobTask::getValidFlag, validFlag)
                .list();
        Map<Integer, List<ExJobTask>> taskMap = MapUtil.collectionToMapList(taskList, ExJobTask::getJobId);
        jobList.forEach(job -> {
            List<ExJobTask> t = taskMap.getOrDefault(job.getJobId(), Collections.emptyList());
            job.setTaskList(t);
        });
        return jobList;
    }

    @Override
    public void setJobFailed(Integer jobInfoId) {
        exJobInfoService.lambdaUpdate()
                .eq(ExJobInfo::getJobId, jobInfoId)
                .set(ExJobInfo::getJobState, 4)
                .update();
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void setJobFailedByTaskId(Integer taskId) {
        ExJobTask task = exJobTaskService.getById(taskId);
        if (task != null) {
            exJobInfoService.lambdaUpdate()
                    .eq(ExJobInfo::getJobId, task.getJobId())
                    .set(ExJobInfo::getJobState, 4)
                    .update();
        }
    }

    @Override
    public ExJobInfo getJobInfo(Integer jobInfoId) {
        return exJobInfoService.getById(jobInfoId);
    }

    @Override
    public ExJobInfo getJobInfoByTaskId(Integer taskId) {
        ExJobTask task = exJobTaskService.getById(taskId);
        if (task != null) {
            return exJobInfoService.getById(task.getJobId());
        }
        return null;
    }

    @Override
    public void setJobComplete(Integer jobInfoId) {
        exJobInfoService.lambdaUpdate()
                .eq(ExJobInfo::getJobId, jobInfoId)
                .set(ExJobInfo::getJobState, 3)
                .update();
    }

    @Override
    public void pauseJob(Integer jobInfoId) {
        exJobInfoService.lambdaUpdate()
                .eq(ExJobInfo::getJobId, jobInfoId)
                .eq(ExJobInfo::getJobState, 2)
                .set(ExJobInfo::getJobState, 5)
                .update();
    }

    @Override
    public void resumeJob(Integer jobInfoId) {
        exJobInfoService.lambdaUpdate()
                .eq(ExJobInfo::getJobId, jobInfoId)
                .eq(ExJobInfo::getJobState, 5)
                .set(ExJobInfo::getJobState, 2)
                .update();
    }

    @Override
    public void saveJobStatistics(Integer jobInfoId, Integer taskId, String hourStr, Integer count) throws Exception {
        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASTATISTIC);
        ExSwapdataDict swapData = formatExSwapdataDict(jobInfoId, taskId, hourStr, count);
        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASTATISTIC);
        if (swapData != null) {
            exSwapdataDictService.save(swapData);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ExSwapdataDict> listByTaskId(Integer jobInfoId, Integer taskId, String hourStr) {
        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASTATISTIC);
        // 数据库中的统计数据
        List<ExSwapdataDict> dbStatList = exSwapdataDictService.lambdaQuery()
                .eq(jobInfoId != null, ExSwapdataDict::getJobId, jobInfoId)
                .eq(taskId != null, ExSwapdataDict::getTaskId, taskId)
                .eq(StringUtil.isNotBlank(hourStr), ExSwapdataDict::getSwaData, hourStr)
                .orderByAsc(ExSwapdataDict::getJobId, ExSwapdataDict::getTaskId, ExSwapdataDict::getSwaData)
                .list();
        // redis中的统计数据
        List<ExSwapdataDict> redisStatList = listRedisJobInfo(jobInfoId, taskId, hourStr);
        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASTATISTIC);
        return mergeJobStat(dbStatList, redisStatList);
    }

    @Override
    public List<ExSwapdataDict> listByTimeRange(String hourStrStart, String hourStrEnd, Integer jobId) {
        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASTATISTIC);
        // 数据库中的统计数据
        List<ExSwapdataDict> dbStatList = exSwapdataDictService.lambdaQuery()
                .ge(StringUtil.isNotBlank(hourStrStart), ExSwapdataDict::getSwaData, hourStrStart)
                .le(StringUtil.isNotBlank(hourStrEnd), ExSwapdataDict::getSwaData, hourStrEnd)
                .orderByAsc(ExSwapdataDict::getJobId, ExSwapdataDict::getTaskId, ExSwapdataDict::getSwaData)
                .eq(jobId != null, ExSwapdataDict::getJobId, jobId)
                .list();
        // redis中的统计数据
        List<ExSwapdataDict> redisStatList = listRedisJobInfo(jobId, null, null);
        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASTATISTIC);
        List<ExSwapdataDict> r = mergeJobStat(dbStatList, redisStatList);
        r = r.stream()
                .filter(item -> StringUtil.isBlank(hourStrStart) || item.getSwaData().compareTo(hourStrStart) >= 0)
                .filter(item -> StringUtil.isBlank(hourStrEnd) || item.getSwaData().compareTo(hourStrEnd) <= 0)
                .collect(Collectors.toList());
        return r;
    }

    @Override
    public List<JobProcessVO> listStatByJobIds(List<Integer> jobIdList) {
        if (jobIdList.isEmpty()) {
            return Collections.emptyList();
        }
        // 数据库中的统计数据
        List<ExSwapdataDict> dbStatList = exSwapdataDictService.queryGroupByJobIds(jobIdList);
        // redis中的统计数据
//        List<ExSwapdataDict> redisStatList = listRedisJobInfo(jobId, null, null);
        CacheUtil.clearCache(CacheNames.CACHE_BUCKET_DATASTATISTIC);
        return null;
    }

    @Override
    public IPage<ExSwapdataDict> pageByTaskId(Integer jobInfoId, Integer taskId, String hourStr, Page<ExSwapdataDict> page) {
        List<ExSwapdataDict> l = listByTaskId(jobInfoId, taskId, hourStr);
        return new Page<ExSwapdataDict>(page.getCurrent(), page.getSize(), l.size())
                .setRecords(l.stream()
                        .limit(page.getCurrent() * page.getSize())
                        .skip((page.getCurrent() - 1) * page.getSize())
                        .collect(Collectors.toList()));
    }

    @Override
    public void convertToAppendeMode(Integer jobTaskId) {
        ExJobTask jobTask = exJobTaskService.getById(jobTaskId);
        exJobTaskService.lambdaUpdate()
                .eq(ExJobTask::getJobtaskId, jobTaskId)
                .set(ExJobTask::getValidFlag, "1")
                .update();
        if (exJobTaskService.lambdaQuery().eq(ExJobTask::getJobId, jobTask.getJobId()).eq(ExJobTask::getValidFlag, "2").count() <= 0) {
            exJobInfoService.lambdaUpdate().eq(ExJobInfo::getJobId, jobTask.getJobId()).set(ExJobInfo::getValidFlag, "1").update();
        }
    }

    @Override
    public void syncCanalDbConfig(ExDbDict exDbDict, Integer type) throws Exception {
        CanalProperty property = formatCanalProperties(exDbDict);
        if (type.equals(1)) {
            CanalTool.updateDatabase(property, exDbDict.getDbId(), true);
        } else if (type.equals(2)) {
            CanalTool.updateDatabase(property, exDbDict.getDbId(), false);
        }
    }

    @Override
    public void syncCanalTbConfig(ExTableDict exTableDict, Integer type) throws Exception {
        ExDbDict exDbDict = exDbDictService.getById(exTableDict.getDbId());
        CanalProperty property = formatCanalProperties(exDbDict);
        if (type.equals(1)) {
            CanalTool.updateTable(property, exTableDict.getDbTable(), true);
        } else if (type.equals(2)) {
            CanalTool.updateTable(property, exTableDict.getDbTable(), false);
        }
    }

    @Override
    public List<ExDbDict> listMappedSrcDb(Integer exTaskId) {
        return exJobTaskService.listMappedSrcDb(exTaskId);
    }

    @Override
    public List<ExTableDict> listMappedSrcTb(Integer exTaskId) {
        return exJobTaskService.listMappedSrcTb(exTaskId);
    }

    @Override
    public void saveLog(Integer taskId, Integer logType, String msg) {
        try {
            ExJobTask task = getJobTask(taskId);
            Integer jobId = null;
            if (task != null) {
                jobId = task.getJobId();
                msg = msg.replace(SyncLogConstants.LogPlaceHolder.JOB_TASK, task.getJobtaskName());
                ExJobInfo jobInfo = getJobInfo(jobId);
                if (jobInfo != null) {
                    msg = msg.replace(SyncLogConstants.LogPlaceHolder.JOB_INFO, jobInfo.getJobName());
                }
            }
            exSyncLogService.saveLog(jobId, taskId, logType, msg.substring(0, Math.min(2000, msg.length())));
        } catch (Exception e) {
            log.error("日志写入失败！", e);
        }
    }

    @Override
    public ExJobTask getJobTask(Integer jobTaskId) {
        return exJobTaskService.getById(jobTaskId);
    }

    @Override
    public void updateStartTimestamp(Long ts, Integer taskId) {
        String tsStr = redisService.getOrDefault(RedisConstants.REDIS_CONSUME_FAILED_TS_MAP + taskId, String.class, "0");
        if (ts > Long.valueOf(tsStr)) {
            redisService.set(RedisConstants.REDIS_CONSUME_FAILED_TS_MAP + taskId, ts.toString());
            CacheUtil.removeCache(CacheNames.CACHE_BUCKET_CONSUME_TS, CacheNames.consumeFailedTsMap + taskId);
            log.info("Set timestamp threshold: {}", DateUtil.formatTime(new Date(ts)) + " # " + ts);
        }
    }

    @Override
    public void addCanalConfig(Integer taskId) throws Exception {
        Lock lock = redisLockRegistry.obtain(SYNC_CANAL_CONFIG_LOCK_KEY);
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                // 更新canal配置
                ExDbDict dbDict = monitorResultService.queryExDbDictSrc(taskId);
                ExTableDict tbDict = monitorResultService.queryExTableDictSrc(taskId);
                syncCanalDbConfig(dbDict, 1);
                syncCanalTbConfig(tbDict, 1);
            } finally {
                lock.unlock();
            }
        } else {
            throw new RuntimeException("获取Canal分布式锁失败，请稍后重试");
        }
    }

    @Override
    public void removeCanalConfig(Integer taskId) throws InterruptedException {
        Lock lock = redisLockRegistry.obtain(SYNC_CANAL_CONFIG_LOCK_KEY);
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                // 移除canal配置
                List<ExTableDict> mappedTbList = listMappedSrcTb(taskId);
                if (mappedTbList.isEmpty()) {
                    ExTableDict tbDict = monitorResultService.queryExTableDictSrc(taskId);
                    syncCanalTbConfig(tbDict, 2);
                }
                List<ExDbDict> mappedDbList = listMappedSrcDb(taskId);
                if (mappedDbList.isEmpty()) {
                    ExDbDict dbDict = monitorResultService.queryExDbDictSrc(taskId);
                    syncCanalDbConfig(dbDict, 2);
                }
            } catch (Exception e) {
                log.error("移除canal配置失败", e);
            } finally {
                lock.unlock();
            }
        } else {
            throw new RuntimeException("获取Canal分布式锁失败，请稍后重试");
        }
    }

    private CanalProperty formatCanalProperties(ExDbDict exDbDict) {
        ExSyncmonUserinfo syncUser = exSyncmonUserinfoService.getById(exDbDict.getSyncdbId());
        ExGatherDict gather = exGatherDictService.getById(syncUser.getGatherId());
        return new CanalProperty(
                gather.getGatherId(),
                gather.getServiceIp(),
                gather.getSshUser(),
                gather.getSshPassword(),
                gather.getSshPort(),
                gather.getGatherPath(),
                CanalTool.CANAL_CONFIG_PREFIX.concat(exDbDict.getDbId().toString()),
                exDbDict.getDbIp().concat(":").concat(exDbDict.getDbPort().toString()),
                syncUser.getSyncdbUser(),
                syncUser.getSyncdbPasswd()
        );
    }

    private List<ExSwapdataDict> listRedisJobInfoList(List<Integer> jobIdList) {
        if (jobIdList.isEmpty()) {
            return Collections.emptyList();
        }
        String jobStatisticFieldSepToken = "##";
        List<ExSwapdataDict> redisStatList = new ArrayList<>();
        String prefix = RedisConstants.REDIS_SYNC_STATISTIC;
        Set<String> keySet = new HashSet<>();
        jobIdList.forEach(jobId -> {
            Set<String> ks = redisService.keys(RedisConstants.REDIS_SYNC_STATISTIC.concat("*")
                    .concat(jobStatisticFieldSepToken).concat(jobId.toString())
                    .concat(jobStatisticFieldSepToken).concat("*"));
            keySet.addAll(ks);
        });
        keySet.forEach(k -> {
            try {
                String[] strs = k.substring(prefix.length()).split(jobStatisticFieldSepToken);
                String hstr = strs[0];
                String groupId = strs[1];
                String jobId = strs[2];
                Integer cnt = redisService.get(k, Integer.class);
                if (cnt != null) {
                    ExSwapdataDict exSwapData = formatExSwapdataDict(Integer.valueOf(groupId), Integer.valueOf(jobId), hstr, cnt);
                    if (exSwapData != null) {
                        redisStatList.add(exSwapData);
                    }
                }
            } catch (Exception e) {
                log.error("Job 统计失败，redis key: [{}]", k, e);
            }
        });
        // 
        return redisStatList;
    }

    private List<ExSwapdataDict> listRedisJobInfo(Integer jobInfoId, Integer taskId, String hourStr) {
        String jobStatisticFieldSepToken = "##";
        List<ExSwapdataDict> redisStatList = new ArrayList<>();
        String prefix = RedisConstants.REDIS_SYNC_STATISTIC;
        Set<String> keySet = redisService.keys(RedisConstants.REDIS_SYNC_STATISTIC.concat(StringUtil.isBlank(hourStr) ? "*" : hourStr)
                .concat(jobStatisticFieldSepToken).concat(jobInfoId == null ? "*" : jobInfoId.toString())
                .concat(jobStatisticFieldSepToken).concat(taskId == null ? "*" : taskId.toString()));
        keySet.forEach(k -> {
            try {
                String[] strs = k.substring(prefix.length()).split(jobStatisticFieldSepToken);
                String hstr = strs[0];
                String groupId = strs[1];
                String jobId = strs[2];
                Integer cnt = redisService.get(k, Integer.class);
                if (cnt != null) {
                    ExSwapdataDict exSwapData = formatExSwapdataDict(Integer.valueOf(groupId), Integer.valueOf(jobId), hstr, cnt);
                    if (exSwapData != null) {
                        redisStatList.add(exSwapData);
                    }
                }
            } catch (Exception e) {
                log.error("Job 统计失败，redis key: [{}]", k, e);
            }
        });
        return redisStatList;
    }

    /**
     * 合并数据库与缓存的统计数据
     *
     * @param l1
     * @param l2
     * @return
     */
    private List<ExSwapdataDict> mergeJobStat(List<ExSwapdataDict> l1, List<ExSwapdataDict> l2) {
        List<ExSwapdataDict> result = new ArrayList<>();
        result.addAll(l1);
        result.addAll(l2);
        result.sort((o1, o2) -> {
            if (o1.getJobId().equals(o2.getJobId())) {
                if (o1.getTaskId().equals(o2.getTaskId())) {
                    if (o1.getSwaData().equals(o2.getSwaData())) {
                        return o1.getId() - o2.getId();
                    } else {
                        return o1.getSwaData().compareTo(o2.getSwaData());
                    }
                } else {
                    return o1.getTaskId() - o2.getTaskId();
                }
            } else {
                return o1.getJobId() - o2.getJobId();
            }
        });
        return result;
    }

    private ExSwapdataDict formatExSwapdataDict(Integer jobInfoId, Integer taskId, String hourStr, Integer count) throws Exception {
        try {
            ExJobTask task = exJobTaskService.getById(taskId);
            Assert.isNull(task, "task[" + taskId + "]不存在");
            ExDbMapping dbMapping = CacheUtil.cacheProcessing(
                    CacheNames.CACHE_BUCKET_DATASTATISTIC,
                    "exDbMappingService.getById" + task.getDbmapId(),
                    () -> exDbMappingService.getById(task.getDbmapId()));
            ExTableMapping tbMapping = CacheUtil.cacheProcessing(
                    CacheNames.CACHE_BUCKET_DATASTATISTIC,
                    "exTableMappingService.getById" + task.getTbmapId(),
                    () -> exTableMappingService.getById(task.getTbmapId()));
            Assert.isNull(dbMapping, "task[" + taskId + "], 库映射[" + task.getDbmapId() + "]不存在");
            Assert.isNull(tbMapping, "task[" + taskId + "], 表映射[" + task.getTbmapId() + "]不存在");
            ExTableDict srcTable = CacheUtil.cacheProcessing(
                    CacheNames.CACHE_BUCKET_DATASTATISTIC,
                    "exTableDictService.getById" + tbMapping.getSourceSyncid(),
                    () -> exTableDictService.getById(tbMapping.getSourceSyncid()));
            ExTableDict destTable = CacheUtil.cacheProcessing(
                    CacheNames.CACHE_BUCKET_DATASTATISTIC,
                    "exTableDictService.getById" + tbMapping.getDestSyncid(),
                    () -> exTableDictService.getById(tbMapping.getDestSyncid()));
            Assert.isNull(srcTable, "task[" + taskId + "], 源表[" + tbMapping.getSourceSyncid() + "]不存在");
            Assert.isNull(destTable, "task[" + taskId + "], 目标表[" + tbMapping.getDestSyncid() + "]不存在");
            ExSwapdataDict swapData = new ExSwapdataDict();
            swapData.setSwaData(hourStr);
            swapData.setSwaGross(count);
            swapData.setJobId(jobInfoId);
            swapData.setTaskId(taskId);
            swapData.setSourcedbId(dbMapping.getSourceDbid());
            swapData.setDestdbId(dbMapping.getDestDbid());
            swapData.setSourceName(srcTable.getDbTable());
            swapData.setDestName(destTable.getDbTable());
            return swapData;
        } catch (BizException e) {
            log.error("formatExSwapdataDict error: {}", e.getMessage());
        }
        return null;
    }
}
