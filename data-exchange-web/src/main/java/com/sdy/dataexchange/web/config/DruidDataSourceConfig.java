package com.sdy.dataexchange.web.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.sdy.mvc.utils.DruidManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by zzq on 2019/6/12.
 */
@Configuration
public class DruidDataSourceConfig {
    @Value("${jdbc.url}")
    private String url;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${jdbc.driver}")
    private String driver;

    @Autowired
    private DruidManager druidManager;

    /**
     * Druid 数据源 sql监控
     * @return
     */
    @Bean
    public DruidDataSource dataSource() {
        return druidManager.createDruidDataSource(url, driver, username, password);
    }
}
