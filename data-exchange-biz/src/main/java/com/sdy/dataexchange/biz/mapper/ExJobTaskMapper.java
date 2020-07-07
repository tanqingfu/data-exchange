package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyy
 * @since 2019-09-08
 */
public interface ExJobTaskMapper extends BaseMapper<ExJobTask> {
    /**
     * 获所有任务信息
     * @return  List<ExJobTask>
     */
    List<ExJobTask> queryAllTask();
    /**
     * 获所有任务信息
     * @param page
     * @return  List<TaskInfos>
     */
    List<TaskInfos> getTask(@Param("page")Page page);
    /**
     * 获取任务详情
     * @param id
     * @return   List<TaskInfoDetails>
     */
    List<TaskInfoDetails> getOneTask(@Param("id") Integer id);
    /**
     * 获取任务详情
     * @param key
     * @return  List<TaskInfos>
     */
    List<TaskInfos> getInfo(@Param("key") String key);

    /**
     * 得到删除任务中的同步字段
     * @param taskId
     */
    List<Map> getDeleteFields(@Param("taskId") Integer taskId);

    /**
     * 判断字段是否当做源字段使用
     * @param tableId
     * @param field
     * @return
     */
    List<Integer> getMappingIdBySource(@Param("tableId") Object tableId,@Param("field") Object field);

    /**
     * 判断字段是否当做目标字段使用
     * @param tableId
     * @param field
     * @return
     */
    List<Integer> getMappingIdByDest(@Param("tableId") Object tableId,@Param("field") Object field);

    /**
     * 删除字段
     * @param tableId
     * @param field
     */
    void deleteField(@Param("tableId") Object tableId,@Param("field") Object field);

    /**
     *
     * @param sourceTableId
     * @param sourceField
     * @param destTableId
     * @param destField
     * @return
     */
    Integer getMappingIds(@Param("sourceTableId") Object sourceTableId, @Param("sourceField") Object sourceField
            ,@Param("destTableId") Object destTableId,@Param("destField") Object destField);

    /**
     * 获取所有字段
     * @return
     */
    List<Map<String,Object>> getAllFields();

    /**
     * 获取任务总数
     * @return
     */
    Integer getTotle();

    /**
     * 获取任务总数
     * @param id
     * @return
     */
    Integer getDetailTotle(@Param("id") Integer id);
    /**
     * 通过库id，表名获取表
     * @param dbId
     * @param tableName
     */
    ExTableDict getTableId(@Param("dbId")String dbId, @Param("tableName") String tableName);
    /**
     * 获取所有同步过得字段
     * @return List<String>
     */
    List<String> getFields();
    /**
     * 如果是第一次同步，将同步表id，源库id，表名插入ex_table_dict
     * @param leftDbId
     * @param leftTableName
     * @param createTime
     */
    void insertIntoExTableDict( @Param("leftDbId") String leftDbId, @Param("leftTableName") String leftTableName
            , @Param("createTime") String createTime);

    /**
     * 如果是第一次同步，将源库同步表和目标库同步表id插入ex_table_mapping
     * @param destSyncId
     * @param createTime
     */

    void insertIntoExTableMapping(@Param("taskName") String taskName,@Param("sourceSyncId") Integer sourceSyncId
            ,@Param("destSyncId") Integer destSyncId, @Param("createTime") String createTime);

    /**
     * 如果不是第一次同步，在ex_field_dict中查到sync_seqno
     * @param sourceSyncid
     * @param field
     */
    Integer getSyncSeqno(@Param("sourceSyncid") Integer sourceSyncid, @Param("field") String field);
    /**
     * 通过源表id，目标表id获取同步id
     * @param sourceSyncId
     * @param destSyncId
     * @return  Integer
     */
    Integer getId(@Param("sourceSyncId") Integer sourceSyncId, @Param("destSyncId") Integer destSyncId);
    /**
     * 通过库id，表名获取表id
     * @param dbId
     * @param tableName
     * @return  Integer
     */
    Integer getSyncIds(@Param("dbId") String dbId,@Param("tableName") String tableName);
    /**
     * 通过字段id，字段名从数据库删除
     * @param syncId
     * @param syncField
     */
    void deleteFromExFieldDict(@Param("syncId") Integer syncId, @Param("syncField") String syncField);
    /**
     * 通过源字段id，字段名从映射表中删除映射关系
     * @param sourceSyncId
     * @param leftSyncField
     */
    Integer deleteFromExFieldMapping(@Param("sourceSyncId")Integer sourceSyncId, @Param("leftSyncField")String leftSyncField
            ,@Param("destSyncId") Integer destSyncId,@Param("rightSyncField") String rightSyncField);
    /**
     * 通过字段id，获取字段名
     * @param syncId
     * @return  List<String>
     */
    List<String> getSyncField(@Param("syncId")Integer syncId);
    /**
     * 通过库名获取库id
     * @param dbName
     * @return  List<Integer>
     */
    Integer getDbId(@Param("dbName")String dbName);
    /**
     * 通过源表id，字段名，目标表id，字段名获取字段映射id
     * @param sourceSyncId
     * @param leftFieldName
     * @param destSyncId
     * @param rightFieldName
     * @return  Integer
     */
    Integer getFieldMappingId(@Param("sourceSyncId") Integer sourceSyncId,@Param("leftFieldName") String leftFieldName
            , @Param("destSyncId")Integer destSyncId, @Param("rightFieldName")String rightFieldName);

    /**
     * 将库表信息加入主键表
     * @param dbId
     * @param syncId
     * @return  List<Integer>
     */
    void addPrimaryKey(@Param("dbId") Integer dbId, @Param("syncId")Integer syncId);
    /**
     * 通过库名，表名获取主键id
     * @param dbId
     * @param syncId
     * @return  Integer
     */
    Integer getPrimaryId(@Param("dbId") Integer dbId, @Param("syncId")Integer syncId);
    /**
     * 新增任务
     * @param taskName
     * @param dbMappingId
     * @param tbMapId
     * @param syncId3
     * @param validFlag
     */
    void addJobTask(@Param("taskName") String taskName,@Param("dbMappingId") Integer dbMappingId
            , @Param("tbMapId")Integer tbMapId,@Param("syncId3") Integer syncId3,@Param("validFlag") String validFlag);
    /**
     * 通过所有转换名
     * @return  List<String>
     */
    List<String> getAllTaskNames();
    /**
     * 通过表id获取目标表
     * @param syncId1
     * @return  Integer
     */
    Integer getDestTb(@Param("syncId1") Integer syncId1);
    /**
     * 通过库映射id，表映射id获取任务id
     * @param dbMappingId
     * @param tbMapId
     * @return  Integer
     */
    Integer getTaskId(@Param("dbMappingId") Integer dbMappingId,@Param("tbMapId") Integer tbMapId);
    /**
     * 更新转换名
     * @param taskName
     * @param dbMappingId
     * @param tbMapId1
     * @return  Integer
     */
    Integer updateTaskName(@Param("taskName") String taskName,@Param("dbMappingId") Integer dbMappingId
            ,@Param("tbMapId1") Integer tbMapId1);

    /**
     * 更新主键信息
     * @param exTablePrimarykey
     */
    void updatePrimaryKey(@Param("exTablePrimarykey")ExTablePrimarykey exTablePrimarykey);

    /**
     *
     * @param syncId2
     * @param rightSyncField
     * @return
     */
    Integer getFieldMappingIdByDestField(@Param("syncId2") Integer syncId2,@Param("rightSyncField") String rightSyncField);

    /**
     * 将源字段加入到ExFieldDict
     * @param leftFields
     */
    void insertIntoLeftExFieldDicts(@Param("leftFields") List<ExFieldDict> leftFields);

    /**
     * 将目标字段加入到ExFieldDict
     * @param rightFields
     */
    void insertIntoRightExFieldDicts(@Param("rightFields") List<ExFieldDict> rightFields);

    /**
     * 将子墩同步信息加入到ExFieldMapping
     * @param exFieldMappings
     * @return
     */
    Integer insertIntoExFieldMappings(@Param("exFieldMappings") List<ExFieldMapping> exFieldMappings);

    /**
     * 通过源字段过去字段同步id
     * @param sourceSyncId
     * @param leftSyncField
     * @return
     */
    Integer getFieldMappingBySourceField(@Param("sourceSyncId") Integer sourceSyncId, @Param("leftSyncField") String leftSyncField);

    /**
     * 通过源库id，目标库id获取映射id
     * @param sourceDbId
     * @param destDbId
     * @return
     */
    Integer getDbMappingId(@Param("sourceDbId") String sourceDbId, @Param("destDbId") String destDbId);

    /**
     * 获取所有同步过的字段信息
     * @return
     */
    List<Map> getAllFieldsDone();

    List<ExDbDict> listMappedSrcDb(Integer exTaskId);

    List<ExTableDict> listMappedSrcTb(Integer exTaskId);

    /**
     * 获取作业状态
     * @param beforeDbMappingId
     * @param beforeTbMappingId
     * @return
     */
    Integer getJobState(@Param("beforeDbMappingId") Integer beforeDbMappingId, @Param("beforeTbMappingId") Integer beforeTbMappingId);

    void deleteExFieldMapping(@Param("beforeSourceSyncId") Integer beforeSourceSyncId,@Param("leftSyncField") String leftSyncField, @Param("beforeDestSyncId") Integer beforeDestSyncId);

    boolean insertIntoFieldDict(@Param("beforeDestSyncId") Integer beforeDestSyncId,@Param("rightField") String rightField,@Param("rightType") String rightType);

    void updateFieldMapping( @Param("fieldMappingId") Integer fieldMappingId, @Param("rightField") String rightField);

    void insertIntoFieldMaping(@Param("beforeSourceSyncId") Integer beforeSourceSyncId,@Param("leftField") String leftField, @Param("beforeDestSyncId") Integer beforeDestSyncId, @Param("rightField") String rightField);
}
