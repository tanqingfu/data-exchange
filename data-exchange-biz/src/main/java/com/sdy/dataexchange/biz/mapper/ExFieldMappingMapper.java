package com.sdy.dataexchange.biz.mapper;

import com.sdy.dataexchange.biz.model.ExFieldMapping;
import com.sdy.dataexchange.biz.model.FieldMappingResult;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-07-30
 */
public interface ExFieldMappingMapper extends BaseMapper<ExFieldMapping> {
    /**
     * 获取字段映射信息
     * @param startIndex
     * @param pageSize
     * @return List<FieldMappingResult>
     */
    List<FieldMappingResult> getMapping(@Param("startIndex") Integer startIndex,@Param("pageSize") Integer pageSize);
    /**
     * 通过id获取字段映射信息
     * @param id
     * @return FieldMappingResult
     */
    FieldMappingResult getInfo(@Param("id") Integer id);
    /**
     * 通过源表id获取字段映射信息
     * @param sourceTableId
     * @return List<ExFieldMapping>
     */
    List<ExFieldMapping> getExFieldMapping(Integer sourceTableId);
    /**
     * 获取字段名
     * @param syncid
     * @return String
     */
    String getFieldName(Integer syncid);
}
