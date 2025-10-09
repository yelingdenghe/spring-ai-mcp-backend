package com.yeling.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiImageModel;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName MultiModelService
 * @Date 2025/10/9 08:57
 * @Version 1.0
 */
public interface MultiModelService {
    ChatClient getChatClient(String modelName);
}
