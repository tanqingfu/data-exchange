package com.sdy.dataexchange.biz.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.sdy.common.model.BaseModel;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author zzq
 * @since 2019-07-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ExIdDict extends BaseModel {
    private static final long serialVersionUID = 1L;

    /**
     * 序号
     */
    @TableId
    private Integer seqNo;

    /**
     * id串定义
     */
    private String idStr;

    /**
     * id序号
     */
    private Integer idSeqno;

    /**
     * id描述信息
     */
    private String idDesc;

}
