package com.sdy.dataexchange.biz.mapper;

import com.sdy.dataexchange.biz.model.ExMonitorMysql;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyy
 * @since 2019-10-10
 */
public interface ExMonitorMysqlMapper extends BaseMapper<ExMonitorMysql> {

    void deleteExpiredAppendLog(Long ts);

    int countDataSize(@Param("tableId") Integer tableId, @Param("startId") Long startId, @Param("count") Integer count);
}
