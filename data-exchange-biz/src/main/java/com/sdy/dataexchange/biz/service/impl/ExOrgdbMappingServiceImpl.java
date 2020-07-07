package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExOrgdbMapping;
import com.sdy.dataexchange.biz.mapper.ExOrgdbMappingMapper;
import com.sdy.dataexchange.biz.service.ExOrgdbMappingService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
@Slf4j
@Service
public class ExOrgdbMappingServiceImpl extends BaseServiceImpl<ExOrgdbMapping> implements ExOrgdbMappingService {
    @Autowired
    private ExOrgdbMappingMapper exOrgdbMappingMapper;
}
