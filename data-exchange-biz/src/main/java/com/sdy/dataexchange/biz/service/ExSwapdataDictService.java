package com.sdy.dataexchange.biz.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.ExSwapdataDict;
import com.sdy.mvc.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
public interface ExSwapdataDictService extends BaseService<ExSwapdataDict> {
    List<Map> querySwapdata(String jobtaskId, String sourceTableName, String liftCreateTime, String rightCreateTime, Page page);

    List<Map> querySwapdata2(String jobId, Page page);

    Integer querySwapdataSize(String jobtaskId, String sourceTableName, String liftCreateTime, String rightCreateTime);

    Integer querySwapdataSize2(String jobId);
    
    List<ExSwapdataDict> queryGroupByJobIds(List<Integer> jobIdList);
}
