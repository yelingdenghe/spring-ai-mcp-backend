package com.yeling.service;

import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

/**
 * @author 夜凌
 * @ClassName ChatService
 * @Date 2025/9/29 08:36
 * @Version 1.0
 */
public interface ChatService {
    
    /**
     * @description: 测试大模型交互聊天
     * @author: 夜凌
     * @date: 2025/9/30 10:25
     * @param: [prompt]
     * @return: java.lang.String
     **/
    String chatTest(String prompt);

    /**
     * @description: 测试大模型交互聊天-流式传输
     * @author: 夜凌
     * @date: 2025/9/30 10:23
     * @param: [prompt]
     * @return: reactor.core.publisher.Flux<org.springframework.ai.chat.model.ChatResponse>
     **/
    Flux<String> steamString(String prompt);
}
