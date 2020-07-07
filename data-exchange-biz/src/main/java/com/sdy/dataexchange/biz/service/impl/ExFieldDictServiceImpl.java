package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.mapper.ExFieldDictMapper;
import com.sdy.dataexchange.biz.model.BO.FieldBO;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExFieldDict;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.model.TableNameResult;
import com.sdy.dataexchange.biz.service.ExDbDictService;
import com.sdy.dataexchange.biz.service.ExFieldDictService;
import com.sdy.dataexchange.biz.service.ExTableDictService;
import com.sdy.dataexchange.biz.service.RepeatDemoService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
@Slf4j
@Service
public class ExFieldDictServiceImpl extends BaseServiceImpl<ExFieldDict> implements ExFieldDictService {
    @Autowired
    private ExFieldDictMapper exFieldDictMapper;

    @Autowired
    private ExDbDictService exDbDictService;

    @Autowired
    private ExTableDictService exTableDictService;
    @Autowired
    RepeatDemoService repeatDemoService;

    @Override
    public List<TableNameResult> getTableName(Page page) {
        return exFieldDictMapper.getTableName(page);
    }

    @Override
    public TableNameResult getInfo(Integer syncSeqno) {
        return exFieldDictMapper.getInfo(syncSeqno);
    }

    @Override
    public Integer getTotle() {
        return exFieldDictMapper.getTotle();
    }

    @Override
    public ExDbDict getDbInfo(Integer dbId) {
        return exFieldDictMapper.getDbInfo(dbId);
    }

    @Override
    public Integer getDbId(String dbName) {
        return exFieldDictMapper.getDbId(dbName);
    }

    @Override
    public Integer getTableId(Integer dbId, String tableName) {
        return exFieldDictMapper.getTableId(dbId, tableName);
    }

    @Override
    public Response getAllSyncFields(FieldBO fieldBO) {
        Integer sourceDbId = fieldBO.getSourceDbId();
        String sourceTableName = fieldBO.getSourceTableName();
        Integer destDbId = fieldBO.getDestDbId();
        String destTableName = fieldBO.getDestTableName();
        List<Map<String, Object>> sourceFields = new ArrayList<>();
        List<Map<String, Object>> destFields = new ArrayList<>();
        Map<String, Object> columns = new HashMap<>(16);
        List<Map<String, Object>> list = new ArrayList<>();
        if (sourceDbId != null && StringUtils.isNotBlank(sourceTableName) && destDbId != null && StringUtils.isNotBlank(destTableName)) {
//            String regStr = "^[^\\\\&*,.'\";^%+\\-()|<>?/=$#@!~\\s]+$";
            //获取源表id
            Integer sourceTableId =null;
            ExTableDict sourceExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                    .eq(ExTableDict::getDbId, sourceDbId)
                    .eq(ExTableDict::getDbTable, sourceTableName));
            if (sourceExTableDict != null) {
                 sourceTableId=sourceExTableDict.getSyncId();
            }
            //获取目标表id
            Integer destTableId=null;
            ExTableDict destExTableDict = exTableDictService.getOne(new LambdaQueryWrapper<ExTableDict>()
                    .eq(ExTableDict::getDbId, destDbId)
                    .eq(ExTableDict::getDbTable, destTableName));
            if (destExTableDict != null) {
                destTableId = destExTableDict.getSyncId();
            }

            ExDbDict sourceExDbDict = exDbDictService.getById(sourceDbId);
            ExDbDict destExDbDict = exDbDictService.getById(destDbId);

            // 获取同步过的字段
            List<Map> doneFields =new ArrayList<>();
            if (sourceTableId != null && destTableId != null) {
               doneFields = exFieldDictMapper.getDoneFields(sourceTableId, destTableId);
            }

            if (sourceExDbDict != null && StringUtils.isNotBlank(sourceExDbDict.getDbIp()) && sourceExDbDict.getDbPort() != null
                    && StringUtils.isNotBlank(sourceExDbDict.getDbName()) && StringUtils.isNotBlank(sourceExDbDict.getDbUser())
                    && StringUtils.isNotBlank(sourceExDbDict.getDbPasswd()) && StringUtils.isNotBlank(sourceExDbDict.getDbType())) {
                //如果源数据库是MYSQL类型，获取所有字段
                sourceFields = getFields(sourceTableName, sourceFields, sourceExDbDict);
            } else {
                return Response.error("连接数据库信息错误，请联系管理员");
            }

            if (destExDbDict != null && StringUtils.isNotBlank(destExDbDict.getDbIp()) && destExDbDict.getDbPort() != null
                    && StringUtils.isNotBlank(destExDbDict.getDbName()) && StringUtils.isNotBlank(destExDbDict.getDbUser())
                    && StringUtils.isNotBlank(destExDbDict.getDbPasswd()) && StringUtils.isNotBlank(destExDbDict.getDbType())) {

                destFields = getFields(destTableName, destFields, destExDbDict);
                destFields.removeIf(item -> item.get("field").equals("ROWSEQ"));
            } else {
                return Response.error("连接数据库信息错误，请联系管理员");
            }

            //新建任务时默认按顺序匹配
            List<Map<String, Object>> mateFields = new ArrayList();
            if (!sourceFields.isEmpty()) {
                if (!destFields.isEmpty()) {
                    for (int i = 0; i < sourceFields.size(); i++) {
                        Map<String, Object> sourceField = sourceFields.get(i);
                        for (int j = 0; j < destFields.size(); j++) {
                            Map<String, Object> destField = destFields.get(j);
                            Map<String, Object> mateField = new HashMap<>(16);
                            if (sourceField != null && destField != null && i == j) {
                                mateField.put("sourceField", sourceField.get("field"));
                                mateField.put("sourceFieldType", sourceField.get("fieldType"));
                                mateField.put("destField", destField.get("field"));
                                mateField.put("destFieldType", destField.get("fieldType"));
                                mateFields.add(mateField);
                            }
                        }
                    }
                }
            }
            columns.put("sourceFields", sourceFields);
            columns.put("destFields", destFields);
            columns.put("doneFields", doneFields);
            columns.put("mateFields", mateFields);
            list.add(columns);
            return Response.success(list);
        } else {
            return Response.error("获取数据库信息失败，请联系管理员");
        }
    }

    /**
     * 无论mysql，oracle，获取字段
     * @param tableName
     * @param fields
     * @param exDbDict
     * @return
     */
    private List<Map<String, Object>> getFields(String tableName, List<Map<String, Object>> fields, ExDbDict exDbDict) {
        if (DbType.MYSQL.getDb().equalsIgnoreCase(exDbDict.getDbType())) {
            fields = mysqlFields(tableName, exDbDict);
        } else if (DbType.ORACLE.getDb().equalsIgnoreCase(exDbDict.getDbType())) {
            fields = oracleFields(tableName, exDbDict);
        } else if (DbType.SQL_SERVER.getDb().equalsIgnoreCase(exDbDict.getDbType())) {
            fields = sqlserverFields(tableName, exDbDict);
        }
        return fields;
    }


    /**
     * 获取mysql字段
     *
     * @param tableName
     * @param exDbDict
     * @return
     */
    private List<Map<String, Object>> mysqlFields(String tableName, ExDbDict exDbDict) {
        List<Map<String, Object>> fields = new ArrayList<>();
        DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, exDbDict.getDbIp(), exDbDict.getDbPort()
                , exDbDict.getDbName(), exDbDict.getDbUser(), exDbDict.getDbPasswd(), null));
        fields = dataAdapter.queryForList("SELECT * FROM information_schema.columns WHERE table_schema = " +
                "? AND table_name = ? ORDER BY column_name ASC;",exDbDict.getDbName(),tableName);
        putField(fields);
        return fields;
    }



    /**
     * 获取oracle字段
     *
     * @param tableName
     * @param exDbDict
     * @return
     */
    private List<Map<String, Object>> oracleFields(String tableName, ExDbDict exDbDict) {
        List<Map<String, Object>> fields = new ArrayList<>();
        DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, exDbDict.getDbIp(), exDbDict.getDbPort()
                , exDbDict.getDbName(), exDbDict.getDbUser(), exDbDict.getDbPasswd(), null));
        fields = dataAdapter.queryForList("select COLUMN_NAME,DATA_TYPE from user_tab_columns where " +
                "TABLE_NAME=? order by COLUMN_ID ASC", tableName);
        putField(fields);
        return fields;
    }

    /**
     * 获取oracle字段
     *
     * @param tableName
     * @param exDbDict
     * @return
     */
    private List<Map<String, Object>> sqlserverFields(String tableName, ExDbDict exDbDict) {
        DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.SQL_SERVER, exDbDict.getDbIp(), exDbDict.getDbPort()
                , exDbDict.getDbName(), exDbDict.getDbUser(), exDbDict.getDbPasswd(), null));
        List<Map<String, Object>> fields = dataAdapter.queryForList("SELECT COLUMN_NAME,DATA_TYPE FROM INFORMATION_SCHEMA.columns WHERE TABLE_NAME=? order by ORDINAL_POSITION;", tableName);
        putField(fields);
        return fields;
    }


    /**
     * 返回的字段
     * @param fields
     */
    private void putField(List<Map<String, Object>> fields) {
        if (!fields.isEmpty()) {
            for (Map<String, Object> field : fields) {
                //更改map的key，增加目标字段名和类型
                field.put("field", field.remove("COLUMN_NAME"));
                field.put("fieldType", field.remove("DATA_TYPE"));
                field.put("field1", "");
                field.put("field1Type", "");
            }
        }
    }
}
