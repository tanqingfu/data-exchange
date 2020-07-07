package com.sdy.dataexchange.biz.model;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wyy
 * @since 2019-09-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExMetadataDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 元数据表ID
     */
    @TableId
    private Integer mdId;

    /**
     * 表ID
     */
    private Integer tabId;

    /**
     * 元数据名称
     */
    private String mdName;

    /**
     * 标识
     */
    private String mdIdent;

    /**
     * 编码
     */
    private String mdCoding;

    /**
     * 原模型名称
     */
    private String mdModel;

    /**
     * 数据库ID
     */
    private Integer originId;

    /**
     * 最新操作时间
     */
    @JsonFormat(pattern = DATETIME_FORMAT)
    private Date operationTime;

}
