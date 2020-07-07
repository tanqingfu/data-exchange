package com.sdy.dataexchange.web.utils;

import lombok.Data;

import java.util.List;

public class PageUtil {
    private int current;
    private int size;
    private int total;
    private List data;

    public PageUtil() {
    }

    @Override
    public String toString() {
        return "PageUtil{" +
                "current=" + current +
                ", size=" + size +
                ", total=" + total +
                ", data=" + data +
                '}';
    }
}
