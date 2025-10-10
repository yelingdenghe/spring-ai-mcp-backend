package com.yeling.controller;

import com.yeling.service.AsyncImageService;
import com.yeling.service.MultiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ImageController
 * @Date 2025/10/9 08:09
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/image")
@Slf4j
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

    @PostMapping("desc")
    public void imageDesc(@RequestParam("query") String query,
                                  @RequestParam("file") MultipartFile file,
                                  @RequestParam("userId") String userId,
                                  @RequestParam("botMsgId") String botMsgId) throws IOException {
        asyncImageService.doImage(query, file, userId, botMsgId);
    }
}
