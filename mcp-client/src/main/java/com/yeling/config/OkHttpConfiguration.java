package com.yeling.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName OkHttpConfiguration
 * @Date 2025/10/5 10:18
 * @Version 1.0
 */
@Configuration
class OkHttpConfiguration implements WebMvcConfigurer {

    @Value("${ok.http.read-timeout}")
    private int readTimeout;

    @Value("${ok.http.write-timeout}")
    private int writeTimeout;

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout,TimeUnit.SECONDS)
                .build();
    }
}
