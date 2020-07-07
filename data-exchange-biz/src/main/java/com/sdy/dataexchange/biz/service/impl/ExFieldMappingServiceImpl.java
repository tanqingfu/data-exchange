package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sdy.dataexchange.biz.mapper.ExFieldMappingMapper;
import com.sdy.dataexchange.biz.model.ExFieldMapping;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.model.FieldMappingResult;
import com.sdy.dataexchange.biz.service.ExFieldMappingService;
import com.sdy.dataexchange.biz.service.ExTableDictService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-07-30
 */
@Slf4j
@Service
public class ExFieldMappingServiceImpl extends BaseServiceImpl<ExFieldMapping> implements ExFieldMappingService {
    @Autowired
    private ExFieldMappingMapper exFieldMappingMapper;
    @Autowired
    ExFieldMappingService exFieldMappingService;
    @Autowired
    ExTableDictService exTableDictService;

    @Override
    public List<FieldMappingResult> getMapping(Integer startIndex, Integer pageSize) {
        return exFieldMappingMapper.getMapping(startIndex, pageSize);
    }

    @Override
    public FieldMappingResult getInfo(Integer id) {
        return exFieldMappingMapper.getInfo(id);
    }

    @Override
    public List<ExFieldMapping> getExFieldMapping(Integer sourceTableId) {
        return exFieldMappingService.list(new LambdaQueryWrapper<ExFieldMapping>().eq(ExFieldMapping::getSourceSyncid, sourceTableId));
    }

    @Override
    public String getFieldName(Integer syncid) {
        String dbTable = null;
        ExTableDict exTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>().eq(ExTableDict::getSyncId, syncid));
        if (exTableDict != null) {
            dbTable = exTableDict.getDbTable();
        }
        return dbTable;
    }
}
