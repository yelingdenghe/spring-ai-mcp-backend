package com.yeling.service.audio.impl;

import com.alibaba.dashscope.audio.asr.translation.TranslationRecognizerParam;
import com.alibaba.dashscope.audio.asr.translation.TranslationRecognizerRealtime;
import com.alibaba.dashscope.audio.asr.translation.results.TranslationRecognizerResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.gson.Gson;
import com.yeling.service.audio.RealTimeAsrService;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName RealTimeAsrServiceImpl
 * @Date 2025/10/17 17:39
 * @Version 1.0
 */
@Service
@Slf4j
public class RealTimeAsrServiceImpl implements RealTimeAsrService {

    @Value("${spring.ai.qwen.audio.api-key}")
    private String API_KEY;

    // 使用 ConcurrentHashMap 来管理每个 WebSocket Session 对应的识别客户端
    private final ConcurrentHashMap<String, TranslationRecognizerRealtime> recognizerClients = new ConcurrentHashMap<>();

    @Override
    public void onOpen(Session session) {
        log.info("WebSocket连接已打开: {}", session.getId());
        try {
            initRecognizer(session);
        } catch (NoApiKeyException e) {
            log.error("Failed to initialize Recognizer for session: {}. Please check if DASHSCOPE_API_KEY environment variable is set.", session.getId(), e);
            closeSession(session);
        }
    }

    @Override
    public void onMessage(Session session, byte[] audioData) {
        TranslationRecognizerRealtime recognizer = recognizerClients.get(session.getId());
        if (recognizer != null) {
            // 发送音频帧
            recognizer.sendAudioFrame(ByteBuffer.wrap(audioData));
        } else {
            log.warn("Recognizer not found for session: {}", session.getId());
        }
    }

    @Override
    public void onClose(Session session) {
        log.info("WebSocket连接已关闭: {}", session.getId());
        TranslationRecognizerRealtime recognizer = recognizerClients.remove(session.getId());
        if (recognizer != null) {
            // 停止识别并关闭连接
            recognizer.stop();
        }
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket 错误，与会话相关: {}", session.getId(), throwable);
        onClose(session); // 发生错误时也清理资源
    }

    private void initRecognizer(Session session) throws NoApiKeyException {
        // 1. 初始化参数
        TranslationRecognizerParam param = TranslationRecognizerParam.builder()
                .model("gummy-realtime-v1") // // 设置模型名
                .apiKey(API_KEY)
                .format("pcm")// // 设置待识别音频格式，支持的音频格式：pcm、wav、mp3、opus、speex、aac、amr
                .sampleRate(16000)// // 设置待识别音频采样率（单位Hz）。支持16000Hz及以上采样率。
                .transcriptionEnabled(true) // 开启识别
                .translationEnabled(true)   // 开启翻译
                .maxEndSilence(10000)  // 设置最大静音间隔，单位ms
                .translationLanguages(new String[]{"en"}) // 设置目标翻译语言
                .build();

        // 2. 初始化流式识别客户端
        TranslationRecognizerRealtime recognizer = new TranslationRecognizerRealtime();

        // 3. 定义回调
        ResultCallback<TranslationRecognizerResult> callback = new ResultCallback<>() {
            @Override
            public void onEvent(TranslationRecognizerResult result) {
                // 识别和翻译结果都在这里处理
                if (result.getTranscriptionResult() != null) {
                    String text = result.getTranscriptionResult().getText();
                    log.info("实时转录会话 {}: {}", session.getId(), text);
                    sendTextToClient(session, new Gson().toJson(Map.of("type", "transcription", "text", text))); // 将识别结果发回前端

                }
                if (result.getTranslationResult() != null && result.getTranslationResult().getTranslation("en") != null) {
                    String translatedText = result.getTranslationResult().getTranslation("en").getText();
                    log.info("实时翻译会话 {}: {}", session.getId(), translatedText);
                    // 你也可以选择将翻译结果发回前端，这里为了演示清晰，只发送了识别结果
                    sendTextToClient(session, new Gson().toJson(Map.of("type", "translation", "text", translatedText)));
                }
            }

            @Override
            public void onComplete() {
                log.info("会话识别完成: {}", session.getId());
            }

            @Override
            public void onError(Exception e) {
                log.error("会话的识别错误: {}", session.getId(), e);
                // 发生错误时关闭会话
                closeSession(session);
            }
        };

        // 4. 启动识别
        recognizer.call(param, callback);
        recognizerClients.put(session.getId(), recognizer);
    }

    private void sendTextToClient(Session session, String text) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(text);
            }
        } catch (IOException e) {
            log.error("Failed to send message to client for session: {}", session.getId(), e);
        }
    }

    private void closeSession(Session session) {
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (IOException ex) {
            log.error("Failed to close session {}", session.getId(), ex);
        }
    }
}
