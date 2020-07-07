package com.sdy.dataexchange.biz.model.DTO;

import lombok.Data;

@Data
public class Mapping {
    private String db_id;
    private String table_id;
    private String table_name1;

    public Mapping() {
    }

    @Override
    public String toString() {
        return "Mapping{" +
                "db_id='" + db_id + '\'' +
                ", table_id='" + table_id + '\'' +
                ", table_name='" + table_name1 + '\'' +
                '}';
    }
}
