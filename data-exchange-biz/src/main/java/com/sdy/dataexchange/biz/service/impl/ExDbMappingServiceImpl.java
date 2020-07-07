package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataexchange.biz.mapper.ExDbMappingMapper;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.model.BO.DbMappingBO;
import com.sdy.dataexchange.biz.service.ExDbDictService;
import com.sdy.dataexchange.biz.service.ExDbMappingService;
import com.sdy.dataexchange.biz.service.ExJobTaskService;
import com.sdy.dataexchange.biz.service.JobService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
@Slf4j
@Service
public class ExDbMappingServiceImpl extends BaseServiceImpl<ExDbMapping> implements ExDbMappingService {
    @Autowired
    private ExDbMappingMapper exDbMappingMapper;
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    ExDbMappingService exDbMappingService;
    @Autowired
    JobService jobService;
    @Autowired
    ExDbDictService exDbDictService;

    /**
     * 删除库映射
     * @param dbMappingIdList
     * @throws Exception
     */
    @Override
    public void removeDbMapping(List<Integer> dbMappingIdList) throws Exception {
        for (Integer dbMappingId : dbMappingIdList) {
            List<ExJobTask> taskList = exJobTaskService.lambdaQuery().eq(ExJobTask::getDbmapId, dbMappingId).list();
            exJobTaskService.removeTask(taskList.stream().map(ExJobTask::getJobtaskId).collect(Collectors.toList()));
        }
        if (!dbMappingIdList.isEmpty()) {
            removeByIds(dbMappingIdList);
        }
    }


    /**
     * 新增库映射
     * @param dbMappingBO
     * @return
     */
    @Override
    public Response addDbMapping(DbMappingBO dbMappingBO) {
        Integer gatherId = Integer.valueOf(dbMappingBO.getGatherDesc());
        Integer sourceDbId = Integer.valueOf(dbMappingBO.getSourceDb());
        Integer destDbId = Integer.valueOf(dbMappingBO.getDestDb());
        if (gatherId != null && !sourceDbId.equals(-1) && !destDbId.equals(-1)) {
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //如果该映射不存在
            LambdaQueryWrapper<ExDbMapping> queryWrapper = new LambdaQueryWrapper<ExDbMapping>();
            queryWrapper.eq(ExDbMapping::getSourceDbid, sourceDbId)
                    .eq(ExDbMapping::getGatherId, gatherId)
                    .eq(ExDbMapping::getDestDbid, destDbId);
            ExDbMapping exDbMapping = exDbMappingService.getOne(queryWrapper);
            if (exDbMapping == null) {
                //新增库映射esc
                boolean b = exDbMappingMapper.insertDbMapping(gatherId, sourceDbId, destDbId, createTime);
                if (b) {
                    return Response.success("新增库映射成功");
                } else {
                    return Response.error("新增库映射失败");
                }
            } else {
                return Response.error("该库映射关系已存在！");
            }
        } else {
            return Response.error("请填写完整信息");
        }
    }

    /**
     * 编辑库映射
     * @param dbMappingBO
     * @return
     */
    @Override
    public Response updateDbMapping(DbMappingBO dbMappingBO) {
        Integer gatherId = Integer.valueOf(dbMappingBO.getGatherDesc());
        Integer sourceDbId = Integer.valueOf(dbMappingBO.getSourceDb());
        Integer destDbId = Integer.valueOf(dbMappingBO.getDestDb());
        Integer id = dbMappingBO.getId();
        if (gatherId != null && !sourceDbId.equals(-1) && !destDbId.equals(-1) && id != null) {
            String modifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            List<ExJobTask> exJobTask = exJobTaskService.list(new LambdaQueryWrapper<ExJobTask>()
                    .eq(ExJobTask::getDbmapId, id)
            );
            if (exJobTask.isEmpty()) {
                ExDbMapping exDbMapping = exDbMappingService.getOne(new LambdaQueryWrapper<ExDbMapping>()
                        .eq(ExDbMapping::getSourceDbid, sourceDbId)
                        .eq(ExDbMapping::getGatherId, gatherId)
                        .eq(ExDbMapping::getDestDbid, destDbId));
                if (exDbMapping == null) {
                    //新增库映射esc
                    boolean b = exDbMappingMapper.updateDbMapping(id, gatherId, sourceDbId, destDbId, modifyTime);
                    if (b) {
                        return Response.success("编辑成功");
                    } else {
                        return Response.error("编辑失败");
                    }
                } else if (exDbMapping.getId() != null && exDbMapping.getId().equals(id)) {
                    boolean b = exDbMappingMapper.updateDbMapping(id, gatherId, sourceDbId, destDbId, modifyTime);
                    if (b) {
                        return Response.success("编辑成功");
                    } else {
                        return Response.error("编辑失败");
                    }
                } else {
                    return Response.error("该库映射已存在");
                }
            } else {
                return Response.error("该库映射下存在任务，请先编辑任务或删除任务后再尝试");
            }
        } else {
            return Response.error("请填写完整信息");
        }
    }
    @Override
    public Integer getTotle() {
        return exDbMappingMapper.getTotle();
    }

    @Override
    public List<DbMappingResult> getByDbName(String dbName) {
        return exDbMappingMapper.getByDbName(dbName);
    }

    @Override
    public Integer getResult(String dbName) {
        return exDbMappingMapper.getResult(dbName);
    }
    @Override
    public List<DbMappingResult> getMapping(Page page) {
        return exDbMappingMapper.getMapping(page);
    }

    @Override
    public DbMappingResult getInfo(Integer id) {

        return exDbMappingMapper.getInfo(id);
    }

    @Override
    public Integer getGatherId(String gatherDesc) {
        return exDbMappingMapper.getGatherId(gatherDesc);
    }

    @Override
    public ExDbMapping getExDbMapping(Integer gatherId) {
        return exDbMappingMapper.getExDbMapping(gatherId);
    }

    @Override
    public String getDbName(Integer dbid) {
        return exDbMappingMapper.getDbName(dbid);
    }
}
