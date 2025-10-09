package com.yeling.service.impl;

import com.yeling.service.MultiModelService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName MultiModelServiceImpl
 * @Date 2025/10/9 08:57
 * @Version 1.0
 */
public class MultiModelServiceImpl implements MultiModelService {
    @Resource
    @Qualifier("deepseekClient")
    private ChatClient deepseekClient;

    @Resource
    @Qualifier("qwenClient")
    private ChatClient qwenClient;

    @Resource
    @Qualifier("qwenImageClient")
    private OpenAiImageModel qwenImageClient;

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

    @Override
    public OpenAiImageModel getImageClient(String modelName) {
        if ("qwen".equalsIgnoreCase(modelName)) {
            return qwenImageClient;
        }
        return null; // DeepSeek 没有图像模型
    }
}
