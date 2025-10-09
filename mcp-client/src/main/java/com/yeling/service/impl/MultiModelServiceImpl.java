package com.yeling.service.impl;

import com.yeling.service.MultiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName MultiModelServiceImpl
 * @Date 2025/10/9 08:57
 * @Version 1.0
 */
@Service
@Slf4j
public class MultiModelServiceImpl implements MultiModelService {
    @Resource
    @Qualifier("deepseekClient")
    private ChatClient deepseekClient;

    @Resource
    @Qualifier("qwenClient")
    private ChatClient qwenClient;

    @Override
    public ChatClient getChatClient(String modelName) {
        if (modelName == null || modelName.isBlank()) {
            return deepseekClient; // 默认 deepseek
        }
        return switch (modelName.toLowerCase()) {
            case "qwen" -> qwenClient;
            default -> deepseekClient;
        };
    }
}
