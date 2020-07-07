package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.mapper.ExDbDictMapper;
import com.sdy.dataexchange.biz.model.*;
import com.sdy.dataexchange.biz.model.BO.DatabaseBO;
import com.sdy.dataexchange.biz.service.*;
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
import java.util.stream.Collectors;

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
public class ExDbDictServiceImpl extends BaseServiceImpl<ExDbDict> implements ExDbDictService {
    @Autowired
    private ExDbDictMapper exDbDictMapper;
    @Autowired
    private ExDbDictService exDbDictService;
    @Autowired
    private RepeatDemoService repeatDemoService;
    @Autowired
    private ExOrgdbMappingService exOrgdbMappingService;
    @Autowired
    private JobService jobService;
    @Autowired
    private ExSyncmonUserinfoService exSyncmonUserinfoService;
    @Autowired
    private ExDbMappingService exDbMappingService;
    @Autowired
    private ExParamService exParamService;


    /**
     * 更新数据源
     *
     * @param dbId
     * @param dbDesc
     * @param dbIp
     * @param dbPort
     * @param dbUser
     * @param dbPasswd
     * @param dbName
     * @param dbType
     * @param office
     * @param userInfo
     * @return
     */
    @Override
    public Response updateDb(Integer dbId, String dbDesc, String dbIp, Integer dbPort, String dbUser, String dbPasswd
            , String dbName, String dbType, Integer office, String userInfo) {
        if (StringUtils.isNotBlank(dbDesc) && StringUtils.isNotBlank(dbIp) && dbPort != null && StringUtils.isNotBlank(dbUser)
                && StringUtils.isNotBlank(dbPasswd) && StringUtils.isNotBlank(dbName) && StringUtils.isNotBlank(dbType) && office != null) {
            List<ExDbMapping> sourceExDbMapping = exDbMappingService.list(new LambdaQueryWrapper<ExDbMapping>()
                    .eq(ExDbMapping::getSourceDbid, dbId));
            List<ExDbMapping> destExDbMapping = exDbMappingService.list(new LambdaQueryWrapper<ExDbMapping>()
                    .eq(ExDbMapping::getDestDbid, dbId));
            if (sourceExDbMapping.isEmpty() && destExDbMapping.isEmpty()) {
                if (userInfo != null) {
                    ExSyncmonUserinfo exSyncmonUserinfo = exSyncmonUserinfoService.getById(userInfo);
                    if (judgeUserinfo(dbIp, dbPort, exSyncmonUserinfo)) {
                        return Response.error("请选择正确的同步账号");
                    }
                }
                Integer getDbId =  getDbId(dbIp, dbPort, dbUser,dbName);
                List<String> allInfo = exDbDictMapper.getAllInfo(office);
                String name = repeatDemoService.getFullName(allInfo);
                if (name != null) {
                    if (dbId != null) {
                        if (getDbId == null || getDbId.equals(dbId)) {
                            if (DbType.MYSQL.getDb().equalsIgnoreCase(dbType)) {
                                //测试连接数据库，如连接成功则修改
                                DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, dbIp, dbPort, dbName
                                        , dbUser, dbPasswd, null));
                                return updateDatabase(dbId, dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, office, userInfo, name, dataAdapter, "mysql_conn_timeout");
                            } else if (DbType.ORACLE.getDb().equalsIgnoreCase(dbType)) {
                                //测试连接数据库，如连接成功则修改
                                DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, dbIp, dbPort, dbName
                                        , dbUser, dbPasswd, null));
                                return updateDatabase(dbId, dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, office, userInfo, name, dataAdapter, "oracle_conn_timeout");
                            } else if (DbType.SQL_SERVER.getDb().equalsIgnoreCase(dbType)) {
                                //测试连接数据库，如连接成功则修改
                                DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.SQL_SERVER, dbIp, dbPort, dbName
                                        , dbUser, dbPasswd, null));
                                return updateDatabase(dbId, dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, office, userInfo, name, dataAdapter, "sqlserver_conn_timeout");
                            } else if (DbType.DB2.getDb().equalsIgnoreCase(dbType)) {
                                //测试连接数据库，如连接成功则修改
                                DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.DB2, dbIp, dbPort, dbName
                                        , dbUser, dbPasswd, null));
                                return updateDatabase(dbId, dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, office, userInfo, name, dataAdapter, "db2_conn_timeout");
                            }
                        } else if (!getDbId.equals(dbId)) {
                            return Response.error("该数据库已存在！");
                        }
                    } else {
                        return Response.error("无法获取库id，请联系管理员");
                    }
                } else {
                    return Response.error("库源所属单位不能为空");
                }
            } else {
                return Response.error("该库源正在使用中，禁止修改");
            }
        } else {
            return Response.error("请填写完整信息！");
        }
        return Response.success("编辑成功");
    }

    /**
     * 更新数据源成功或者失败
     *
     * @param dbId
     * @param dbDesc
     * @param dbIp
     * @param dbPort
     * @param dbUser
     * @param dbPasswd
     * @param dbName
     * @param dbType
     * @param office
     * @param userInfo
     * @param name
     * @param dataAdapter
     * @return
     */
    private Response updateDatabase(Integer dbId, String dbDesc, String dbIp, Integer dbPort, String dbUser,
                                    String dbPasswd, String dbName, String dbType, Integer office, String userInfo,
                                    String name, DataAdapter dataAdapter, String timeoutKey) {
        if (dataAdapter.checkConnection(Long.valueOf(exParamService.getParamOrDefault(timeoutKey, "2000")))) {
            //更新库信息
            if (exDbDictMapper.updateDbInfo(dbId, dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType
                    , office, name, userInfo) != null) {
                return Response.success("更新数据源成功！");
            } else {
                return Response.error("更新失败");
            }
        } else {
            return Response.error("该数据库配置错误，测试连接失败");
        }
    }

    /**
     * 判断所选同步账号是否正确
     *
     * @param dbIp
     * @param dbPort
     * @param exSyncmonUserinfo
     * @return
     */
    private boolean judgeUserinfo(String dbIp, Integer dbPort, ExSyncmonUserinfo exSyncmonUserinfo) {
        if (exSyncmonUserinfo != null) {
            String syncIp = exSyncmonUserinfo.getSyncdbIp();
            Integer syncPort = exSyncmonUserinfo.getSyncdbPort();
            if (StringUtils.isNotBlank(syncIp) && syncPort != null) {
                if (!syncIp.equals(dbIp) || !syncPort.equals(dbPort)) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 新增数据源
     *
     * @param databaseBO
     * @return
     */
    @Override
    public Response addDatabase(DatabaseBO databaseBO) {
        String dbDesc = databaseBO.getDbDesc();
        String dbIp = databaseBO.getDbIp();
        Integer dbPort = databaseBO.getDbPort();
        String dbUser = databaseBO.getDbUser();
        String dbPasswd = databaseBO.getDbPasswd();
        String dbName = databaseBO.getDbName();
        String dbType = databaseBO.getDbType();
        Integer userInfo = databaseBO.getUserInfo();
        Integer office = databaseBO.getOffice();
        if (StringUtils.isNotBlank(dbDesc) && StringUtils.isNotBlank(dbIp) && dbPort != null
                && StringUtils.isNotBlank(dbUser) && StringUtils.isNotBlank(dbPasswd) && StringUtils.isNotBlank(dbName)
                && StringUtils.isNotBlank(dbType) && office != null) {
            if (userInfo != null) {
                ExSyncmonUserinfo exSyncmonUserinfo = exSyncmonUserinfoService.getById(userInfo);
                if (judgeUserinfo(dbIp, dbPort, exSyncmonUserinfo)) {
                    return Response.error("请选择正确的同步账号");
                }
            }
            Integer dbId = getDbId(dbIp, dbPort, dbUser,dbName);
            if (dbId == null) {
                if (DbType.MYSQL.getDb().equalsIgnoreCase(dbType)) {
                    //如果该数据库是MYSQL类型，测试连接，如果连接成功则新增
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, dbIp, dbPort, dbName
                            , dbUser, dbPasswd, null));
                    if (dataAdapter.checkConnection(Long.valueOf(exParamService.getParamOrDefault("mysql_conn_timeout", "2000")))) {
                        exDbDictMapper.addDb(dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, userInfo, office);
                    } else {
                        return Response.error("该数据库配置错误，测试连接失败");
                    }
                } else if (DbType.ORACLE.getDb().equalsIgnoreCase(dbType)) {
                    //如果该数据库是ORACLE类型，测试连接，如果连接成功则新增
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, dbIp, dbPort, dbName
                            , dbUser, dbPasswd, null));
                    if (dataAdapter.checkConnection(Long.valueOf(exParamService.getParamOrDefault("oracle_conn_timeout", "2000")))) {
                        exDbDictMapper.addDb(dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, userInfo, office);
                    } else {
                        return Response.error("该数据库配置错误，测试连接失败");
                    }
                } else if (DbType.SQL_SERVER.getDb().equalsIgnoreCase(dbType)) {
                    //如果该数据库是SQL_SERVER类型，测试连接，如果连接成功则新增
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.SQL_SERVER, dbIp, dbPort, dbName
                            , dbUser, dbPasswd, null));
                    if (dataAdapter.checkConnection(Long.valueOf(exParamService.getParamOrDefault("sqlserver_conn_timeout", "2000")))) {
                        exDbDictMapper.addDb(dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, userInfo, office);
                    } else {
                        return Response.error("该数据库配置错误，测试连接失败");
                    }
                } else if (DbType.DB2.getDb().equalsIgnoreCase(dbType)) {
                    //如果该数据库是DB2类型，测试连接，如果连接成功则新增
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.DB2, dbIp, dbPort, dbName
                            , dbUser, dbPasswd, null));
                    if (dataAdapter.checkConnection(Long.valueOf(exParamService.getParamOrDefault("db2_conn_timeout", "2000")))) {
                        exDbDictMapper.addDb(dbDesc, dbIp, dbPort, dbUser, dbPasswd, dbName, dbType, userInfo, office);
                    } else {
                        return Response.error("该数据库配置错误，测试连接失败");
                    }
                }
            } else {
                return Response.error("该库源已存在");
            }
            List<String> allInfo = exDbDictMapper.getAllInfo(office);
            String name = repeatDemoService.getFullName(allInfo);
            Integer dbId1 = getDbId(dbIp, dbPort, dbUser,dbName);
            if (dbId1 != null) {
                Integer orgId = null;
                ExOrgdbMapping exOrgdbMapping = exOrgdbMappingService.getOne(new LambdaQueryWrapper<ExOrgdbMapping>()
                        .eq(ExOrgdbMapping::getDbId, dbId1));
                if (exOrgdbMapping != null) {
                    orgId = exOrgdbMapping.getOrgId();
                }
                if (orgId == null) {
                    //添加库所属地
                    exDbDictMapper.insertIntoOrgDbMapping(office, dbId1, name);
                }
            }
        } else {
            return Response.error("请填写完整信息！");
        }
        return Response.success("新增数据源成功");
    }


    /**
     * 获取所有数据源
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getAllDatabases() {
        //存放返回给前端信息的List
        List<Map<String, Object>> dbInfos = exDbDictMapper.getAllDatabases();
        if (!dbInfos.isEmpty()) {
            dbInfos.forEach(s -> {
                s.put("db", s.get("dbName"));
                s.put("dbName", "库描述:" + s.get("dbDesc") + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;库名:" + s.get("dbName")
                        + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(" + "IP:" + s.get("dbIp") + " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;PORT:"
                        + s.get("dbPort") + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;用户名:" + s.get("dbUser") + ")");
                s.put("dbId", s.get("dbId"));
                s.put("value", s.get("dbName"));
                s.put("userName", s.get("dbUser"));
            });
        }
        return dbInfos;
    }

    /**
     * 获取交换节点下的所有数据源
     *
     * @param gatherDesc
     * @return
     */
    @Override
    public Response getDbByDesc(String gatherDesc) {
        if (StringUtils.isNotBlank(gatherDesc)) {
            List<Map> allInfo = new ArrayList<>();
            List<Map<String, Object>> sourceTables = new ArrayList<>();
            List<Map<String, Object>> destTables = new ArrayList<>();
            List<Map> dbs = exDbDictMapper.getDbByDesc(gatherDesc);
            if (!dbs.isEmpty()) {
                for (Map db : dbs) {
                    Map<String, Object> sourceDb = new HashMap<>(16);
                    Map<String, Object> destDb = new HashMap<>(16);
                    sourceDb.put("sourceDb", db.get("sourceDb"));
                    sourceDb.put("sourceDbid", db.get("sourceDbid"));
                    destDb.put("destDb", db.get("destDb"));
                    destDb.put("destDbid", db.get("destDbid"));
                    ExDbDict sourceExDbDict = exDbDictService.getOne(new LambdaQueryWrapper<ExDbDict>()
                            .eq(ExDbDict::getDbId, db.get("sourceDbid")));
                    ExDbDict destExDbDict = exDbDictService.getOne(new LambdaQueryWrapper<ExDbDict>()
                            .eq(ExDbDict::getDbId, db.get("destDbid")));
                    //如果源库是MYSQL类型
                    if (sourceExDbDict != null && StringUtils.isNotBlank(sourceExDbDict.getDbIp()) && sourceExDbDict.getDbPort() != null
                            && StringUtils.isNotBlank(sourceExDbDict.getDbName()) && StringUtils.isNotBlank(sourceExDbDict.getDbUser())
                            && StringUtils.isNotBlank(sourceExDbDict.getDbPasswd()) && StringUtils.isNotBlank(sourceExDbDict.getDbType())) {
                        if ("MYSQL".equals(sourceExDbDict.getDbType())) {
                            repeatDemoService.mysqlField(sourceTables, sourceExDbDict);
                        } else if ("ORACLE".equals(sourceExDbDict.getDbType())) {
                            repeatDemoService.oracleField(sourceTables, sourceExDbDict);
                        }
                    } else {
                        return Response.error("连接数据库信息错误，请联系管理员");
                    }
                    //如果目标库是MYSQL类型
                    if (StringUtils.isNotBlank(destExDbDict.getDbIp()) && destExDbDict.getDbPort() != null
                            && StringUtils.isNotBlank(destExDbDict.getDbName()) && StringUtils.isNotBlank(destExDbDict.getDbUser())
                            && StringUtils.isNotBlank(destExDbDict.getDbPasswd()) && StringUtils.isNotBlank(destExDbDict.getDbType())) {
                        if ("MYSQL".equals(destExDbDict.getDbType())) {
                            repeatDemoService.mysqlField(destTables, destExDbDict);
                        } else if ("ORACLE".equals(destExDbDict.getDbType())) {
                            repeatDemoService.oracleField(destTables, destExDbDict);
                        }
                    } else {
                        return Response.error("连接数据库信息错误，请联系管理员");
                    }
                    sourceDb.put("sourceTables", sourceTables);
                    destDb.put("destTables", destTables);
                    allInfo.add(sourceDb);
                    allInfo.add(destDb);
                }
            } else {
                return Response.error("无法获取库信息，请联系管理员");
            }
            return Response.success(allInfo);
        } else {
            return Response.error("无法获取交换节点信息，请联系管理员");
        }
    }

    /**
     * 获取源库和目标库
     *
     * @param gatherId
     * @return
     */
    @Override
    public Map getSourceDbsAndDestDbs(Integer gatherId) {
        //获取源库名
        List<Map<String, Object>> dbs = exDbDictMapper.getDbByGatherDesc(gatherId);
        List<Map<String, Object>> sourceDbInfos = new ArrayList<>();
        List<Map<String, Object>> destDbInfos = new ArrayList<>();
        Map map = new HashMap(16);
        Map<String, Object> destDb = new HashMap<>(16);
        if (!dbs.isEmpty()) {
            for (Map<String, Object> db : dbs) {
                Map<String, Object> sourceDb = new HashMap<>(16);
                Integer sourceDbId = (Integer) db.get("sourceDbId");
                sourceDb.put("sourceDbName", db.get("sourceDbName"));
                sourceDb.put("sourceIp", db.get("sourceIp"));
                sourceDb.put("sourcePort", db.get("sourcePort"));
                sourceDb.put("sourceUser", db.get("sourceUser"));
                sourceDb.put("sourceDbId", sourceDbId);
                sourceDbInfos.add(sourceDb);
            }
        }
        destDbInfos.add(destDb);
        map.put("sourceDb", sourceDbInfos);
        map.put("destDb", destDbInfos);
        return map;
    }

    @Override
    public List<String> getUserByDbDesc(String dbDesc) {
        List userList = new ArrayList();
        if (StringUtils.isNotBlank(dbDesc)) {
            List<String> users = new ArrayList<>();
            List<ExDbDict> exDbDict = exDbDictService.list(new LambdaQueryWrapper<ExDbDict>().eq(ExDbDict::getDbDesc, dbDesc));
            if (!exDbDict.isEmpty()) {
                users = exDbDict.stream().map(ExDbDict::getDbUser).collect(Collectors.toList());
            }
            if (!users.isEmpty()) {
                for (String user : users) {
                    Map<String, Object> userInfo = new HashMap<>(16);
                    userInfo.put("text", user);
                    userInfo.put("value", user);
                    userList.add(userInfo);
                }
            }
        }
        return userList;
    }

    /**
     * 通过IP，端口，用户名获取库id
     *
     * @param dbIp
     * @param dbPort
     * @param dbUser
     * @return
     */
    private Integer getDbId(String dbIp, Integer dbPort, String dbUser, String dbName) {
        Integer getDbId = null;
        ExDbDict exDbDict = exDbDictService.getOne(new LambdaQueryWrapper<ExDbDict>()
                .eq(ExDbDict::getDbIp, dbIp)
                .eq(ExDbDict::getDbPort, dbPort)
                .eq(ExDbDict::getDbUser, dbUser)
                .eq(ExDbDict::getDbName, dbName));
        if (exDbDict != null) {
            getDbId = exDbDict.getDbId();
        }
        return getDbId;
    }

    @Override
    public Integer getTotleByName(String dbName, Page page) {
        return exDbDictMapper.getTotleByName(dbName);
    }

    @Override
    public Integer getTotle() {
        return exDbDictMapper.getTotle();
    }

    @Override
    public List<DbDetails> getDb(Page<DbDetails> page1) {
        return exDbDictMapper.getDb(page1);
    }

    @Override
    public ExDbDict getDbInfo(Integer dbId) {
        return exDbDictMapper.getDbInfo(dbId);
    }

    @Override
    public List<Map<String, Object>> getDbByGatherDesc(Integer gatherDesc) {
        return exDbDictMapper.getDbByGatherDesc(gatherDesc);
    }

    @Override
    public Integer getSyncIds(Integer dbId, String text) {
        return exDbDictMapper.getSyncIds(dbId, text);
    }

    @Override
    public String getInfo(Integer dbId) {
        return exDbDictMapper.getInfo(dbId);
    }

    @Override
    public DbDetails getOneInfo(Integer dbId) {
        return exDbDictMapper.getOneInfo(dbId);
    }

    @Override
    public CharSequence getDbId(String dbDesc) {
        return exDbDictMapper.getDbIdByDesc(dbDesc);
    }

}
