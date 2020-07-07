package com.sdy.dataexchange.biz.model.DTO;

import lombok.Data;

@Data
public class TaskRowRecord {
    private String dealtime;
    private Long scnNo;
    
    public TaskRowRecord() {
        
    }

    public TaskRowRecord(String dealtime, Long scnNo) {
        this.dealtime = dealtime;
        this.scnNo = scnNo;
    }
}
