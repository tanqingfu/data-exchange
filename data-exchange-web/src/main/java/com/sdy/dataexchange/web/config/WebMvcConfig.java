package com.sdy.dataexchange.web.config;

import com.sdy.dataexchange.web.interceptor.BizInterceptor;
import com.sdy.common.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Date;

/**
 * Created by zzq on 2019/1/24.
 */
@Configuration
//@EnableSpringDataWebSupport // 支持controller参数通过主键绑定数据库对象(通过findById)
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private BizInterceptor bizInterceptor;

    /**
     * 一些web配置
     *
     * @return
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        /**
         * 添加拦截器
         */
        return new WebMvcConfigurer() {
            /**
             * 拦截器配置
             * @param registry
             */
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(bizInterceptor)
                        .addPathPatterns("/**")
                        .excludePathPatterns("/opt/file/**", "/js/**", "/css/**")
                        .order(31);
            }

            /**
             * CORS跨域配置
             * @param registry
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH");
            }

            /**
             * 静态资源配置
             * @param registry
             */
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/static/**", "/opt/file/**")
                        .addResourceLocations("classpath:/webapp/public/static/", "file:/opt/file/");
            }
        };
    }

    /**
     * 表单String字段转Date
     *
     * @return
     */
    @Bean
    public Converter<String, Date> dateConvert() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(String s) {
                //转换日期
                if (s.length() == 19) {
                    return DateUtil.getDate(s, DateUtil.DATETIME_FORMAT);
                } else if (s.length() == 10) {
                    return DateUtil.getDate(s, DateUtil.DATE_FORMAT);
                } else {
                    return null;
                }
            }
        };
    }
}
