package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExMetadataDict;
import com.sdy.dataexchange.biz.mapper.ExMetadataDictMapper;
import com.sdy.dataexchange.biz.service.ExMetadataDictService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wyy
 * @since 2019-09-12
 */
@Slf4j
@Service
public class ExMetadataDictServiceImpl extends BaseServiceImpl<ExMetadataDict> implements ExMetadataDictService {
    @Autowired
    private ExMetadataDictMapper exMetadataDictMapper;
}
