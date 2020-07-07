package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.mapper.ExSwapdataDictMapper;
import com.sdy.dataexchange.biz.model.ExSwapdataDict;
import com.sdy.dataexchange.biz.service.ExSwapdataDictService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
@Slf4j
@Service
public class ExSwapdataDictServiceImpl extends BaseServiceImpl<ExSwapdataDict> implements ExSwapdataDictService {
    @Autowired
    private ExSwapdataDictMapper exSwapdataDictMapper;

    @Override
    public List<Map> querySwapdata(String jobtaskId, String sourceTableName, String liftCreateTime, String rightCreateTime, Page page) {
        return exSwapdataDictMapper.querySwapdata(jobtaskId, sourceTableName, liftCreateTime, rightCreateTime, page);

    }

    @Override
    public Integer querySwapdataSize(String jobtaskId, String sourceTableName, String liftCreateTime, String rightCreateTime) {
        return exSwapdataDictMapper.querySwadataSize(jobtaskId, sourceTableName, liftCreateTime, rightCreateTime);
    }

    @Override
    public List<Map> querySwapdata2(String jobId, Page page) {
        return exSwapdataDictMapper.querySwapdata2(jobId, page);
    }


    @Override
    public Integer querySwapdataSize2(String jobId) {
        List<Map> list = exSwapdataDictMapper.querySwadataSize2(jobId);
        Integer listSize = list.size();
        return listSize;
    }

    @Override
    public List<ExSwapdataDict> queryGroupByJobIds(List<Integer> jobIdList) {
        return exSwapdataDictMapper.queryGroupByJobIds(jobIdList);
    }
}
