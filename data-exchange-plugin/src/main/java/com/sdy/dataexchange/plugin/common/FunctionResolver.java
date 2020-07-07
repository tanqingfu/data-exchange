package com.sdy.dataexchange.plugin.common;

public interface FunctionResolver {
    /**
     * 检查函数是否适合
     */
    boolean check(String v);

    /**
     * 解析函数
     */
    String resolve(String v);
}
