package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.Demo;
import com.sdy.dataexchange.biz.mapper.DemoMapper;
import com.sdy.dataexchange.biz.service.DemoService;
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
 * @since 2019-06-06
 */
@Slf4j
@Service
public class DemoServiceImpl extends BaseServiceImpl<Demo> implements DemoService {
    @Autowired
    private DemoMapper demoMapper;
}
