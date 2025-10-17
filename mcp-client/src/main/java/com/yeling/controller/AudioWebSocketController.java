package com.yeling.controller;

import com.yeling.service.audio.RealTimeAsrService;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName AudioWebSocketController
 * @Date 2025/10/17 17:57
 * @Version 1.0
 */
@Component
@ServerEndpoint("/api/audio/realtime")
public class AudioWebSocketController {

    private static RealTimeAsrService realTimeAsrService;

    @Autowired
    public void setRealTimeAsrService(RealTimeAsrService realTimeAsrService) {
        AudioWebSocketController.realTimeAsrService = realTimeAsrService;
    }

    @OnOpen
    public void onOpen(Session session) {
        realTimeAsrService.onOpen(session);
    }

    @OnMessage
    public void onMessage(Session session, byte[] audioData) {
        realTimeAsrService.onMessage(session, audioData);
    }

    @OnClose
    public void onClose(Session session) {
        realTimeAsrService.onClose(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        realTimeAsrService.onError(session, throwable);
    }
}