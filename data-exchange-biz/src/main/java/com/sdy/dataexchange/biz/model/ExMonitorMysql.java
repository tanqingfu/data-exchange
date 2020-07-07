package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sdy.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.servlet.annotation.HandlesTypes;
import java.sql.Blob;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyy
 * @since 2019-10-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName(autoResultMap = true)
public class ExMonitorMysql extends BaseModel {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    private String data;

    private Integer tableId;

    private Integer dbId;
    
    @TableField(typeHandler = org.apache.ibatis.type.BlobTypeHandler.class)
    private byte[] byteData;

    /**
     * 未完成数据包标志1 默认0
     */
    private Integer nextFlag;

}
