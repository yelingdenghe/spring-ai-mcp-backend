package com.yeling.service;

import com.yeling.entity.ChatEntity;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.document.Document;
import reactor.core.publisher.Flux;

import java.util.List;

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

    /**
     * @description: 和大模型交互
     * @author: 夜凌
     * @date: 2025/10/1 19:47
     * @param: [chat]
     * @return: void
     **/
    void doChat(ChatEntity chat);

    /**
     * @description: Rag知识库检索汇总到大模型输出
     * @author: 夜凌
     * @date: 2025/10/3 15:58
     * @param: [chat, ragContext]
     * @return: void
     **/
    void doChatRagSearch(ChatEntity chat, List<Document> ragContext);
}
