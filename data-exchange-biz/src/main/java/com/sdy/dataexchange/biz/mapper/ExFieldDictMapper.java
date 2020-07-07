package com.sdy.dataexchange.biz.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sdy.dataexchange.biz.model.ExDbDict;
import com.sdy.dataexchange.biz.model.ExFieldDict;
import com.sdy.dataexchange.biz.model.TableNameResult;
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
public interface ExFieldDictMapper extends BaseMapper<ExFieldDict> {
    /**
     * 获取所有表信息
     * @param page
     * @return  List<TableNameResult>
     */
    List<TableNameResult> getTableName(@Param("page")Page page);
    /**
     * 获取表信息
     * @param syncSeqno
     * @return TableNameResult
     */
    TableNameResult getInfo(Integer syncSeqno);

    /**
     * 获取字段总数
     * @return
     */
    Integer getTotle();

    /**
     * 通过库id获取库信息
     * @param dbId
     * @return  List<ExDbDict>
     */
    ExDbDict getDbInfo(@Param("dbId") Integer dbId);
    /**
     * 通过库名获取库id
     * @param dbName
     * @return  List<Map>
     */
    Integer getDbId(@Param("dbName") String dbName);
    /**
     * 获取同步过的字段
     * @param sourceTableId
     * @param destTableId
     * @return  List<Map>
     */
    List<Map> getDoneFields(@Param("sourceTableId") Integer sourceTableId, @Param("destTableId") Integer destTableId);
    /**
     * 获取表id
     * @param dbId
     * @param tableName
     * @return  Integer
     */
    Integer getTableId(@Param("dbId")Integer dbId,@Param("tableName") String tableName);
}
