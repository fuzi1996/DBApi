package com.gitee.freakchicken.dbapi.apiserver.config;

import com.gitee.freakchicken.dbapi.basic.filter.ApiAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 用于注册Filter
 * @program: dbApi
 * @author: kensan
 * @create: 2022-04-16 12:43
 */
@Slf4j
@Configuration
public class FilterConfig {

    private static final int DEFAULT_AUTH_FILTER_ORDER = 2;

    @Value("${dbapi.api.context}")
    private String apiContext;

    @Autowired
    private ApiAuthFilter apiAuthFilter;

    @Bean
    public FilterRegistrationBean authFilter() {
        String format = String.format("/%s/*", apiContext);
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(apiAuthFilter);
        registrationBean.addUrlPatterns(format);
        registrationBean.setOrder(DEFAULT_AUTH_FILTER_ORDER);
        registrationBean.setEnabled(true);
        log.debug("regist authFilter for {} UrlPatterns and order is {}",format,DEFAULT_AUTH_FILTER_ORDER);
        return registrationBean;
    }
}
