package com.yeling.service.audio.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.AudioParameters;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.yeling.service.audio.TTSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName TTSServiceImpl
 * @Date 2025/10/9 17:05
 * @Version 1.0
 */
@Service
@Slf4j
public class TTSServiceImpl implements TTSService {
    @Value("${spring.ai.qwen.audio.api-key}")
    private String API_KEY;

    @Override
    public byte[] tts(String content, String model) throws ApiException, NoApiKeyException, UploadFileException {
        String modelId;
        if ("qwen".equalsIgnoreCase(model)) {
            modelId = "qwen-tts-latest";
        } else {
            throw new IllegalArgumentException("不支持的模型：" + model);
        }
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .model(modelId) // 模型ID
                .apiKey(API_KEY) // API密钥
                .text(content) // 文本
                .voice(AudioParameters.Voice.CHELSIE) // 语音类型
                .languageType("Chinese") // 建议与文本语种一致，以获得正确的发音和自然的语调。
                .build();

        MultiModalConversationResult result = conv.call(param);
        String audioUrl = result.getOutput().getAudio().getUrl();
        log.info("Generated audio URL: {}", audioUrl);

        // 下载音频文件并返回字节数组
        try (InputStream in = new URL(audioUrl).openStream();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            log.info("Audio file downloaded successfully.");
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
