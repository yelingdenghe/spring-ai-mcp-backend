package com.yeling.controller;

import com.yeling.entity.AsrUrlRequest;
import com.yeling.service.ChatService;
import com.yeling.service.audio.AsrService;
import com.yeling.service.audio.TTSService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName AudioController
 * @Date 2025/10/9 17:32
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/audio")
@Slf4j
public class AudioController {

    @Resource
    private TTSService ttsService;

    @Resource
    private AsrService asrService;

    @Resource
    private ChatService chatService;

    @GetMapping("/tts")
    public ResponseEntity<byte[]> generate(@RequestParam String content, @RequestParam String model) {
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

    @PostMapping("/asr")
    public LeeResult recognizeAndChat(@RequestParam("file") MultipartFile file,
                                      @RequestParam("asrModel") String asrModel) {
        log.info("Received audio file for recognition, asrModel: {}", asrModel);

        // 调用 ASR 服务将语音转为文字
        String recognizedText = asrService.recognize(file, asrModel);

        if (recognizedText.startsWith("语音识别失败")) {
            return LeeResult.errorMsg(recognizedText);
        }
        // 直接返回识别后的文本
        return LeeResult.ok(recognizedText);
    }

    @PostMapping("/asr_url")
    public LeeResult recognizeUrlAndChat(@RequestBody AsrUrlRequest request) {
        log.info("Received audio URL for recognition, asrModel: {}", request.getAsrModel());
        String recognizedText = asrService.recognize(request.getAudioUrl(), request.getAsrModel());

        if (recognizedText.startsWith("语音识别失败")) {
            return LeeResult.errorMsg(recognizedText);
        }

        // 直接返回识别后的文本
        return LeeResult.ok(recognizedText);
    }

}
