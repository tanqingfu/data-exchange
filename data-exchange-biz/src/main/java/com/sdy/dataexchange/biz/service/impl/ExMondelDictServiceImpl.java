package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExMondelDict;
import com.sdy.dataexchange.biz.mapper.ExMondelDictMapper;
import com.sdy.dataexchange.biz.service.ExMondelDictService;
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
public class ExMondelDictServiceImpl extends BaseServiceImpl<ExMondelDict> implements ExMondelDictService {
    @Autowired
    private ExMondelDictMapper exMondelDictMapper;
}
