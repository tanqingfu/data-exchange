package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExParam;
import com.sdy.dataexchange.biz.mapper.ExParamMapper;
import com.sdy.dataexchange.biz.service.ExParamService;
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
 * @since 2019-10-29
 */
@Slf4j
@Service
public class ExParamServiceImpl extends BaseServiceImpl<ExParam> implements ExParamService {
    @Autowired
    private ExParamMapper exParamMapper;

    @Override
    public String getParamOrDefault(String key, String defaultValue) {
        ExParam param = lambdaQuery().eq(ExParam::getParamKey, key).one();
        return param == null ? defaultValue : param.getParamValue();
    }
}
