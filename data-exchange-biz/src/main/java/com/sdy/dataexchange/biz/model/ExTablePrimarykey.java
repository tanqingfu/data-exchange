package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzq
 * @since 2019-08-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExTablePrimarykey extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 库id
     */
    private Integer dbid;

    /**
     * 表id
     */
    private Integer tableid;

    /**
     * 字段个数
     */
    private Integer fieldNum;

    /**
     * 字段1
     */
    private String prmField1;

    /**
     * 字段2
     */
    private String prmField2;

    /**
     * 字段3
     */
    private String prmField3;

    /**
     * 字段4
     */
    private String prmField4;

    /**
     * 字段5
     */
    private String prmField5;

    /**
     * 字段6
     */
    private String prmField6;

    /**
     * 字段7
     */
    private String prmField7;

    /**
     * 字段8
     */
    private String prmField8;

    /**
     * 字段9
     */
    private String prmField9;

    /**
     * 字段10
     */
    private String prmField10;

    /**
     * 主键
     */
    @TableId
    private Integer id;

}
