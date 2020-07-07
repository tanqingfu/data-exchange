package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *所有表字段信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class TableNameResult {
    private Integer syncId;
    private Integer syncSeqno;
    private String syncField;
    private String syncType;
    private String dbTable;

}
