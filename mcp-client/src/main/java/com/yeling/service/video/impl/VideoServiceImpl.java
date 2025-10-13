package com.yeling.service.video.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.yeling.entity.VideoRequest;
import com.yeling.service.video.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName VideoServiceImpl
 * @Date 2025/10/12 16:58
 * @Version 1.0
 */
@Service
@Slf4j
public class VideoServiceImpl implements VideoService {

    @Value("${spring.ai.zhipuai.api-key}")
    private String zhipuKey;

    private static final String BASE_URL = "https://open.bigmodel.cn/api/paas/v4/";
    // 声明 RestClient 实例
    private final RestClient restClient;

    // 在构造函数中初始化 RestClient
    public VideoServiceImpl() {
        this.restClient = RestClient.builder().baseUrl(BASE_URL).build();
    }

    @Override
    public String generateVideo(VideoRequest videoRequest) {
        try {
            // 1.提交异步任务
            Map<String, Object> body = new HashMap<>();
            body.put("model", videoRequest.getModel());
            body.put("prompt", videoRequest.getPrompt());
            body.put("quality", videoRequest.getQuality());
            body.put("size", videoRequest.getSize());
            body.put("fps", videoRequest.getFps());
            body.put("with_audio", videoRequest.getWith_audio());
            body.put("watermark_enabled", videoRequest.getWatermark());

            // 使用 RestClient 发送 POST 请求
            JsonNode initialResponse = restClient.post()
                    .uri("videos/generations")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + zhipuKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);

            if (initialResponse == null || !initialResponse.has("id")) {
                throw new RuntimeException("提交视频生成任务失败: " + (initialResponse != null ? initialResponse.toPrettyString() : "null response"));
            }

            // 解析响应，获取task_id
            String taskId = initialResponse.get("id").asText();
            log.info("提交视频生成任务成功，task_id={}", taskId);

            // 2. 轮询任务状态
            while (true) {
                // 使用 RestClient 发送 GET 请求来查询任务状态
                JsonNode pollResponse = restClient.get()
                        .uri("async-result/{taskId}", taskId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + zhipuKey)
                        .retrieve()
                        .body(JsonNode.class);

                if (pollResponse == null) {
                    throw new RuntimeException("轮询任务状态失败，获得空的响应。");
                }

                // 解析状态
                String status = pollResponse.get("task_status").asText();
                log.info("当前任务状态：{}", status);

                if ("SUCCESS".equals(status)) {
                    JsonNode videoResultNode  = pollResponse.get("video_result");
                    if (videoResultNode != null && videoResultNode.isArray() && !videoResultNode.isEmpty()) {
                        String videoUrl = videoResultNode.get(0).get("url").asText();
                        log.info("任务成功，视频地址：{}", videoUrl);
                        return videoUrl;
                    } else {
                        throw new RuntimeException("视频生成成功，但未找到 video_result 或其内容为空。");
                    }
                } else if ("FAILED".equals(status)) {
                    throw new RuntimeException("视频生成失败：" + pollResponse.toPrettyString());
                }

                // 等待5秒再进行下一次查询
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (Exception e) {
            log.error("视频生成异常：", e);
            return null;
        }
    }
}
