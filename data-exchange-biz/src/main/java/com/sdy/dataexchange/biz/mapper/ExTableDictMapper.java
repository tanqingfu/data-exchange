package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.DbNameResult;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-07-23
 */
public interface ExTableDictMapper extends BaseMapper<ExTableDict> {
    /**
     * 获取库表信息
     * @param page
     * @return  List<DbNameResult>
     */
    List<DbNameResult> getDbName(@Param("page")Page page);
    /**
     * 通过表id获取库表信息
     * @param syncId
     * @return  DbNameResult
     */
    DbNameResult getInfo(Integer syncId);

    Integer getTotle();

    /**
     * 查询需要监控的MYSQL库表
     * @return
     */
    List<ExTableDict> listToSync();

    /**
     * 通过库id获取库信息
     * @param dbId
     * @return ExDbDict
     */
    ExDbDict getDbInfo(@Param("dbId") Integer dbId);

    /**
     * 通过源库获取所有对应目标库
     * @param sourceDbId
     * @return
     */
    List<Map<String,Object>> getDestDbInfo(@Param("sourceDbId") Integer sourceDbId,@Param("gatherId") Integer gatherId);
}
