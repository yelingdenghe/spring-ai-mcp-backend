package com.yeling.service.audio.impl;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.yeling.service.audio.AsrService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName AsrServiceImpl
 * @Date 2025/10/9 18:18
 * @Version 1.0
 */
@Service
@Slf4j
public class AsrServiceImpl implements AsrService {
    
    @Value("${spring.ai.qwen.audio.api-key}")
    private String API_KEY;

    @Override
    public String recognize(MultipartFile audioFile, String model) {
        // 1. DashScope SDK 需要本地文件路径，所以我们先将上传的文件保存到临时目录
        Path tempFile = null;
        try {
            String modelId;

            if ("qwen3-asr".equalsIgnoreCase(model)) {
                modelId = "qwen3-asr-flash";
            } else if ("qwen-audio".equalsIgnoreCase(model)){
                modelId = "qwen-audio-asr";
            } else {
                throw new IllegalArgumentException("不支持的模型：" + model);
            }

            tempFile = Files.createTempFile(UUID.randomUUID().toString(), ".tmp");
            Files.copy(audioFile.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Temporary audio file created at: {}", tempFile.toAbsolutePath());

            // 2. 构造符合SDK要求的文件路径格式
            String localFilePath = tempFile.toUri().toString();

            MultiModalConversation conv = new MultiModalConversation();

            // 3. 构建用户消息，包含音频文件
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap("audio", localFilePath)))
                    .build();

            // 4. 构建请求参数
            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .model(modelId)
                    .apiKey(API_KEY)
                    .messages(new ArrayList<>(Arrays.asList(userMessage)))
                    .build();

            // 5. 发起调用并获取结果
            MultiModalConversationResult result = conv.call(param);
            String recognizedText = result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
            log.info("Recognized Text: {}", recognizedText);

            return recognizedText;

        } catch (IOException | NoApiKeyException | UploadFileException e) {
            log.error("ASR recognition failed", e);
            return "语音识别失败：" + e.getMessage();
        } finally {
            // 6. 删除临时文件
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                    log.info("Temporary audio file deleted.");
                } catch (IOException e) {
                    log.error("Failed to delete temporary audio file.", e);
                }
            }
        }
    }

    @Override
    public String recognize(String audioUrl, String model) {
        String modelId;

        if ("qwen3-asr".equalsIgnoreCase(model)) {
            modelId = "qwen3-asr-flash";
        } else if ("qwen-audio".equalsIgnoreCase(model)){
            modelId = "qwen-audio-asr";
        } else {
            throw new IllegalArgumentException("不支持的模型：" + model);
        }
        try {
            log.info("Recognizing audio from URL: {}", audioUrl);
            MultiModalConversation conv = new MultiModalConversation();

            // 对于在线URL，直接在内容中使用
            MultiModalMessage userMessage = MultiModalMessage.builder()
                    .role(Role.USER.getValue())
                    .content(Arrays.asList(
                            Collections.singletonMap("audio", audioUrl)))
                    .build();

            MultiModalConversationParam param = MultiModalConversationParam.builder()
                    .model(modelId)
                    .apiKey(API_KEY)
                    .message(userMessage)
                    .build();

            MultiModalConversationResult result = conv.call(param);
            String recognizedText = result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
            log.info("Recognized Text from URL: {}", recognizedText);

            return recognizedText;

        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            log.error("ASR recognition from URL failed", e);
            return "语音识别失败：" + e.getMessage();
        }
    }
}
