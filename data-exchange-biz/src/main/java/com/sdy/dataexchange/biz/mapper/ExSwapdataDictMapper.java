package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.ExSwapdataDict;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
public interface ExSwapdataDictMapper extends BaseMapper<ExSwapdataDict> {
    List<Map> querySwapdata(@Param("jobtaskId") String jobtaskId, @Param("sourceTableName") String sourceTableName
            ,@Param("liftCreateTime")String liftCreateTime,@Param("rightCreateTime")String rightCreateTime ,Page page);
    List<Map> querySwapdata2(@Param("jobId")String jobId, Page page);

    Integer querySwadataSize(@Param("jobtaskId") String jobtaskId, @Param("sourceTableName") String sourceTableName
            , @Param("liftCreateTime")String liftCreateTime, @Param("rightCreateTime")String rightCreateTime );
    List<Map> querySwadataSize2(@Param("jobId")String jobId);


    List<ExSwapdataDict> queryGroupByJobIds(List<Integer> jobIdList);
}
