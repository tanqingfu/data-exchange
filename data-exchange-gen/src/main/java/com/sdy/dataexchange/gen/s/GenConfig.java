package com.sdy.dataexchange.gen.s;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by zzq on 2018/5/28.
 */
@Data
@Accessors(chain = true)
public class GenConfig {

    // 模块
    private String module;
    // 数据库连接
    private String dbUrl;
    private String dbName;
    private String dbPwd;
    // Author
    private String author;
    // Package
    private String projectName;
    private String packageName;
    private String driverName;
    private DbType dbType;
    private String vuePath;
    private Boolean restStyle;
    private String ide;
}
