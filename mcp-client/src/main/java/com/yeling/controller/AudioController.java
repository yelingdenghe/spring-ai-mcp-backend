package com.yeling.controller;

import com.yeling.entity.AsrUrlRequest;
import com.yeling.service.audio.AsrService;
import com.yeling.service.audio.TTSService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/tts")
    public ResponseEntity<byte[]> generate(@RequestParam String content, 
                                          @RequestParam String model,
                                          HttpServletRequest request) {
        try {
            byte[] audioBytes = ttsService.tts(content, model);
            long fileSize = audioBytes.length;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf("audio/wav"));
            headers.add(HttpHeaders.ACCEPT_RANGES, "bytes");
            
            // 检查是否有Range请求头
            String rangeHeader = request.getHeader(HttpHeaders.RANGE);
            
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                try {
                    // 解析Range请求
                    List<HttpRange> ranges = HttpRange.parseRanges(rangeHeader);
                    
                    if (!ranges.isEmpty()) {
                        HttpRange range = ranges.get(0);
                        long start = range.getRangeStart(fileSize);
                        long end = range.getRangeEnd(fileSize);
                        long contentLength = end - start + 1;
                        
                        // 提取指定范围的字节
                        byte[] rangeBytes = new byte[(int) contentLength];
                        System.arraycopy(audioBytes, (int) start, rangeBytes, 0, (int) contentLength);
                        
                        // 设置206 Partial Content响应头
                        headers.add(HttpHeaders.CONTENT_RANGE, 
                                   String.format("bytes %d-%d/%d", start, end, fileSize));
                        headers.setContentLength(contentLength);
                        
                        log.debug("处理Range请求: bytes={}-{}/{}", start, end, fileSize);
                        
                        return ResponseEntity
                                .status(HttpStatus.PARTIAL_CONTENT)
                                .headers(headers)
                                .body(rangeBytes);
                    }
                } catch (IllegalArgumentException e) {
                    log.warn("无效的Range请求头: {}", rangeHeader, e);
                    // Range请求无效，返回完整内容
                }
            }
            
            // 没有Range请求或Range请求无效，返回完整内容
            headers.setContentLength(fileSize);
            headers.setContentDispositionFormData("attachment", "generated_audio.wav");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(audioBytes);
        } catch (Exception e) {
            log.error("TTS生成失败", e);
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
