package com.sdy.dataexchange.biz.mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 高连明
 * @since 2019-08-26
 */
public interface ShowDataMapper {
    /**
     * 获取上一小时的同步数据信息
     * @param
     * @return  List<Map>
     */
    List<Map> getData();
    /**
     * 获取所有的库信息
     * * @return  List<Map>
     */
    List<Map> getDb();
}
