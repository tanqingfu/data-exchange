package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.BizException;
import com.sdy.common.model.Response;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.mapper.ExJobTaskMapper;
import com.sdy.dataexchange.biz.mapper.InsertFlagMapper;
import com.sdy.dataexchange.biz.model.BO.TaskBO;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.service.*;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wyy
 * @since 2019-09-08
 */
@Slf4j
@Service
public class ExJobTaskServiceImpl extends BaseServiceImpl<ExJobTask> implements ExJobTaskService {
    @Autowired
    private ExJobTaskMapper exJobTaskMapper;
    @Autowired
    private ExFieldMappingService exFieldMappingService;
    @Autowired
    private ExTableMappingService exTableMappingService;
    @Autowired
    private ExTablePrimarykeyService exTablePrimarykeyService;
    @Autowired
    private ExTableDictService exTableDictService;
    @Autowired
    private ExJobInfoService exJobInfoService;
    @Autowired
    private JobService jobService;
    @Autowired
    private InsertFlagMapper insertFlagMapper;
    @Autowired
    private ExJobTaskService exJobTaskService;
    @Autowired
    private ExDbMappingService exDbMappingService;
    @Autowired
    private ExFieldDictService exFieldDictService;
    @Autowired
    private RepeatDemoService repeatDemoService;
    @Autowired
    private ExDbDictService exDbDictService;

    /**
     * 任务页面select下拉框
     */
    @Override
    public List<ExJobTask> queryAllTask() {
        return exJobTaskMapper.queryAllTask();
    }

    @Override
    public List<TaskInfos> getTask(Page page) {
        return exJobTaskMapper.getTask(page);
    }

    @Override
    public List<TaskInfoDetails> getOneTask(Integer id) {
        return exJobTaskMapper.getOneTask(id);
    }

    @Override
    public List<TaskInfos> getInfo(String key) {
        return exJobTaskMapper.getInfo(key);
    }

    @Override
    public Integer getTotle() {
        return exJobTaskMapper.getTotle();
    }

    @Override
    public List<ExDbDict> listMappedSrcDb(Integer exTaskId) {
        return exJobTaskMapper.listMappedSrcDb(exTaskId);
    }

    @Override
    public List<ExTableDict> listMappedSrcTb(Integer exTaskId) {
        return exJobTaskMapper.listMappedSrcTb(exTaskId);
    }

    @Override
    public void removeTask(List<Integer> taskIdList) throws Exception {
        List<Map<String, Object>> allFields = exJobTaskMapper.getAllFields();
        for (Integer taskId : taskIdList) {
            ExJobTask task = getById(taskId);
            if (task != null) {
                ExJobInfo jobInfo = exJobInfoService.getById(task.getJobId());
                if (jobInfo != null && jobInfo.getJobState().equals(2)) {
                    throw new BizException("作业[" + jobInfo.getJobName() + "]正在进行中，禁止删除。");
                }
                ExTableMapping tbMapping = exTableMappingService.getById(task.getTbmapId());
                if (tbMapping != null) {
                    // 删除字段映射
                    exFieldMappingService.remove(new LambdaQueryWrapper<ExFieldMapping>()
                            .eq(ExFieldMapping::getSourceSyncid, tbMapping.getSourceSyncid())
                            .eq(ExFieldMapping::getDestSyncid, tbMapping.getDestSyncid()));
                    //删除字段
                    if (!allFields.isEmpty()) {
                        for (Map<String, Object> field : allFields) {
                            List<Integer> mappingIdBySource = new ArrayList<>();
                            List<Integer> mappingIdByDest = new ArrayList<>();
                            List<ExFieldMapping> exFieldMappingsBySource = exFieldMappingService.list(new LambdaQueryWrapper<ExFieldMapping>()
                                    .eq(ExFieldMapping::getSourceSyncid, field.get("tableId"))
                                    .eq(ExFieldMapping::getSourceSyncname, field.get("field")));
                            if (exFieldMappingsBySource != null) {
                                mappingIdBySource = exFieldMappingsBySource.stream().map(ExFieldMapping::getId).collect(Collectors.toList());
                            }
                            List<ExFieldMapping> exFieldMappingsByDest = exFieldMappingService.list(new LambdaQueryWrapper<ExFieldMapping>()
                                    .eq(ExFieldMapping::getDestSyncid, field.get("tableId"))
                                    .eq(ExFieldMapping::getDestSyncname, field.get("field")));
                            if (exFieldMappingsByDest != null) {
                                mappingIdByDest = exFieldMappingsByDest.stream().map(ExFieldMapping::getId).collect(Collectors.toList());
                            }
                            if (mappingIdBySource.isEmpty() && mappingIdByDest.isEmpty()) {
                                exJobTaskMapper.deleteField(field.get("tableId"), field.get("field"));
                            }
                        }
                    }

                    // 删除表映射
                    if (task.getTbmapId() != null) {
                        exTableMappingService.removeById(task.getTbmapId());
                    }
                    // 查询源表是否还在使用
                    Integer existsCnt = exTableMappingService.lambdaQuery()
                            .eq(ExTableMapping::getSourceSyncid, tbMapping.getSourceSyncid())
                            .or(wp -> wp.eq(ExTableMapping::getDestSyncid, tbMapping.getSourceSyncid()))
                            .count();
                    if (existsCnt <= 0) {
                        // 删除源表
                        exTableDictService.removeById(tbMapping.getSourceSyncid());
                    }
                    // 查询目标表是否还在使用
                    existsCnt = exTableMappingService.lambdaQuery()
                            .eq(ExTableMapping::getSourceSyncid, tbMapping.getDestSyncid())
                            .or(wp -> wp.eq(ExTableMapping::getDestSyncid, tbMapping.getDestSyncid()))
                            .count();
                    if (existsCnt <= 0) {
                        // 删除目标表
                        exTableDictService.removeById(tbMapping.getDestSyncid());
                    }
                    // 删除主键映射
                    exTablePrimarykeyService.remove(new LambdaQueryWrapper<ExTablePrimarykey>()
                            .eq(ExTablePrimarykey::getTableid, tbMapping.getSourceSyncid()));
                    exTablePrimarykeyService.remove(new LambdaQueryWrapper<ExTablePrimarykey>()
                            .eq(ExTablePrimarykey::getTableid, tbMapping.getDestSyncid()));
                }
            }
        }
        // 删除任务task
        if (!taskIdList.isEmpty()) {
            removeByIds(taskIdList);
        }
    }

    /**
     * 新增任务
     *
     * @param taskBO
     * @return
     */
    @Override
    public Response saveTask(TaskBO taskBO) {
        String taskName = taskBO.getTaskName();
        String sourceDbId = taskBO.getSourceDbId();
        String destDbId = taskBO.getDestDbId();
        String sourceTableName = taskBO.getSourceTableName();
        String destTableName = taskBO.getDestTableName();
        List<Map<String, String>> tableInfos = taskBO.getTableInfos();
        String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Integer fieldResult = null;
        if (StringUtils.isNotBlank(taskName) && StringUtils.isNotBlank(sourceDbId) && StringUtils.isNotBlank(destDbId)
                && StringUtils.isNotBlank(sourceTableName) && StringUtils.isNotBlank(destTableName) && !tableInfos.isEmpty()) {
            List<String> taskNames = new ArrayList<>();
            List<ExJobTask> exJobTask = exJobTaskService.list();
            if (!exJobTask.isEmpty()) {
                taskNames = exJobTask.stream().map(ExJobTask::getJobtaskName).collect(Collectors.toList());
            }
            if (taskNames.contains(taskName)) {
                return Response.error("该交换任务名称已存在，请重新输入");
            } else {
                //获取库映射id，表映射id
                Integer beforeDbMappingId = null;
                Integer beforeSourceSyncId = null;
                Integer beforeDestSyncId = null;
                Integer beforeTbMappingId = null;

                ExDbMapping exDbMapping = exDbMappingService.getOne(new LambdaQueryWrapper<ExDbMapping>()
                        .eq(ExDbMapping::getSourceDbid, sourceDbId)
                        .eq(ExDbMapping::getDestDbid, destDbId));
                if (exDbMapping == null) {
                    return Response.error("该库映射不存在");
                }
                beforeDbMappingId = exDbMapping.getId();
                if (beforeDbMappingId == null) {
                    return Response.error("该库映射不存在");
                }

                ExTableDict beforeSourceExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                        .eq(ExTableDict::getDbId, sourceDbId)
                        .eq(ExTableDict::getDbTable, sourceTableName));
                if (beforeSourceExTableDict != null) {
                    beforeSourceSyncId = beforeSourceExTableDict.getSyncId();
                }

                ExTableDict beforeDestExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                        .eq(ExTableDict::getDbId, destDbId)
                        .eq(ExTableDict::getDbTable, destTableName));
                if (beforeDestExTableDict != null) {
                    beforeDestSyncId = beforeDestExTableDict.getSyncId();
                }
                if (beforeSourceSyncId != null && beforeDestSyncId != null) {
                    ExTableMapping exTableMapping = exTableMappingService.getOne(new LambdaQueryWrapper<ExTableMapping>()
                            .eq(ExTableMapping::getSourceSyncid, beforeSourceSyncId)
                            .eq(ExTableMapping::getDestSyncid, beforeDestSyncId));
                    if (exTableMapping != null) {
                        beforeTbMappingId = exTableMapping.getId();
                    }
                    if (beforeTbMappingId != null && exJobTaskMapper.getTaskId(
                            beforeDbMappingId, beforeTbMappingId) != null) {
                        return Response.error("该任务已存在");
                    }
                }
                if (beforeSourceSyncId == null) {
                    exJobTaskMapper.insertIntoExTableDict(sourceDbId, sourceTableName, createTime);
                }
                if (beforeDestSyncId == null) {
                    exJobTaskMapper.insertIntoExTableDict(destDbId, destTableName, createTime);
                }
                Integer sourceSyncId = null;
                Integer destSyncId = null;
                Integer tbMappingId = null;
                ExTableDict afterSourceExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                        .eq(ExTableDict::getDbId, sourceDbId)
                        .eq(ExTableDict::getDbTable, sourceTableName));
                if (afterSourceExTableDict != null) {
                    sourceSyncId = afterSourceExTableDict.getSyncId();
                }
                ExTableDict afterDestExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                        .eq(ExTableDict::getDbId, destDbId)
                        .eq(ExTableDict::getDbTable, destTableName));
                if (afterDestExTableDict != null) {
                    destSyncId = afterDestExTableDict.getSyncId();
                }
                if (sourceSyncId == null) {
                    return Response.error("无法获取源表id,请联系管理员");
                }
                if (destSyncId == null) {
                    return Response.error("无法获取目标表id，请联系管理员");
                }
                ExTableMapping afterExTableMapping = exTableMappingService.getOne(new LambdaQueryWrapper<ExTableMapping>()
                        .eq(ExTableMapping::getSourceSyncid, sourceSyncId)
                        .eq(ExTableMapping::getDestSyncid, destSyncId));
                if (afterExTableMapping != null) {
                    tbMappingId = afterExTableMapping.getId();
                }
                if (tbMappingId == null) {
                    exJobTaskMapper.insertIntoExTableMapping(taskName, sourceSyncId, destSyncId, createTime);
                } else {
                    return Response.error("该表映射关系已存在！");
                }
                Integer tbMapId = null;
                ExTableMapping afterInsertExTableMapping = exTableMappingService.getOne(new LambdaQueryWrapper<ExTableMapping>()
                        .eq(ExTableMapping::getSourceSyncid, sourceSyncId)
                        .eq(ExTableMapping::getDestSyncid, destSyncId));
                if (afterInsertExTableMapping != null) {
                    tbMapId = afterInsertExTableMapping.getId();
                }
                if (tbMapId == null) {
                    return Response.error("插入表映射失败");
                }
                //加入任务表
                exJobTaskMapper.addJobTask(taskName, beforeDbMappingId, tbMapId, sourceSyncId, "1");
                List<ExFieldDict> leftFields = new ArrayList<>();
                List<ExFieldDict> rightFields = new ArrayList<>();
                List<ExFieldMapping> exFieldMappings = new ArrayList<>();
                List<String> sourceSyncField = new ArrayList<>();
                List<String> destSyncField = new ArrayList<>();
                List<ExFieldDict> sourceExFieldDictList = exFieldDictService.list(new LambdaQueryWrapper<ExFieldDict>()
                        .eq(ExFieldDict::getSyncId, sourceSyncId));
                if (!sourceExFieldDictList.isEmpty()) {
                    sourceSyncField = sourceExFieldDictList.stream().map(ExFieldDict::getSyncField).collect(Collectors.toList());
                }
                List<ExFieldDict> destExFieldDictList = exFieldDictService.list(new LambdaQueryWrapper<ExFieldDict>()
                        .eq(ExFieldDict::getSyncId, destSyncId));
                if (!destExFieldDictList.isEmpty()) {
                    destSyncField = destExFieldDictList.stream().map(ExFieldDict::getSyncField).collect(Collectors.toList());
                }

                fieldResult = repeatDemoService.saveTasks(tableInfos, fieldResult, sourceSyncId, destSyncId, leftFields
                        , rightFields, exFieldMappings, sourceSyncField, destSyncField);
                //ex_db_mapping中插入有效标志
                Integer gatherId = exDbMapping.getGatherId();
                if (gatherId == null) {
                    return Response.error("无法获取交换节点信息，请联系管理员");
                }
                Integer dbResult = insertFlagMapper.updateExDbMapping("1", gatherId, Integer.valueOf(sourceDbId), Integer.valueOf(destDbId));
                Integer tableResult = null;
                //任务配置完成后将表映射更改为有效
                tableResult = insertFlagMapper.updateExTableMapping("1", tbMapId);
                if (dbResult == null || tableResult == null || fieldResult == null) {
                    return Response.error("新增任务失败！");
                } else {
                    return Response.success("新增任务成功！");
                }
            }
        } else {
            return Response.error("请填写完整信息！");
        }
    }


    @Override
    public Response editTask(TaskBO taskBO) {
        String taskName = taskBO.getTaskName();
        String sourceDbId = taskBO.getSourceDbId();
        String destDbId = taskBO.getDestDbId();
        String sourceTableName = taskBO.getSourceTableName();
        String destTableName = taskBO.getDestTableName();
        List<Map<String, String>> tableInfos = taskBO.getTableInfos();
        Integer fieldResult = null;
        if (StringUtils.isNotBlank(taskName) && StringUtils.isNotBlank(sourceDbId) && StringUtils.isNotBlank(destDbId)
                && StringUtils.isNotBlank(sourceTableName) && StringUtils.isNotBlank(destTableName) && !tableInfos.isEmpty()) {
            ExDbMapping exDbMapping = exDbMappingService.getOne(new LambdaQueryWrapper<ExDbMapping>()
                    .eq(ExDbMapping::getSourceDbid, Integer.parseInt(sourceDbId))
                    .eq(ExDbMapping::getDestDbid, Integer.parseInt(destDbId)));
            if (exDbMapping == null) {
                return Response.error("无法获取库映射信息，请联系管理员");
            }
            Integer beforeDbMappingId = exDbMapping.getId();
            if (beforeDbMappingId == null) {
                return Response.error("无法获取库映射id，请联系管理员");
            }
            ExTableDict sourceExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                    .eq(ExTableDict::getDbId, sourceDbId)
                    .eq(ExTableDict::getDbTable, sourceTableName));
            if (sourceExTableDict == null) {
                return Response.error("无法获取源表信息，请联系管理员");
            }
            Integer beforeSourceSyncId = sourceExTableDict.getSyncId();
            if (beforeSourceSyncId == null) {
                return Response.error("无法获取源表id，请联系管理员");
            }
            ExTableDict destExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                    .eq(ExTableDict::getDbId, destDbId)
                    .eq(ExTableDict::getDbTable, destTableName));
            if (destExTableDict == null) {
                return Response.error("无法获取目标表信息，请联系管理员");
            }
            Integer beforeDestSyncId = destExTableDict.getSyncId();
            if (beforeDestSyncId == null) {
                return Response.error("无法获取目标表id，请联系管理员");
            }
            ExTableMapping exTableMapping = exTableMappingService.getOne(new LambdaQueryWrapper<ExTableMapping>()
                    .eq(ExTableMapping::getSourceSyncid, beforeSourceSyncId)
                    .eq(ExTableMapping::getDestSyncid, beforeDestSyncId));
            if (exTableMapping == null) {
                return Response.error("无法获取表映射信息，请联系管理员");
            }
            Integer beforeTbMappingId = exTableMapping.getId();
            if (beforeTbMappingId == null) {
                return Response.error("无法获取表映射id，请联系管理员");
            }

            ExJobTask exJobTask = exJobTaskService.getOne(new LambdaQueryWrapper<ExJobTask>()
                    .eq(ExJobTask::getDbmapId, beforeDbMappingId)
                    .eq(ExJobTask::getTbmapId, beforeTbMappingId));
            if (exJobTask == null) {
                return Response.error("无法获取任务信息，请联系管理员");
            }
            if (exJobTask.getJobId() == null) {
                exJobTaskMapper.updateTaskName(taskName, beforeDbMappingId, beforeTbMappingId);
            } else {
                Integer jobState = exJobTaskMapper.getJobState(beforeDbMappingId, beforeTbMappingId);
                if (jobState != null) {
                    if (jobState == 2 || jobState == 5) {
                        return Response.error("该任务所在作业正在运行中，禁止编辑该任务，请等待作业完成或停止时再次尝试");
                    } else {
                        exJobTaskMapper.updateTaskName(taskName, beforeDbMappingId, beforeTbMappingId);
                    }
                } else {
                    return Response.error("无法获取作业状态，请联系管理员");
                }
            }

            //存放获取的字段
            List<Map<String, Object>> getLeftFields = new ArrayList<>();
            List<Map<String, Object>> getAllRightFields = new ArrayList<>();
            for (Map<String, String> tableInfo1 : tableInfos) {
                Map<String, Object> leftFieldMap = new HashMap(16);
                Map<String, Object> rightFieldMap = new HashMap(16);
                String leftFieldName = tableInfo1.get("field");
                String leftFieldType = tableInfo1.get("fieldType");
                String rightFieldName = tableInfo1.get("field1");
                String rightFieldType = tableInfo1.get("field1Type");
                if (StringUtils.isNotBlank(leftFieldName) && StringUtils.isNotBlank(leftFieldType)) {
                    leftFieldMap.put("field", leftFieldName);
                    leftFieldMap.put("fieldType", leftFieldType);
                    getLeftFields.add(leftFieldMap);
                }
                rightFieldMap.put("field", rightFieldName);
                rightFieldMap.put("fieldType", rightFieldType);
                getAllRightFields.add(rightFieldMap);
            }

            List<ExFieldDict> leftFields = new ArrayList<>();
            List<ExFieldDict> rightFields = new ArrayList<>();
            List<ExFieldMapping> exFieldMappings = new ArrayList<>();
            List<String> sourceSyncField = new ArrayList<>();
            List<String> destSyncField = new ArrayList<>();
            //同步过的字段
            List<ExFieldDict> sourceExFieldDict = exFieldDictService.list(new LambdaQueryWrapper<ExFieldDict>()
                    .eq(ExFieldDict::getSyncId, beforeSourceSyncId));
            if (!sourceExFieldDict.isEmpty()) {
                sourceSyncField = sourceExFieldDict.stream().map(ExFieldDict::getSyncField).collect(Collectors.toList());
            }
            List<ExFieldDict> destExFieldDict = exFieldDictService.list(new LambdaQueryWrapper<ExFieldDict>()
                    .eq(ExFieldDict::getSyncId, beforeDestSyncId));
            if (!destExFieldDict.isEmpty()) {
                destSyncField = destExFieldDict.stream().map(ExFieldDict::getSyncField).collect(Collectors.toList());
            }

            //删除字段映射，无用字段，添加新增字段
            if (!getAllRightFields.isEmpty() && !getLeftFields.isEmpty()) {
                for (int i = 0; i < getLeftFields.size(); i++) {
                    String leftField = String.valueOf(getLeftFields.get(i).get("field"));
                    String leftFieldType = String.valueOf(getLeftFields.get(i).get("fieldType"));
                    for (int j = 0; j < getAllRightFields.size(); j++) {
                        String rightField = String.valueOf(getAllRightFields.get(j).get("field"));
                        String rightFieldType = String.valueOf(getAllRightFields.get(j).get("fieldType"));
                        //如果没有对应关系
                        if (i == j && StringUtils.isBlank(rightField)) {
                            ExFieldMapping exFieldMapping = exFieldMappingService.getOne(new LambdaQueryWrapper<ExFieldMapping>()
                                    .eq(ExFieldMapping::getSourceSyncid, beforeSourceSyncId)
                                    .eq(ExFieldMapping::getSourceSyncname, leftField)
                                    .eq(ExFieldMapping::getDestSyncid, beforeDestSyncId));
                            //如果编辑之前有对应关系，判断源字段和目标字段是否还有其他映射关系，没有就从ex_field_dict删除，删除原有映射关系
                            if (exFieldMapping != null) {
                                String beforeRightField = exFieldMapping.getDestSyncname();
                                if (StringUtils.isNotBlank(beforeRightField)) {
                                    Integer sourceResultBySource = exJobTaskMapper.getFieldMappingBySourceField(beforeSourceSyncId, leftField);
                                    Integer destResultBySource = exJobTaskMapper.getFieldMappingIdByDestField(beforeSourceSyncId, leftField);
                                    if (sourceResultBySource + destResultBySource == 1) {
                                        exJobTaskMapper.deleteFromExFieldDict(beforeSourceSyncId, leftField);
                                    }
                                    Integer sourceResultByDest = exJobTaskMapper.getFieldMappingBySourceField(beforeDestSyncId, beforeRightField);
                                    Integer destResultByDest = exJobTaskMapper.getFieldMappingIdByDestField(beforeDestSyncId, beforeRightField);
                                    if (sourceResultByDest + destResultByDest == 1) {
                                        exJobTaskMapper.deleteFromExFieldDict(beforeDestSyncId, rightField);
                                    }
                                    exJobTaskMapper.deleteFromExFieldMapping(beforeSourceSyncId, leftField, beforeDestSyncId, beforeRightField);
                                }
                            }
                            //如果获取的源字段和目标字段有映射关系
                        } else if (i == j && StringUtils.isNotBlank(rightField)) {
                            ExFieldMapping exFieldMapping = exFieldMappingService.getOne(new LambdaQueryWrapper<ExFieldMapping>()
                                    .eq(ExFieldMapping::getSourceSyncid, beforeSourceSyncId)
                                    .eq(ExFieldMapping::getSourceSyncname, leftField)
                                    .eq(ExFieldMapping::getDestSyncid, beforeDestSyncId));
                            //如果原先该源字段没有映射关系
                            if (exFieldMapping == null) {
                                boolean sourceFieldResult = true;
                                boolean destFieldResult = true;
                                //判断源字段和目标字段是否存在ex_field_dict中，没有就新增
                                ExFieldDict getSourceExFieldDict = exFieldDictService.getOne(new LambdaQueryWrapper<ExFieldDict>()
                                        .eq(ExFieldDict::getSyncId, beforeSourceSyncId)
                                        .eq(ExFieldDict::getSyncField, leftField));
                                if (getSourceExFieldDict == null) {
                                    sourceFieldResult = exJobTaskMapper.insertIntoFieldDict(beforeSourceSyncId, leftField, leftFieldType);
                                }
                                ExFieldDict getDestExFieldDict = exFieldDictService.getOne(new LambdaQueryWrapper<ExFieldDict>()
                                        .eq(ExFieldDict::getSyncId, beforeDestSyncId)
                                        .eq(ExFieldDict::getSyncField, rightField));
                                if (getDestExFieldDict == null) {
                                    destFieldResult = exJobTaskMapper.insertIntoFieldDict(beforeDestSyncId, rightField, rightFieldType);
                                }
                                //ex_field_dict中存在源字段和目标字段后，把映射关系加入到ex_field_mapping
                                if (sourceFieldResult && destFieldResult) {
                                    ExFieldMapping getExFieldMapping = exFieldMappingService.getOne(new LambdaQueryWrapper<ExFieldMapping>()
                                            .eq(ExFieldMapping::getSourceSyncid, beforeSourceSyncId)
                                            .eq(ExFieldMapping::getSourceSyncname, leftField)
                                            .eq(ExFieldMapping::getDestSyncid, beforeDestSyncId)
                                    .eq(ExFieldMapping::getDestSyncname,rightField));
                                    if (getExFieldMapping==null) {
                                        exJobTaskMapper.insertIntoFieldMaping(beforeSourceSyncId, leftField, beforeDestSyncId, rightField);
                                    }
                                } else {
                                    return Response.error("编辑任务失败");
                                }
                                //如果该源字段原先有同步字段
                            } else {
                                String beforeRightField = exFieldMapping.getDestSyncname();
                                Integer fieldMappingId = exFieldMapping.getId();
                                if (fieldMappingId == null) {
                                    return Response.error();
                                }
                                //判断原有的目标字段是否还存在映射关系，没有就从ex_field_dict中删除
                                Integer sourceResultByDest = exJobTaskMapper.getFieldMappingBySourceField(beforeDestSyncId, beforeRightField);
                                Integer destResultByDest = exJobTaskMapper.getFieldMappingIdByDestField(beforeDestSyncId, beforeRightField);
                                if (sourceResultByDest + destResultByDest == 1) {
                                    exJobTaskMapper.deleteFromExFieldDict(beforeDestSyncId, beforeRightField);
                                }
                                ExFieldDict exFieldDict = exFieldDictService.getOne(new LambdaQueryWrapper<ExFieldDict>()
                                        .eq(ExFieldDict::getSyncId, beforeDestSyncId)
                                        .eq(ExFieldDict::getSyncField, rightField));
                                //判断传入的目标字段是否存在于ex_field_dict，不存在则加入，然后将新的映射关系加入ex_field_mapping
                                if (exFieldDict == null) {
                                    exJobTaskMapper.insertIntoFieldDict(beforeDestSyncId, rightField, rightFieldType);
                                    if (!beforeRightField.equals(rightField)) {
                                        exJobTaskMapper.updateFieldMapping(fieldMappingId, rightField);
                                    }
                                } else {
                                    if (!beforeRightField.equals(rightField)) {
                                        exJobTaskMapper.updateFieldMapping(fieldMappingId, rightField);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //获取所有要新增的源字段，目标字段，字段映射的信息
            repeatDemoService.saveTasks(tableInfos, fieldResult, beforeSourceSyncId, beforeDestSyncId, leftFields, rightFields
                    , exFieldMappings, sourceSyncField, destSyncField);
            return Response.success("编辑任务成功！");
        } else {
            return Response.error("请填写完整信息！");
        }
    }


    @Override
    public Response getRowseq(Integer destDbId, String destTableName) {
        if (destDbId != null && destTableName != null) {
//            String regStr = "^[^\\\\&*,.'\";^%+\\-()|<>?/=$#@!~\\s]+$";
            ExDbDict destExDbDict = exDbDictService.getById(destDbId);
            if (destExDbDict != null && StringUtils.isNotBlank(destExDbDict.getDbIp()) && destExDbDict.getDbPort() != null
                    && StringUtils.isNotBlank(destExDbDict.getDbName()) && StringUtils.isNotBlank(destExDbDict.getDbUser())
                    && StringUtils.isNotBlank(destExDbDict.getDbPasswd())) {
                if ("MYSQL".equals(destExDbDict.getDbType())) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, destExDbDict.getDbIp()
                            , destExDbDict.getDbPort(), destExDbDict.getDbName(), destExDbDict.getDbUser()
                            , destExDbDict.getDbPasswd(), null));
                    List<Map<String, Object>> destFields = dataAdapter.queryForList("SELECT * FROM " +
                            "information_schema.columns WHERE table_schema = ? AND table_name = ? " +
                            "ORDER BY column_name ASC;", destExDbDict.getDbName(), destTableName);
                    Response ifDestTableHaveRowseq = destTableRowseq(destFields);
                    if (ifDestTableHaveRowseq != null) {
                        return ifDestTableHaveRowseq;
                    }
                } else if ("ORACLE".equals(destExDbDict.getDbType())) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, destExDbDict.getDbIp()
                            , destExDbDict.getDbPort(), destExDbDict.getDbName(), destExDbDict.getDbUser()
                            , destExDbDict.getDbPasswd(), null));
                    List<Map<String, Object>> destFields = dataAdapter.queryForList("select COLUMN_NAME,DATA_TYPE " +
                            "from user_tab_columns where TABLE_NAME=?", destTableName);
                    Response ifDestTableHaveRowseq = destTableRowseq(destFields);
                    if (ifDestTableHaveRowseq != null) {
                        return ifDestTableHaveRowseq;
                    }
                }
            } else {
                return Response.error("无法获取目标库信息，请联系管理员");
            }
        } else {
            return Response.error("无法获取所需要的目标库信息，请联系管理员");
        }
        return Response.success();
    }


    /**
     * 判断目标表是否存在ROWSEQ字段
     *
     * @param destFields
     * @return
     */
    private Response destTableRowseq(List<Map<String, Object>> destFields) {
        if (!destFields.isEmpty()) {
            List<String> fieldList = new ArrayList<>();
            for (Map<String, Object> destField : destFields) {
                fieldList.add((String) destField.get("COLUMN_NAME"));
            }
            if (!fieldList.contains("ROWSEQ")) {
                return Response.error("该目标表不存在'ROWSEQ'字段，不能作为目标表");
            }
        } else {
            return Response.error("该表中无字段");
        }
        return null;
    }
}
