package com.sdy.dataexchange.biz.model.BO;

import lombok.Data;

@Data
public class DbMappingBO {
    private Integer id;
    private String gatherDesc;
    private String sourceDb;
    private String sourceUser;
    private String destDb;

}
