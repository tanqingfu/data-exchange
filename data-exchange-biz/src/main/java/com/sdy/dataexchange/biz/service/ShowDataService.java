package com.sdy.dataexchange.biz.service;

import java.util.List;
import java.util.Map;

public interface ShowDataService {
    /**
     * 获取上一小时的同步数据信息
     * @param
     * @return  List<Map>
     */
    List<Map> getData();
    /**
     * 获取所有的库信息
     * @return  List<Map>
     */
    List<Map> getDb();
}
