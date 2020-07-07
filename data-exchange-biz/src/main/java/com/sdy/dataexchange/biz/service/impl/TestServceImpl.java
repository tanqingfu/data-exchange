/*
package com.sdy.dataexchange.biz.service.impl;

import com.sdy.dataexchange.biz.mapper.TestMapper;
import com.sdy.dataexchange.biz.model.DTO.Mapping;
import com.sdy.dataexchange.biz.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TestServceImpl implements TestService {
    @Autowired
    TestMapper testMapper;
    @Override
    public List getTables(String db_id) {
        List<Mapping> tables = testMapper.getTables(db_id);
        List tableName=null;
        for (int i=0;i<tables.size();i++){
            String table_name1 = tables.get(i).getTable_name1();
            tableName.add(table_name1);
        }
        return tableName;
    }
}
*/
