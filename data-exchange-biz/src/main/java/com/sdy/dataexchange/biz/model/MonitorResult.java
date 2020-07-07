package com.sdy.dataexchange.biz.model;
/**
 * @author 王越洋
 * @date 2019年6月26日-上午10:06:44
 * @info
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import com.sdy.dataadapter.annotation.TableFieldAdapter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MonitorResult extends BaseModel {
    @TableId(type = IdType.AUTO)
    private String uuid;
    private String username;
    private String tableName;
    private Long scnNo;
    private Date timestampNo;
    private String sqlRedo;
    private String dealtime;
    private Integer dbId;
    private Integer tableId;
    private String rowseq;
    /**
     * 1-INSERT
     * 2-DELETE
     * 3-UPDATE
     * 255-UNSUPPORTED
     */
    private Integer operCode;
    /**
     * 0 不用处理
     * 1 char
     * 2 clob、blob类
     * 3 char和clob类
     */
    private Integer dealFlag;
    /**
     * 0-COMPLETED
     * 1-UNCOMPLETED
     */
    private Integer isComplete;
    
    @TableFieldAdapter(exist = false)
    @TableField(exist = false)
    private Boolean empty;

    /**
     * 是否忽略BLOB类型的数据
     */
    @TableFieldAdapter(exist = false)
    @TableField(exist = false)
    private Boolean noneBlob;
    
    public boolean isBlobData() {
        return (dealFlag.equals(2) || dealFlag.equals(3))
                && !operCode.equals(1) && !operCode.equals(2) && !operCode.equals(3);
    }
}
