package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExTablePrimarykey;
import com.sdy.dataexchange.biz.mapper.ExTablePrimarykeyMapper;
import com.sdy.dataexchange.biz.service.ExTablePrimarykeyService;
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
public class ExTablePrimarykeyServiceImpl extends BaseServiceImpl<ExTablePrimarykey> implements ExTablePrimarykeyService {
    @Autowired
    private ExTablePrimarykeyMapper exTablePrimarykeyMapper;
}
