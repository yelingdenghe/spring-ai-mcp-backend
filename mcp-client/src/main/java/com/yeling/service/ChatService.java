package com.yeling.service;

import com.yeling.entity.ChatEntity;
import com.yeling.entity.OperatorSummary;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
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
    
    /**
     * @description: 大模型处理基于SearXNG的联网搜索数据集 -- User -> SearXNG -> LLM
     * @author: 夜凌
     * @date: 2025/10/5 18:12
     * @param: [chat]
     * @return: void
     **/
    void doInternetSearch(ChatEntity chat);

    /**
     * @description: 大模型处理基于OpenAI的联网搜索关于MCP工具-明日方舟干员相关数据的数据集
     * @author: 夜凌
     * @date: 2025/10/5 18:12
     * @param: [chat]
     * @return: void
     **/
    void doOperatorSearch(ChatEntity chat);
}
