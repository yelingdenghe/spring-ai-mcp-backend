package com.yeling.service.impl;

import com.yeling.service.ImageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ImageServiceImpl
 * @Date 2025/10/9 08:00
 * @Version 1.0
 */
@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Resource(name = "qwenImageClient")
    private OpenAiImageModel qwenImageClient;

    @Override
    public byte[] imageTest(String query) {
        log.info("开始生成图片");
        log.info("请求：{}", query);
        ImagePrompt imagePrompt = new ImagePrompt(query); // 构建提示词
        log.info("提示词：{}", imagePrompt);
        ImageResponse response = qwenImageClient.call(imagePrompt); // 调用模型
        log.info("响应：{}", response);
        String b64Json = response.getResult().getOutput().getB64Json(); // 获取图片数据
        log.info("图片数据：{}", b64Json);
        return Base64.getDecoder().decode(b64Json);
    }
}
