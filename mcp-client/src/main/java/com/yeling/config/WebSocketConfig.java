package com.yeling.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @author 夜凌
 * @Description: 配置WebSocket
 * @ClassName WebSocketConfig
 * @Date 2025/10/17 17:38
 * @Version 1.0
 */
@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}