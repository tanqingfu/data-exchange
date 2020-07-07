package com.sdy.dataexchange.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.common.model.Response;
import com.sdy.dataadapter.DataAdapter;
import com.sdy.dataadapter.DbType;
import com.sdy.dataadapter.RawDataSource;
import com.sdy.dataexchange.biz.mapper.ExSyncmonUserinfoMapper;
import com.sdy.dataexchange.biz.model.BO.UserInfoBO;
import com.sdy.dataexchange.biz.model.ExSyncmonUserinfo;
import com.sdy.dataexchange.biz.model.UserInfo;
import com.sdy.dataexchange.biz.service.ExParamService;
import com.sdy.dataexchange.biz.service.ExSyncmonUserinfoService;
import com.sdy.mvc.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wyy
 * @since 2019-08-27
 */
@Slf4j
@Service
public class ExSyncmonUserinfoServiceImpl extends BaseServiceImpl<ExSyncmonUserinfo> implements ExSyncmonUserinfoService {
    @Autowired
    private ExSyncmonUserinfoMapper exSyncmonUserinfoMapper;
    @Autowired
    private ExSyncmonUserinfoService exSyncmonUserinfoService;
    @Autowired
    private ExParamService exParamService;

    @Override
    public List<UserInfo> getUserInfos(Page page) {
        return exSyncmonUserinfoMapper.getUserInfos(page);
    }

    @Override
    public UserInfo getUserInfoDetail(Integer id) {
        return exSyncmonUserinfoMapper.getUserInfoDetail(id);
    }

    @Override
    public boolean removeOne(Integer syncdbId) {
        return exSyncmonUserinfoMapper.removeOne(syncdbId);
    }

    @Override
    public List<UserInfo> getInfo(String key, Page page) {
        return exSyncmonUserinfoMapper.getInfo(key, page);
    }

    @Override
    public Integer getTotle() {
        return exSyncmonUserinfoMapper.getTotle();
    }

    @Override
    public List<Map<String,Object>> getAllUserInfo() {
        return exSyncmonUserinfoMapper.getAllUserInfo();
    }

    @Override
    public void changeCode(int i, Integer syncdbId) {
        exSyncmonUserinfoMapper.changeCode(i, syncdbId);
    }

    /**
     * 更新同步账号
     * @param exSyncmonUserinfo
     * @return
     */
    @Override
    public Response updateUserInfo(ExSyncmonUserinfo exSyncmonUserinfo) {
        String syncDbIp = exSyncmonUserinfo.getSyncdbIp();
        Integer syncDbPort = exSyncmonUserinfo.getSyncdbPort();
        String syncDbType = exSyncmonUserinfo.getSyncdbType();
        String syncDbName = exSyncmonUserinfo.getSyncdbName();
        String syncDbUser = exSyncmonUserinfo.getSyncdbUser();
        String syncDbPasswd = exSyncmonUserinfo.getSyncdbPasswd();
        String syncDesc = exSyncmonUserinfo.getSyncDesc();
        if (StringUtils.isNotBlank(syncDbIp) && syncDbPort != null && StringUtils.isNotBlank(syncDbType) && StringUtils.isNotBlank(syncDbName)
                && StringUtils.isNotBlank(syncDbUser) && StringUtils.isNotBlank(syncDbPasswd) && StringUtils.isNotBlank(syncDesc)){
            String modifyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            exSyncmonUserinfo.setModifyTime(modifyTime);
            Integer syncdbId = null;
            if (StringUtils.isNotBlank(exSyncmonUserinfo.getSyncdbIp()) && exSyncmonUserinfo.getSyncdbPort() != null) {
                ExSyncmonUserinfo userInfos = exSyncmonUserinfoService.getOne(new LambdaQueryWrapper<ExSyncmonUserinfo>()
                        .eq(ExSyncmonUserinfo::getSyncdbIp, exSyncmonUserinfo.getSyncdbIp())
                        .eq(ExSyncmonUserinfo::getSyncdbPort, exSyncmonUserinfo.getSyncdbPort()));
                if (userInfos != null) {
                    syncdbId = userInfos.getSyncdbId();
                }
            }
            if (syncdbId != null && !syncdbId.equals(exSyncmonUserinfo.getSyncdbId())) {
                return Response.error("该同步账号已存在！");
            } else {
                if ("MYSQL".equals(syncDbType)) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, syncDbIp, syncDbPort, syncDbName, syncDbUser, syncDbPasswd, null));
                    return updateUserInfoSuccessOrFail(exSyncmonUserinfo, dataAdapter);
                } else if ("ORACLE".equals(syncDbType)){
                    //ORACLE数据库的同步账号测试是否可用
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, syncDbIp, syncDbPort, syncDbName, syncDbUser, syncDbPasswd, null));
                    return updateUserInfoSuccessOrFail(exSyncmonUserinfo, dataAdapter);
                }else {
                    return Response.error("该数据库类型不存在");
                }
            }
        }else{
            return Response.error("请填写完整信息");
        }
    }


    /**
     * 更新同步账号成功或者失败
     * @param exSyncmonUserinfo
     * @param dataAdapter
     * @return
     */
    private Response updateUserInfoSuccessOrFail(ExSyncmonUserinfo exSyncmonUserinfo, DataAdapter dataAdapter) {
        if (dataAdapter.checkConnection(Long.valueOf(exParamService.getParamOrDefault("mysql_conn_timeout", "2000")))) {
            Integer result = exSyncmonUserinfoMapper.updateUserInfo(exSyncmonUserinfo);
            if (result!=null){
                return Response.success("同步账号更新成功");
            }else{
                return Response.error("同步账号更新失败");
            }
        } else {
            return Response.error("该同步账号配置错误，测试连接失败");
        }
    }

    /**
     * 新增同步账号
     * @param userInfoBO
     * @return
     */
    @Override
    public Response addUserInfo(UserInfoBO userInfoBO) {
        Integer gatherId = userInfoBO.getGatherId();
        String dbDesc = userInfoBO.getDbDesc();
        String dbName = userInfoBO.getDbName();
        String passwd = userInfoBO.getPasswd();
        String ip = userInfoBO.getIp();
        Integer port = userInfoBO.getPort();
        String userName = userInfoBO.getUserName();
        String dbType = userInfoBO.getDbType();
        if (gatherId != null && StringUtils.isNotBlank(dbDesc) && StringUtils.isNotBlank(dbName)
                && StringUtils.isNotBlank(passwd) && StringUtils.isNotBlank(ip) && StringUtils.isNotBlank(userName)
                && StringUtils.isNotBlank(dbType) && port != null) {
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            ExSyncmonUserinfo exSyncmonUserinfo = exSyncmonUserinfoService.getOne(new LambdaQueryWrapper<ExSyncmonUserinfo>()
                    .eq(ExSyncmonUserinfo::getSyncdbIp, ip)
                    .eq(ExSyncmonUserinfo::getSyncdbPort, port));
            if (exSyncmonUserinfo != null) {
                return Response.error("该同步账号已存在！");
            } else {
                if (DbType.MYSQL.getDb().equalsIgnoreCase(dbType)) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.MYSQL, ip, port, dbName, userName, passwd, null));
                    return addSuccessOrFail(gatherId, dbDesc, dbName, passwd, ip, port, userName, dbType, createTime, dataAdapter, "mysql_conn_timeout");
                } else if (DbType.ORACLE.getDb().equalsIgnoreCase(dbType)) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.ORACLE, ip, port, dbName, userName, passwd, null));
                    return addSuccessOrFail(gatherId, dbDesc, dbName, passwd, ip, port, userName, dbType, createTime, dataAdapter, "oracle_conn_timeout");
                } else if (DbType.SQL_SERVER.getDb().equalsIgnoreCase(dbType)) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.SQL_SERVER, ip, port, dbName, userName, passwd, null));
                    return addSuccessOrFail(gatherId, dbDesc, dbName, passwd, ip, port, userName, dbType, createTime, dataAdapter, "sqlserver_conn_timeout");
                } else if (DbType.DB2.getDb().equalsIgnoreCase(dbType)) {
                    DataAdapter dataAdapter = new DataAdapter(new RawDataSource(DbType.DB2, ip, port, dbName, userName, passwd, null));
                    return addSuccessOrFail(gatherId, dbDesc, dbName, passwd, ip, port, userName, dbType, createTime, dataAdapter, "db2_conn_timeout");
                } else {
                    return Response.error("不支持的数据库！");
                }
            }
        } else {
            return Response.error("请填写完整信息！");
        }
    }

    /**
     * 新增成功或者失败
     * @param gatherId
     * @param dbDesc
     * @param dbName
     * @param passwd
     * @param ip
     * @param port
     * @param userName
     * @param dbType
     * @param createTime
     * @param dataAdapter
     * @return
     */
    private Response addSuccessOrFail(Integer gatherId, String dbDesc, String dbName, String passwd, String ip, Integer port, String userName, String dbType, String createTime, DataAdapter dataAdapter, String timeoutKey) {
        if (dataAdapter.checkConnection(Long.valueOf(exParamService.getParamOrDefault(timeoutKey, "2000")))) {
            if(exSyncmonUserinfoMapper.addUserInfo(gatherId, dbDesc, dbName, passwd, ip, port, userName, dbType, createTime, 0)){
                return Response.success("新增同步账号成功");
            }else {
                return Response.error("新增同步账号失败");
            }
        } else {
            return Response.error("该同步账号配置错误，测试连接失败");
        }
    }
}
