package com.yeling.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 夜凌
 * @ClassName ChatClientConfig
 * @Date 2025/9/30 10:28
 * @Version 1.0
 */

@Configuration
public class ChatClientConfig {

    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider tools) {
        return builder.defaultToolCallbacks(tools)
                .defaultSystem("你是一个聪明的ai，名字叫做夜凌")
                .build();
    }
}
