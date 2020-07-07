package com.sdy.dataexchange.biz.model;

import lombok.Data;
/**
 * <p>
 *字段同步信息
 * </p>
 *
 * @author 高连明
 * @since 2019-07-22
 */
@Data
public class FieldMappingResult {
    private Integer id;
    private String sourceTable;
    private String sourceSyncName;
    private String sourceFunc;
    private String destTable;
    private String destSyncName;
    private String destFunc;
    private String validFlag;

}
