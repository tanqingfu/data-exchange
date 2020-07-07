package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExConfigChange;
import com.sdy.dataexchange.biz.mapper.ExConfigChangeMapper;
import com.sdy.dataexchange.biz.service.ExConfigChangeService;
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
public class ExConfigChangeServiceImpl extends BaseServiceImpl<ExConfigChange> implements ExConfigChangeService {
    @Autowired
    private ExConfigChangeMapper exConfigChangeMapper;
}
