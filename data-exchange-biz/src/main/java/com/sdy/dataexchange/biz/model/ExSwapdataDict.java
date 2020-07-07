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
public class ExSwapdataDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 数据统计表id
     */
    @TableId
    private Integer id;
    
    private Integer jobId;
    private Integer taskId;

    /**
     * 源库ID
     */
    private Integer sourcedbId;

    /**
     * 目标库ID
     */
    private Integer destdbId;

    /**
     * 源表名
     */
    private String sourceName;

    /**
     * 目标表名
     */
    private String destName;

    /**
     * 交换的时间24小时制
     */
    private String swaData;

    /**
     * 交换数据 输入量
     */
    private Integer swaInput;

    /**
     * 交换数据 总量
     */
    private Integer swaGross;

}
