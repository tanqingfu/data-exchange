package com.sdy.dataexchange.biz.service;

import com.sdy.dataexchange.biz.model.ExParam;
import com.sdy.mvc.service.BaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author wyy
 * @since 2019-10-29
 */
public interface ExParamService extends BaseService<ExParam> {
    String getParamOrDefault(String key, String defaultValue);
}
