package com.yeling.controller;

import com.yeling.service.audio.TTSService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName TTSController
 * @Date 2025/10/9 17:32
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/tts")
public class TTSController {

    @Resource
    private TTSService ttsService;

    @RequestMapping("/generate")
    public ResponseEntity<byte[]> generate(String content, String model) {
        try {
            byte[] audioBytes = ttsService.tts(content, model);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("audio/wav"));
            headers.setContentDispositionFormData("attachment", "generated_audio.wav");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(audioBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
