package com.wf.ew.common.config;

import org.jasig.cas.client.util.AbstractCasFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangfan on 2019-01-04 下午 3:40.
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {
    // 更换CAS中的session中的key
    public final static String SESSION_KEY = AbstractCasFilter.CONST_CAS_ASSERTION;

    // 普通登录SESSION
    public final static String SESSION_LOGIN = "SESSION_LOGIN";
    // 支持跨域访问
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").maxAge(3600)
                .allowedOrigins("*")  //支持跨域请求
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                .allowedHeaders("Content-Type, x-requested-with, X-Custom-Header, Authorization");
    }

    /**
     * 跨域请求拦截器。如项目中需要进行拦截跨域的请求时可进行配置
     * /
//    private CorsConfiguration addcorsConfig() {
//        CorsConfiguration corsConfiguration = new CorsConfiguration();
//        List<String> list = new ArrayList<>();
//        list.add("*");
//        corsConfiguration.setAllowedOrigins(list);
//    /*
//    // 请求常用的三种配置，*代表允许所有，当时你也可以自定义属性（比如header只能带什么，只能是post方式等等）
//    */
//        corsConfiguration.addAllowedOrigin("*");
//        corsConfiguration.addAllowedHeader("*");
//        corsConfiguration.addAllowedMethod("*");
//        return corsConfiguration;
//    }
    // 支持PUT请求
    @Bean
    public HttpPutFormContentFilter httpPutFormContentFilter() {
        return new HttpPutFormContentFilter();
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(new OptionsInterceptor()).addPathPatterns("/**");
        InterceptorRegistration addInterceptor = registry.addInterceptor(getSecurityInterceptor());
        //设定匹配的优先级

//        addInterceptor.excludePathPatterns("/error");
        addInterceptor.excludePathPatterns("/user/**");
        addInterceptor.excludePathPatterns("/common/**");
        addInterceptor.addPathPatterns("/**");
    }

    @Bean
    public OptionsInterceptor getSecurityInterceptor() {
        return new OptionsInterceptor();
    }
}