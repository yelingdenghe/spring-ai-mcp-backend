package com.yeling.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 夜凌
 * @Description: CORS跨越问题解决
 * @ClassName CORSConfig
 * @Date 2025/10/1 17:43
 * @Version 1.0
 */
@Configuration
class CORSConfig implements WebMvcConfigurer {

    @Value("${website.domain}")
    private String domain;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if ("dev".equals(activeProfile)) {
            // 开发环境：允许所有源（便于本地开发）
            registry.addMapping("/**")
                    .allowedOriginPatterns("*")
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        } else {
            // 生产环境：仅允许指定域名
            registry.addMapping("/**")
                    .allowedOrigins(domain)
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
        }
    }
}
