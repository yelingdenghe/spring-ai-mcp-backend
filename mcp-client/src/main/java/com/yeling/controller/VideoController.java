package com.yeling.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yeling.entity.FrameRequest;
import com.yeling.entity.TaskStatusResponse;
import com.yeling.entity.VideoRequest;
import com.yeling.service.video.FrameService;
import com.yeling.service.video.VideoService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName VideoController
 * @Date 2025/10/12 16:56
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/video")
public class VideoController {

    @Resource
    private VideoService videoService;

    @Resource
    private FrameService frameService;

    @PostMapping("/generate")
    public LeeResult generateVideo(@RequestBody VideoRequest videoRequest) throws JsonProcessingException {
        String videoUrl = videoService.generateVideo(videoRequest);
        if (videoUrl != null) {
            return LeeResult.ok(videoUrl);
        } else {
            return LeeResult.errorMsg("视频生成失败");
        }
    }

    @PostMapping("/submitGenerateAsync")
    public LeeResult generateVideoAsync(@RequestParam("prompt") String prompt,
                                        @RequestParam("model") String model,
                                        @RequestParam("firstFrame") MultipartFile firstFrame,
                                        @RequestParam("lastFrame") MultipartFile lastFrame) {
        // 手动构建 FrameRequest 对象
        FrameRequest frameRequest = new FrameRequest(firstFrame, lastFrame, model, prompt);

        try {
            String taskId = frameService.asyncCall(frameRequest);
            if (taskId != null) {
                return LeeResult.ok(taskId); // 立即返回任务ID
            } else {
                return LeeResult.errorMsg("任务提交失败");
            }
        } catch (RuntimeException e) {
            return LeeResult.errorMsg("任务提交异常: " + e.getMessage());
        }
    }

    // 新增：查询任务状态
    @GetMapping("/task-status/{taskId}")
    public LeeResult getTaskStatus(@PathVariable String taskId) {
        TaskStatusResponse result = frameService.getTaskResult(taskId);
        return LeeResult.ok(result);
    }
}
