package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.model.ExSyncdbCtl;
import com.sdy.dataexchange.biz.mapper.ExSyncdbCtlMapper;
import com.sdy.dataexchange.biz.service.ExSyncdbCtlService;
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
public class ExSyncdbCtlServiceImpl extends BaseServiceImpl<ExSyncdbCtl> implements ExSyncdbCtlService {
    @Autowired
    private ExSyncdbCtlMapper exSyncdbCtlMapper;
}
