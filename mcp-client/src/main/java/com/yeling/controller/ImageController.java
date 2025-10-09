package com.yeling.controller;

import com.yeling.service.AsyncImageService;
import jakarta.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private AsyncImageService asyncImageService;

    @GetMapping(value = "/generate")
    public ResponseEntity<byte[]> imageTest(@RequestParam String query, @RequestParam String model) {
        byte[] imageBytes = asyncImageService.generateImage(query, model);
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }

}
