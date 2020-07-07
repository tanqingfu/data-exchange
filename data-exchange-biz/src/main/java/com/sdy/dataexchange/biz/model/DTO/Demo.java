package com.sdy.dataexchange.biz.model.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class Demo {
    private Integer rid;
    private String name;
    private Date create_time;

    public Demo() {
    }

    @Override
    public String toString() {
        return "Demo{" +
                "rid=" + rid +
                ", name='" + name + '\'' +
                ", create_time=" + create_time +
                '}';
    }
}
