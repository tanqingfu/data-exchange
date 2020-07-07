package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.DbDetails;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.mvc.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
public interface ExDbDictMapper extends BaseMapper<ExDbDict> {
    /**
     * 通过库id获取摸个库的信息
     * @param dbId
     * @return ExDbDict
     */
    DbDetails getOneInfo(@Param("dbId") Integer dbId);
    /**
     * 通过库描述获取库id
     * @param dbDesc
     * @return CharSequence
     */
    CharSequence getDbIdByDesc(String dbDesc);

    /**
     * 更新数据库信息
     * @param dbId
     * @param dbDesc
     * @param dbIp
     * @param dbPort
     * @param dbUser
     * @param dbPasswd
     * @param dbName
     * @param dbType
     * @param office
     * @param name
     * @return
     */
    Integer updateDbInfo(@Param("dbId")Integer dbId,@Param("dbDesc") String dbDesc,@Param("dbIp") String dbIp
            ,@Param("dbPort") Integer dbPort,@Param("dbUser") String dbUser, @Param("dbPasswd")String dbPasswd
            ,@Param("dbName") String dbName, @Param("dbType")String dbType,@Param("office") Integer office
            ,@Param("name") String name,@Param("userInfo") String userInfo);

    /**
     * 获取库总量
     * @return
     */
    Integer getTotle();

    /**
     * 获取库详情
     * @param page1
     * @return
     */
    List<DbDetails> getDb(Page<DbDetails> page1);

    /**
     * 将库源信息加入到中间库
     *
     * @param dbDesc
     * @param dbIp
     * @param dbPort
     * @param dbUser
     * @param dbPasswd
     * @param dbName
     * @param dbType
     * @param userInfo
     * @param office
     */
    void addDb(@Param("dbDesc") String dbDesc, @Param("dbIp") String dbIp, @Param("dbPort") Integer dbPort
            , @Param("dbUser") String dbUser, @Param("dbPasswd") String dbPasswd, @Param("dbName") String dbName
            , @Param("dbType") String dbType, @Param("userInfo") Integer userInfo, @Param("office") Integer office);
    /**
     * 通过id获取库信息
     *
     * @param id
     * @return List<String>
     */
    List<String> getAllInfo(@Param("id") Integer id);
    /**
     * 获取所属省市区id
     *
     * @param office
     * @return Integer
     */
    Integer getOrgId(@Param("office") Integer office);
    /**
     * 通过库ip，端口获取库id
     *
     * @param dbIp
     * @param dbPort
     * @return Integer
     */
    Integer getDbId(@Param("dbIp") String dbIp, @Param("dbPort") Integer dbPort);
    /**
     * 将所属地id，库id，所属地名称加入ex_orgdb_mapping
     *
     * @param office
     * @param dbId
     * @param name
     */
    void insertIntoOrgDbMapping(@Param("office") Integer office, @Param("dbId") Integer dbId, @Param("name") String name);
    /**
     * 获取所有库名称
     *
     * @return List<String>
     */
    List<String> getAllDbDesc();

    /**
     * 通过库描述获取ip，端口
     * @param dbDesc
     * @return
     */
    Map getIpAndPortByDbDesc(@Param("dbDesc") String dbDesc);

    /**
     * 通过描述，ip，端口获取库id
     * @param dbDesc
     * @param dbIp
     * @param dbPort
     * @return
     */
    Integer getDbIdBbDescIpPort(@Param("dbDesc") String dbDesc, @Param("dbIp") String dbIp,@Param("dbPort") Integer dbPort);

    /**
     * 通过库id和表名获取表id
     * @param dbId
     * @param text
     * @return  Integer
     */
    Integer getSyncIds(@Param("dbId")Integer dbId, @Param("text")String text);

    /**
     * 得到库的所属地信息
     * @param dbId
     * @return String
     */
    String getInfo(Integer dbId);
    /**
     * 获取所有库信息
     * @return  List<Map<String,Object>>
     */
    List<Map<String,Object>> getAllDatabases();

    /**
     * 通过交换节点名称获取所有库
     * @param gatherDesc
     */
    List<Map> getDbByDesc(@Param("gatherDesc") String gatherDesc);
    /**
     * 通过库id获取库信息
     * @param dbId
     */
    ExDbDict getDbInfo(@Param("dbId") Integer dbId);

    /**
     * /通过交换节点id获取源库目标库
     * @param gatherDesc
     * @return
     */
    List<Map<String,Object>> getDbByGatherDesc(Integer gatherDesc);

    /**
     * 通过库名获取用户
     * @param dbDesc
     */
    List<String> getUserByDbDesc(@Param("dbDesc") String dbDesc);

    /**
     * 通过库名获取库总数
     * @param dbName
     * @return
     */
    Integer getTotleByName(@Param("dbName") String dbName);
}
