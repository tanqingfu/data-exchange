package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *库表信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class DbNameResult {
    private  String dbDesc;
    private Integer syncId;
    private String dbTable;
    private String createTime;
    private String modifyTime;
    private String validFlag;
}
