package com.yeling.service.video.impl;

import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesis;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisParam;
import com.alibaba.dashscope.aigc.videosynthesis.VideoSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.JsonUtils;
import com.yeling.service.video.FrameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    @Value("classpath:images/first.png")
    private Resource firstFrame;
    @Value("classpath:images/last.png")
    private Resource lastFrame;

    // 定义轮询间隔时间（毫秒）
    private static final long POLLING_INTERVAL_MS = 5000; // 5秒

    // 定义任务超时时间（毫秒）
    private static final long TASK_TIMEOUT_MS = TimeUnit.MINUTES.toMillis(20); // 20分钟
    @Override
    public String asyncCall() {
        try {
            // 创建视频处理参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("prompt_extend", true);

            // 创建视频合成工具
            VideoSynthesis videoSynthesis = new VideoSynthesis();

            // 创建首尾帧参数
            String firstFrameUrl = null;
            String lastFrameUrl = null;
            firstFrameUrl = "file:///" + firstFrame.getFile().getAbsolutePath().replace("\\", "/");
            lastFrameUrl = "file:///" + lastFrame.getFile().getAbsolutePath().replace("\\", "/");

            // 创建视频合成参数
            VideoSynthesisParam param = VideoSynthesisParam.builder()
                    .apiKey(qwenKey)
                    .model("wanx2.1-kf2v-plus")
                    .prompt("写实风格，一只黑色小猫好奇地看向天空，镜头从平视逐渐上升，最后俯拍小猫好奇的眼神。")
                    .firstFrameUrl(firstFrameUrl)
                    .lastFrameUrl(lastFrameUrl)
                    .parameters(parameters)
                    .build();

            VideoSynthesisResult result;
            log.info("异步开始中，请等待几分钟...");
            result = videoSynthesis.asyncCall(param);

            String taskId = result.getOutput().getTaskId();
            log.info("任务状态：{}", JsonUtils.toJson(result));
            log.info("任务ID：{}", taskId);


            long startTime = System.currentTimeMillis();
            while (true) {
                if (System.currentTimeMillis() - startTime > TASK_TIMEOUT_MS) {
                    log.error("任务轮询超时，任务ID: {}", taskId);
                    throw new RuntimeException("视频生成任务超时，请稍后重试。");
                }
                VideoSynthesisResult pollResult = videoSynthesis.fetch(taskId, qwenKey);
                String taskStatus = pollResult.getOutput().getTaskStatus();
                log.info("正在轮询... 任务ID: {}, 当前状态: {}", taskId, taskStatus);
                log.info("任务状态：{}", JsonUtils.toJson(result));

                // 根据任务状态进行判断
                if ("SUCCEEDED".equals(taskStatus)) {
                    log.info("任务成功完成！任务ID: {}", taskId);
                    log.info("最终任务结果: {}", JsonUtils.toJson(pollResult.getOutput()));
                    String videoUrl = pollResult.getOutput().getVideoUrl();
                    log.info("视频地址：{}", videoUrl);
                    return videoUrl; // 任务成功，返回视频URL
                } else if ("FAILED".equals(taskStatus) || "CANCELED".equals(taskStatus)) {
                    String errorMessage = pollResult.getOutput().getMessage();
                    log.error("任务失败或被取消。任务ID: {}, 状态: {}, 失败信息: {}",
                            taskId, taskStatus, errorMessage);
                    throw new RuntimeException("视频生成任务失败或被取消: " + errorMessage);
                }
                // 如果任务仍在进行中 (RUNNING, PENDING)，则等待后继续轮询
                Thread.sleep(POLLING_INTERVAL_MS);
            }
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        } catch (InputRequiredException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
