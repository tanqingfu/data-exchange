package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExFieldmappingRule;
import com.sdy.dataexchange.biz.mapper.ExFieldmappingRuleMapper;
import com.sdy.dataexchange.biz.service.ExFieldmappingRuleService;
import com.sdy.mvc.annotation.RemoteService;
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
 * @since 2019-07-22
 */
@Slf4j
@Service
public class ExFieldmappingRuleServiceImpl extends BaseServiceImpl<ExFieldmappingRule> implements ExFieldmappingRuleService {
    @Autowired
    private ExFieldmappingRuleMapper exFieldmappingRuleMapper;
}
