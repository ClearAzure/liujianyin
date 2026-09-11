package com.music.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//注册拦截器
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    //构造器注入
    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override//重写Spring MVC 注册拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)//注册jwt拦截器,把 jwtInterceptor 注册到 Spring MVC 中
                .addPathPatterns("/api/**")//JwtInterceptor 要拦截所有 /api/ 开头的请求
                .excludePathPatterns("/api/user/login", "/api/user/register");//;例外
    }
}
