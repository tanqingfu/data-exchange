package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.mapper.ExTableMappingMapper;
import com.sdy.dataexchange.biz.model.ExJobTask;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.biz.model.TableMappingResult;
import com.sdy.dataexchange.biz.model.TaskInfoResult;
import com.sdy.dataexchange.biz.service.ExJobTaskService;
import com.sdy.dataexchange.biz.service.ExTableMappingService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
@Slf4j
@Service
public class ExTableMappingServiceImpl extends BaseServiceImpl<ExTableMapping> implements ExTableMappingService {
    @Autowired
    private ExTableMappingMapper exTableMappingMapper;
    @Autowired
    private ExJobTaskService exJobTaskService;

    @Override
    public List<TableMappingResult> getMapping(Page page) {
        return exTableMappingMapper.getMapping(page);
    }

    @Override
    public List<TaskInfoResult> getInfo(Integer id) {
        return exTableMappingMapper.getInfo(id);
    }

    @Override
    public Integer getTableId(String taskName) {
        return exTableMappingMapper.getTableId(taskName);
    }

    @Override
    public TableMappingResult getOneInfo(Integer id) {
        return exTableMappingMapper.getOneInfo(id);
    }

    @Override
    public Integer updateInfo(TableMappingResult tableMappingResult) {
        return exTableMappingMapper.updateInfo(tableMappingResult);
    }

    @Override
    public void removeTask(List<Integer> integers) {
        exTableMappingMapper.removeTask(integers);
    }

    @Override
    public void removeTbMapping(List<Integer> tbMappingIdList) throws Exception {
        for (Integer tbMappingId : tbMappingIdList) {
            List<ExJobTask> taskList = exJobTaskService.lambdaQuery().eq(ExJobTask::getTbmapId, tbMappingId).list();
            exJobTaskService.removeTask(taskList.stream().map(ExJobTask::getJobtaskId).collect(Collectors.toList()));
        }
        if (!tbMappingIdList.isEmpty()) {
            removeByIds(tbMappingIdList);
        }
    }

    @Override
    public Integer getDbId(String db, String ip, Integer port, String user) {
        return exTableMappingMapper.getDbId(db, ip, port, user);
    }

    @Override
    public Integer getTotle(Integer id) {
        return exTableMappingMapper.getTotle(id);
    }

    @Override
    public Integer getAll() {
        return exTableMappingMapper.getAll();
    }

    @Override
    public List<TableMappingResult> getInfos(String key, Page page) {
        return exTableMappingMapper.getInfos(key, page);
    }

}
