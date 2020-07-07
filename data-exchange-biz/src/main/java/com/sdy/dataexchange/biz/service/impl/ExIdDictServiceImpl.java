package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.mapper.ExIdDictMapper;
import com.sdy.dataexchange.biz.model.ExIdDict;
import com.sdy.dataexchange.biz.service.ExIdDictService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
@Slf4j
@Service
public class ExIdDictServiceImpl extends BaseServiceImpl<ExIdDict> implements ExIdDictService {
    @Autowired
    private ExIdDictMapper exIdDictMapper;
}
