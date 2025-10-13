package com.yeling.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.yeling.entity.VideoRequest;
import com.yeling.service.video.FrameService;
import com.yeling.service.video.VideoService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/generateAsync")
    public LeeResult generateVideoAsync() {
        String videoUrl = frameService.asyncCall();
        if (videoUrl != null) {
            return LeeResult.ok(videoUrl);
        } else {
            return LeeResult.errorMsg("视频生成失败");
        }
    }
}
