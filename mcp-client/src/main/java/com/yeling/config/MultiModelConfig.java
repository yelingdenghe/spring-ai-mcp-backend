package com.yeling.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName MultiModelConfig
 * @Date 2025/10/8 20:53
 * @Version 1.0
 */
@Configuration
public class MultiModelConfig {

    @Value("${spring.ai.openai.api-key}")
    private String deepseekKey;

    @Value("${spring.ai.openai.base-url}")
    private String deepseekUrl;

    @Value("${spring.ai.openai.chat.options.model}")
    private String deepseekModel;

    @Value("${spring.ai.qwen.api-key}")
    private String qwenKey;

    @Value("${spring.ai.qwen.base-url}")
    private String qwenUrl;

    @Value("${spring.ai.qwen.chat.options.model}")
    private String qwenModel;

    private final ChatMemory chatMemory = MessageWindowChatMemory.builder().build();

    /**
     * 默认 DeepSeek
     */
    @Bean("deepseekClient")
    public ChatClient deepseekClient(ToolCallbackProvider tools) {
        OpenAiApi api = OpenAiApi.builder()
                .baseUrl(deepseekUrl)
                .apiKey(deepseekKey)
                .build();
        OpenAiChatModel model = OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(deepseekModel)
                        .temperature(0.5)
                        .build())
                .build();
        return ChatClient.builder(model)
                .defaultToolCallbacks(tools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("你是一个聪明的AI助手，名字叫夜凌（DeepSeek）")
                .build();
    }

    /**
     * Qwen 模型
     */
    @Bean("qwenClient")
    public ChatClient qwenClient(ToolCallbackProvider tools) {
        OpenAiApi api = OpenAiApi.builder()
                .baseUrl(qwenUrl)
                .apiKey(qwenKey)
                .build();

        OpenAiChatModel model = OpenAiChatModel.builder()
                .openAiApi(api)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(qwenModel)
                        .temperature(0.7)
                        .build())
                .build();
        return ChatClient.builder(model)
                .defaultToolCallbacks(tools)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("你是一个聪明的AI助手，名字叫夜凌（Qwen）")
                .build();
    }
}