package com.sdy.dataexchange.gen;

import com.baomidou.mybatisplus.annotation.DbType;
import com.sdy.dataexchange.gen.s.GenConfig;
import com.sdy.dataexchange.gen.s.S;

/**
 * Created by zzq on 2018/5/11.
 */
public class Generator {

    // 数据库连接
 /*   private DbType dbType = DbType.ORACLE;
    private String driverName = "oracle.jdbc.driver.OracleDriver";
    private String dbUrl = "jdbc:oracle:thin:@192.168.1.132:1521:ORCL";
    private String dbName = "TEXT";
    private String dbPwd = "123456";*/
    
    private DbType dbType = DbType.MYSQL;
    private String driverName = "com.mysql.cj.jdbc.Driver";
    private String dbUrl = "jdbc:mysql://192.168.1.95:3306/dataexchange?serverTimezone=Asia/Shanghai&useSSL=false&useUnicode=true&characterEncoding=UTF-8";
    private String dbName = "usetest";
    private String dbPwd = "test1234";
    
    // 作者
    private String author = "wyy";
    // 包信息
    private String projectName = "data-exchange";
    private String packageName = "com.sdy.dataexchange";
    private String vuePath = "./static-vue";
    private Boolean restStyle = true;
    private String ide = "idea"; // idea eclipse

    public static void main(String[] args) {
        new Generator().generateCode("ex_monitor_mysql");
    }
    
    public void generateCode(String... tables) {
        new S().generateByTables(
                new GenConfig()
                        .setDbType(dbType)
                        .setDriverName(driverName)
                        .setAuthor(author)
                        .setDbName(dbName)
                        .setDbPwd(dbPwd)
                        .setDbUrl(dbUrl)
                        .setPackageName(packageName)
                        .setProjectName(projectName)
                        .setVuePath(vuePath)
                        .setRestStyle(restStyle)
                        .setIde(ide),
                tables);
    }

}
