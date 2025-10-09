package com.yeling.controller;

import com.yeling.service.FluxImageService;
import com.yeling.service.ImageService;
import com.yeling.service.MultiModelService;
import jakarta.annotation.Resource;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ImageController
 * @Date 2025/10/9 08:09
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/image")
class ImageController {

    @Resource
    private FluxImageService fluxImageService;

    @GetMapping(value = "/test")
    public ResponseEntity<byte[]> imageTest(@RequestParam String query) {
        byte[] imageBytes = fluxImageService.generateImage(query);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }

}
