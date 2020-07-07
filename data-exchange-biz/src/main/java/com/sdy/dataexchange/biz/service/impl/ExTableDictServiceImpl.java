package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.mapper.ExTableDictMapper;
import com.sdy.dataexchange.biz.model.DbNameResult;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExTableDict;
import com.sdy.dataexchange.biz.service.ExDbDictService;
import com.sdy.dataexchange.biz.service.ExTableDictService;
import com.sdy.dataexchange.biz.service.RepeatDemoService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import com.sun.org.apache.regexp.internal.RE;
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
 * @since 2019-07-23
 */
@Slf4j
@Service
public class ExTableDictServiceImpl extends BaseServiceImpl<ExTableDict> implements ExTableDictService {
    @Autowired
    private ExTableDictMapper exTableDictMapper;
    @Autowired
    private ExDbDictService exDbDictService;
    @Autowired
    private RepeatDemoService repeatDemoService;

    String regStr = "^[^\\\\&*,.'\";^%+\\-()|<>?/=$#@!~\\s]+$";


    @Override
    public List<DbNameResult> getDbName(Page page) {
        return exTableDictMapper.getDbName(page);
    }

    @Override
    public DbNameResult getInfo(Integer syncId) {
        return exTableDictMapper.getInfo(syncId);
    }

    @Override
    public Integer getTotle() {
        return exTableDictMapper.getTotle();
    }

    @Override
    public List<ExTableDict> listToSync() {
        return exTableDictMapper.listToSync();
    }

    @Override
    public ExDbDict getDbInfo(Integer dbId) {
        return exTableDictMapper.getDbInfo(dbId);
    }

    /**
     * 获取目标表和字段
     *
     * @param destDbId
     * @return
     */
    @Override
    public Response getDestTablesAndFields(Integer destDbId) {
        List destTables = new ArrayList();
        List<Map<String, Object>> allInfo = new ArrayList<>();
        Map<String, Object> destDb = new HashMap(16);
        if (destDbId != null) {
            ExDbDict destExDbDict = exDbDictService.getById(destDbId);
            //如果目标库是MYSQL类型
            if (destExDbDict != null && StringUtils.isNotBlank(destExDbDict.getDbIp()) && destExDbDict.getDbPort() != null
                    && StringUtils.isNotBlank(destExDbDict.getDbName()) && StringUtils.isNotBlank(destExDbDict.getDbUser())
                    && StringUtils.isNotBlank(destExDbDict.getDbPasswd()) && StringUtils.isNotBlank(destExDbDict.getDbType())) {
                if (!destExDbDict.getDbName().matches(regStr)) {
                    return Response.error("目标库名不符合规范，请重新选择");
                }
                if (DbType.MYSQL.getDb().equalsIgnoreCase(destExDbDict.getDbType())) {
                    List<Map<String, Object>> tableNames = new ArrayList<>();
                    List<Map<String, Object>> fields = new ArrayList<>();
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, destExDbDict.getDbIp()
                            , destExDbDict.getDbPort(), destExDbDict.getDbName(), destExDbDict.getDbUser()
                            , destExDbDict.getDbPasswd(), null));
                    //获取所有表名
                    tableNames = dataAdapter.executeQuery("show tables from " + destExDbDict.getDbName());
                    if (!tableNames.isEmpty()) {
                        for (Map<String, Object> tableName : tableNames) {
                            tableName.put("text", tableName.remove("Tables_in_" + destExDbDict.getDbName()));
                            //获取该表所有字段名
                            if (tableName.get("text") == null) {
                                return Response.error("无法获取表名，请联系管理员");
                            }
                            fields = dataAdapter.queryForList("SELECT * FROM information_schema.columns WHERE table_schema =?" +
                                    " AND table_name = ? ORDER BY column_name ASC;", destExDbDict.getDbName(), tableName.get("text"));
                            repeatDemoService.setFields(fields, tableName);
                            destTables.add(tableName);
                        }
                    } else {
                        return Response.error("该库下没有表");
                    }
                } else if (DbType.ORACLE.getDb().equalsIgnoreCase(destExDbDict.getDbType())) {
                    List<Map<String, Object>> tableNames = new ArrayList<>();
                    List<Map<String, Object>> fields = new ArrayList<>();
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, destExDbDict.getDbIp()
                            , destExDbDict.getDbPort(), destExDbDict.getDbName(), destExDbDict.getDbUser()
                            , destExDbDict.getDbPasswd(), null));
                    //获取所有表名
                    tableNames = dataAdapter.executeQuery("select * from user_tab_comments");
                    if (!tableNames.isEmpty()) {
                        for (Map<String, Object> tableName : tableNames) {
                            tableName.put("text", tableName.remove("TABLE_NAME"));
                            //获取所有字段名
                            if (tableName.get("text") == null) {
                                return Response.error("无法获取表名，请联系管理员");
                            }
                            fields = dataAdapter.queryForList("select COLUMN_NAME,DATA_TYPE from user_tab_columns " +
                                    "where TABLE_NAME=? order by COLUMN_ID ASC", tableName.get("text"));
                            repeatDemoService.setFields(fields, tableName);
                            destTables.add(tableName);
                        }
                    } else {
                        return Response.error("该库下没有表");
                    }
                } else if (DbType.SQL_SERVER.getDb().equalsIgnoreCase(destExDbDict.getDbType())) {
                    List<Map<String, Object>> tableNames;
                    List<Map<String, Object>> fields;
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.SQL_SERVER, destExDbDict.getDbIp()
                            , destExDbDict.getDbPort(), destExDbDict.getDbName(), destExDbDict.getDbUser()
                            , destExDbDict.getDbPasswd(), null));
                    //获取所有表名
                    tableNames = dataAdapter.executeQuery("SELECT name text FROM SysObjects Where XType='U' ORDER BY Name;");
                    if (!tableNames.isEmpty()) {
                        for (Map<String, Object> tableName : tableNames) {
                            //获取该表所有字段名
                            if (tableName.get("text") == null) {
                                return Response.error("无法获取表名，请联系管理员");
                            }
                            fields = dataAdapter.queryForList("SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.columns WHERE TABLE_NAME=?" +
                                    " order by ORDINAL_POSITION ASC;", tableName.get("text"));
                            repeatDemoService.setFields(fields, tableName);
                            destTables.add(tableName);
                        }
                    } else {
                        return Response.error("该库下没有表");
                    }
                }
                destDb.put("destTables", destTables);
                allInfo.add(destDb);
                return Response.success(allInfo);
            } else {
                return Response.error("连接数据库信息错误，请联系管理员");
            }
        } else {
            return Response.error("无法获取库id，请联系管理员");
        }
    }

    /**
     * 获取源表和字段信息
     *
     * @param sourceDbId
     * @param gatherId
     * @return
     */
    @Override
    public Response getSourceTablesAndFields(Integer sourceDbId, Integer gatherId) {
        if (sourceDbId != null && gatherId != null) {
            ExDbDict sourceExDbDict = exDbDictService.getOne(new LambdaQueryWrapper<ExDbDict>().eq(ExDbDict::getDbId, sourceDbId));
            Map<String, Object> sourceDb = new HashMap(16);
            //库下面得所有源表名
            List<Map<String, Object>> sourceTableNames = new ArrayList<>();
            //表下面的所有字段
            List<Map<String, Object>> fields = new ArrayList<>();
            //存放所有源字段
            List<Map<String, Object>> sourceTables = new ArrayList<>();
            //返回前端的List
            List<Map<String, Object>> allInfo = new ArrayList<>();
            List<Map<String, Object>> destDbInfos = new ArrayList<>();
            if (sourceExDbDict != null && StringUtils.isNotBlank(sourceExDbDict.getDbIp()) && sourceExDbDict.getDbPort() != null
                    && StringUtils.isNotBlank(sourceExDbDict.getDbName()) && StringUtils.isNotBlank(sourceExDbDict.getDbUser())
                    && StringUtils.isNotBlank(sourceExDbDict.getDbPasswd()) && StringUtils.isNotBlank(sourceExDbDict.getDbType())) {
                if (!sourceExDbDict.getDbName().matches(regStr)) {
                    return Response.error("源库名不符合规范，请重新选择");
                }
                if (DbType.MYSQL.getDb().equalsIgnoreCase(sourceExDbDict.getDbType())) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, sourceExDbDict.getDbIp()
                            , sourceExDbDict.getDbPort(), sourceExDbDict.getDbName(), sourceExDbDict.getDbUser()
                            , sourceExDbDict.getDbPasswd(), null));
                    //获取库下所有表名

                    sourceTableNames = dataAdapter.executeQuery("show tables from " + sourceExDbDict.getDbName());
                    destDbInfos = exTableDictMapper.getDestDbInfo(sourceDbId, gatherId);
                    if (destDbInfos.isEmpty()) {
                        return Response.error("无法获取目标库信息，请联系管理员");
                    }
                    destDbInfosScirculate(destDbInfos);
                    if (!sourceTableNames.isEmpty()) {
                        for (Map<String, Object> sourceTableName : sourceTableNames) {
                            sourceTableName.put("text", sourceTableName.remove("Tables_in_" + sourceExDbDict.getDbName()));
                            //获取该表下所有字段名
                            if (sourceTableName.get("text") == null) {
                                return Response.error("无法获取表名，请联系管理员");
                            }
                            fields = dataAdapter.queryForList(
                                    "SELECT * FROM information_schema.columns WHERE table_schema =?AND table_name = ?" +
                                            " ORDER BY column_name ASC;", sourceExDbDict.getDbName(), sourceTableName.get("text"));
                            repeatDemoService.setFields(fields, sourceTableName);
                            sourceTables.add(sourceTableName);
                        }
                    }
                } else if (DbType.ORACLE.getDb().equalsIgnoreCase(sourceExDbDict.getDbType())) {
                    //如果源库是ORACLE类型
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, sourceExDbDict.getDbIp()
                            , sourceExDbDict.getDbPort(), sourceExDbDict.getDbName(), sourceExDbDict.getDbUser()
                            , sourceExDbDict.getDbPasswd(), null));
                    //获取表名
                    sourceTableNames = dataAdapter.executeQuery("select * from user_tab_comments");
                    destDbInfos = exTableDictMapper.getDestDbInfo(sourceDbId, gatherId);
                    if (destDbInfos.isEmpty()) {
                        return Response.error("无法获取目标库信息，请联系管理员");
                    }
                    destDbInfosScirculate(destDbInfos);
                    if (sourceTableNames.isEmpty()) {
                        return Response.error("无法获取源库信息，请联系管理员");
                    }
                    repeatDemoService.getSourceNames(sourceTables, sourceTableNames, dataAdapter);
                } else if (DbType.SQL_SERVER.getDb().equalsIgnoreCase(sourceExDbDict.getDbType())) {
                    //如果源库是ORACLE类型
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.SQL_SERVER, sourceExDbDict.getDbIp()
                            , sourceExDbDict.getDbPort(), sourceExDbDict.getDbName(), sourceExDbDict.getDbUser()
                            , sourceExDbDict.getDbPasswd(), null));
                    //获取表名
                    sourceTableNames = dataAdapter.executeQuery("SELECT name text FROM SysObjects Where XType='U' ORDER BY Name");
                    destDbInfos = exTableDictMapper.getDestDbInfo(sourceDbId, gatherId);
                    if (destDbInfos.isEmpty()) {
                        return Response.error("无法获取目标库信息，请联系管理员");
                    }
                    destDbInfosScirculate(destDbInfos);
                    if (!sourceTableNames.isEmpty()) {
                        for (Map<String, Object> sourceTableName : sourceTableNames) {
                            //获取该表下所有字段名
                            if (sourceTableName.get("text") == null) {
                                return Response.error("无法获取表名，请联系管理员");
                            }
                            fields = dataAdapter.queryForList(
                                    "SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.columns WHERE TABLE_NAME=?" +
                                            " order by ORDINAL_POSITION ASC;", sourceTableName.get("text"));
                            repeatDemoService.setFields(fields, sourceTableName);
                            sourceTables.add(sourceTableName);
                        }
                    }
                }
            } else {
                return Response.error("连接数据库信息错误，请联系管理员");
            }
            sourceDb.put("sourceTables", sourceTables);
            sourceDb.put("destDbInfo", destDbInfos);
            allInfo.add(sourceDb);
            return Response.success(allInfo);
        } else {
            return Response.error("无法获取交换节点及库信息，请联系管理员");
        }
    }

    /**
     * 目标库信息循坏
     *
     * @param destDbInfos
     */
    private void destDbInfosScirculate(List<Map<String, Object>> destDbInfos) {
        for (Map<String, Object> destDbInfo : destDbInfos) {
            destDbInfo.put("destDb", destDbInfo.get("destDbName"));
            destDbInfo.put("destDbIp", destDbInfo.get("destDbIp"));
            destDbInfo.put("destDbPort", destDbInfo.get("destDbPort"));
            destDbInfo.put("destDbUser", destDbInfo.get("destDbUser"));

        }
    }
}
