package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.ExSyncmonUserinfo;
import com.sdy.dataexchange.biz.model.UserInfo;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author wyy
 * @since 2019-08-27
 */
public interface ExSyncmonUserinfoMapper extends BaseMapper<ExSyncmonUserinfo> {
    /**
     * 通过同步账号信息
     * @param page
     * @return  List<UserInfo>
     */
    List<UserInfo> getUserInfos(@Param("startIndex")Page page);
    /**
     * 通过id获取同步账号详情
     * @param id
     * @return  UserInfo
     */
    UserInfo getUserInfoDetail(@Param("id") Integer id);
    /**
     * 删除同步账号
     * @param syncdbId
     * @return  boolean
     */
    boolean removeOne(@Param("syncdbId") Integer syncdbId);
    /**
     * 获取同步账号信息
     * @param key
     * @return  List<UserInfo>
     */
    List<UserInfo> getInfo(@Param("key") String key,@Param("page") Page page);
    /**
     * 通获取同步账号id
     * @param syncdbIp
     * @param syncdbPort
     * @return  Integer
     */
    Integer getSyncdbId(@Param("syncdbIp") String syncdbIp,@Param("syncdbPort") Integer syncdbPort);

    /**
     * 通过同步描述获取ip，端口
     * @param syncDesc
     * @return
     */
    Map getIpAndPort(String syncDesc);

    /**
     * 获取同步账号总数
     * @return
     */
    Integer getTotle();

    /**
     * g更新同步账号
     * @param exSyncmonUserinfo
     * @return
     */
    Integer updateUserInfo(@Param("exSyncmonUserinfo") ExSyncmonUserinfo exSyncmonUserinfo);

    /**
     * 通过交换节点描述获取交换节点id
     * @param gatherDesc
     * @return Integer
     */
    Integer getGatherId(@Param("gatherDesc") String gatherDesc);
    /**
     * 将同步账号信息加入到中间库中
     * @param gatherId
     * @param dbDesc
     * @param dbName
     * @param passwd
     * @param ip
     * @param port
     * @param userName
     * @param dbType
     * @param createTime
     */
    boolean addUserInfo(@Param("gatherId") Integer gatherId, @Param("dbDesc")String dbDesc, @Param("dbName")String dbName
            , @Param("passwd")String passwd, @Param("ip")String ip, @Param("port")Integer port,@Param("userName") String userName
            ,@Param("dbType") String dbType, @Param("createTime")String createTime,@Param("validFlag") Integer validFlag);
    /**
     * 获取一条同步账号信息
     * @param gatherId
     * @param dbName
     * @param passwd
     * @param ip
     * @param port
     * @param userName
     * @return ExSyncmonUserinfo
     */
    ExSyncmonUserinfo getUserInfo(@Param("gatherId") Integer gatherId,@Param("dbName") String dbName
            , @Param("passwd") String passwd,@Param("ip")  String ip, @Param("port") Integer port, @Param("userName") String userName);

    /**
     * 获取所有同步账号描述
     * @return
     */
    List<String> getUserInfoDesc();

    /**
     * 获取所有用户信息

     * @return  List<Map>
     */
    List<Map<String,Object>> getAllUserInfo();
    /**
     * 修改同步账号状态
     * @param i
     * @param syncdbId
     */
    void changeCode(@Param("i") int i,@Param("syncdbId") Integer syncdbId);
}
