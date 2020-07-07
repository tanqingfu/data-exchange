package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.biz.model.TableMappingResult;
import com.sdy.dataexchange.biz.model.TaskInfoResult;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-07-26
 */
public interface ExTableMappingMapper extends BaseMapper<ExTableMapping> {
    /**
     * 获取所有表映射关系
     * @param page
     * @return  List<TableMappingResult>
     */
    List<TableMappingResult> getMapping(@Param("page") Page page);
    /**
     * 获取所有任务信息
     * @param id
     * @return  List<TaskInfoResult>
     */
    List<TaskInfoResult> getInfo(@Param("id") Integer id);
    /**
     * 获取表id
     * @param taskName
     * @return  Integer
     */
    Integer getTableId(@Param("taskName") String taskName);
    /**
     * 获取表映射信息
     ** @param id
     * @return  TableMappingResult
     */
    TableMappingResult getOneInfo(@Param("id") Integer id);
    /**
     * 更新表映射关系
     * @param tableMappingResult
     * @return  Integer
     */
    Integer updateInfo(@Param("tableMappingResult") TableMappingResult tableMappingResult);

    /**
     * 删除任务
     * @param integers
     */
    void removeTask(@Param("integers") List<Integer> integers);

    /**
     * 获取库id
     * @param db
     * @return
     */
    Integer getDbId(@Param("db") String db,@Param("ip") String ip,@Param("port") Integer port,@Param("user") String user);

    /**
     * 获取分页后的总数
     * @param id
     * @return
     */
    Integer getTotle(Integer id);

    /**
     * 获取表映射总数
     * @return
     */
    Integer getAll();

    /**
     * 通过任务名查询
     * @param key
     * @param page
     * @return
     */
    List<TableMappingResult> getInfos(@Param("key") String key,@Param("page") Page page);
}
