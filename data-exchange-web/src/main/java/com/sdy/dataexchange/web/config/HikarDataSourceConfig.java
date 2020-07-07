package com.sdy.dataexchange.web.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Created by zzq on 2019/1/11.
 */
//@Configuration
public class HikarDataSourceConfig {
    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.driver}")
    private String driver;

    /**
     * Hikari 连接池配置
     * 详细配置请访问<a href="https://github.com/brettwooldridge/HikariCP">Hikari</a>
     *
     * @return
     */
    @Bean
    public HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        // 最小空闲连接数量
        dataSource.setMinimumIdle(5);
        // 空闲连接存活最大时间，默认600000（10分钟）
        dataSource.setIdleTimeout(180000);
        // 连接池最大连接数，默认是10
        dataSource.setMaximumPoolSize(10);
        // 此属性控制从池返回的连接的默认自动提交行为,默认值：true
        dataSource.setAutoCommit(true);
        // 连接池名字
        dataSource.setPoolName("MyHikariCP");
        // 此属性控制池中连接的最长生命周期，值0表示无限生命周期，默认1800000即30分钟
        dataSource.setMaxLifetime(1800000);
        // 数据库连接超时时间,默认30秒，即30000
        dataSource.setConnectionTimeout(60000);
        dataSource.setConnectionTestQuery("SELECT 1");
        return dataSource;
    }
}
