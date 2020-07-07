package com.sdy.dataexchange.biz.model.BO;

import lombok.Data;

@Data
public class FieldBO {
    private Integer sourceDbId;
    private String sourceTableName;
    private Integer  destDbId;
    private String destTableName;

}
