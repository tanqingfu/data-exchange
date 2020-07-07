package com.sdy.dataexchange.biz.service;

import com.sdy.dataexchange.biz.model.DbNameResult;
import com.sdy.dataexchange.biz.model.ExFieldMapping;
import com.sdy.dataexchange.biz.model.ExTableMapping;
import com.sdy.dataexchange.biz.model.FieldMappingResult;
import com.sdy.mvc.service.BaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zzq
 * @since 2019-07-30
 */
public interface ExFieldMappingService extends BaseService<ExFieldMapping> {
    /**
     * 获取字段映射信息
     * @param startIndex
     * @param pageSize
     * @return List<FieldMappingResult>
     */
    List<FieldMappingResult> getMapping(Integer startIndex, Integer pageSize);
    /**
     * 通过id获取字段映射信息
     * @param id
     * @return FieldMappingResult
     */
    FieldMappingResult getInfo(Integer id);
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
