package com.music.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//解决“跨域能不能访问”
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")//跨域规则只应用于 /api/ 开头的接口。
                .allowedOriginPatterns("*")//允许任意来源的请求。
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")//请求头允许携带任意 Header
                .allowCredentials(true)//允许跨域请求携带凭证。
                .maxAge(3600);//浏览器可以把 CORS 的预检请求（OPTIONS）结果缓存 1 小时。
    }
}
