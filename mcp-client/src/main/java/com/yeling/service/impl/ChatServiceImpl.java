package com.yeling.service.impl;

import com.yeling.service.ChatService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author 夜凌
 * @ClassName ChatServiceImpl
 * @Date 2025/9/29 08:38
 * @Version 1.0
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient chatClient) {
        // 使用 Builder 来创建一个 ChatClient 实例
        this.chatClient = chatClient;
    }

    /**
     * @description:
     * @author: 夜凌
     * @date: 2025/9/29 09:32
     * @param: [prompt]
     * @return: java.lang.String
     **/
    @Override
    public String chatTest(String prompt) {
        // 使用 ChatClient 的链式 API 进行调用，代码更简洁
        return chatClient.prompt()
                .user(prompt) // 设置用户输入
                .call()       // 发起调用
                .content();   // 直接获取返回的文本内容
    }
    
    @Override
    public Flux<String> steamString(String prompt) {
        return chatClient.prompt()
                .user(u -> u
                    .text(prompt))
                .stream()
                .content();
    }
}