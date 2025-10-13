package com.yeling.service.video.impl;

import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.yeling.entity.FrameRequest;
import com.yeling.entity.TaskStatusResponse;
import com.yeling.service.video.FrameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName FrameServiceImpl
 * @Date 2025/10/13 09:14
 * @Version 1.0
 */
@Service
@Slf4j
public class FrameServiceImpl implements FrameService {

    @Value("${spring.ai.qwen.video.api-key}")
    private String qwenKey;

    // 定义轮询间隔时间（毫秒）
    private static final long POLLING_INTERVAL_MS = 5000; // 5秒

    // 定义任务超时时间（毫秒）
    private static final long TASK_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(20); // 20分钟
    @Override
    public String asyncCall(FrameRequest frameRequest) {
        Path firstFrameTemp = null;
        Path lastFrameTemp = null;
        MultipartFile firstFrame = frameRequest.getFirstFrame();
        MultipartFile lastFrame = frameRequest.getLastFrame();
        try {
            // 1. 使用统一的前缀 "frame_" 创建临时文件
            firstFrameTemp = Files.createTempFile("frame_", "_" + firstFrame.getOriginalFilename());
            Files.copy(firstFrame.getInputStream(), firstFrameTemp, StandardCopyOption.REPLACE_EXISTING);

            lastFrameTemp = Files.createTempFile("frame_", "_" + lastFrame.getOriginalFilename());
            Files.copy(lastFrame.getInputStream(), lastFrameTemp, StandardCopyOption.REPLACE_EXISTING);

            // 2. 构造符合SDK要求的文件路径格式
            String firstFrameUrl = "file:///" + firstFrameTemp.toAbsolutePath().toString().replace("\\", "/");
            String lastFrameUrl = "file:///" + lastFrameTemp.toAbsolutePath().toString().replace("\\", "/");


            // 创建视频处理参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("prompt_extend", true);

            // 创建视频合成工具
            VideoSynthesis videoSynthesis = new VideoSynthesis();

            // 创建视频合成参数
            VideoSynthesisParam param = VideoSynthesisParam.builder()
                    .apiKey(qwenKey)
                    .model(frameRequest.getModel())
                    .prompt(frameRequest.getPrompt())
                    .firstFrameUrl(firstFrameUrl)
                    .lastFrameUrl(lastFrameUrl)
                    .parameters(parameters)
                    .build();

            log.info("提交异步任务...");
            VideoSynthesisResult result = videoSynthesis.asyncCall(param);
            String taskId = result.getOutput().getTaskId();
            log.info("任务提交成功，任务ID：{}", taskId);

            return taskId;
        } catch (ApiException | NoApiKeyException | InputRequiredException | IOException e) {
            // 提交失败时，最好还是立即清理一下，避免留下垃圾文件
            try {
                if (firstFrameTemp != null) Files.deleteIfExists(firstFrameTemp);
                if (lastFrameTemp != null) Files.deleteIfExists(lastFrameTemp);
            } catch (IOException cleanupException) {
                log.error("提交失败后清理临时文件也失败", cleanupException);
            }
            throw new RuntimeException("视频生成任务提交失败: " + e.getMessage());
        }
    }

    @Override
    public TaskStatusResponse getTaskResult(String taskId) {
        try {
            VideoSynthesis videoSynthesis = new VideoSynthesis();
            VideoSynthesisResult pollResult = videoSynthesis.fetch(taskId, qwenKey);
            String taskStatus = pollResult.getOutput().getTaskStatus();
            log.info("轮询任务ID: {}, 当前状态: {}", taskId, taskStatus);

            return switch (taskStatus) {
                case "SUCCEEDED" -> TaskStatusResponse.success(taskId, pollResult.getOutput().getVideoUrl());
                case "FAILED", "CANCELED" -> TaskStatusResponse.failed(taskId, pollResult.getOutput().getMessage());
                default -> // PENDING, RUNNING
                        TaskStatusResponse.running(taskId);
            };
        } catch (ApiException | NoApiKeyException e) {
            log.error("轮询任务失败，任务ID: {}", taskId, e);
            return TaskStatusResponse.failed(taskId, e.getMessage());
        }
    }

}
