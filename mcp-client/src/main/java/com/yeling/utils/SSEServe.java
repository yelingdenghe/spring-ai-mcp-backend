package com.yeling.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author 夜凌
 * @ClassName SSEServe
 * @Date 2025/10/1 09:13
 * @Version 1.0
 */
@Slf4j
public class SSEServe {

    // 存放所有用户
    private static final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    /**
     * @description: 连接SSE服务
     * @author: 夜凌
     * @date: 2025/10/1 09:25
     * @param: [userId]
     * @return: SseEmitter
     **/
    public static SseEmitter connect(String userId) {
        SseEmitter emitter = new SseEmitter(0L);

        emitter.onTimeout(timeOutCallback(userId));
        emitter.onCompletion(completionCallback(userId));
        emitter.onError(errorCallBack(userId));
        sseClients.put(userId, emitter);
        return emitter;

    }

    public static Runnable timeOutCallback(String userId) {
        return () -> {
            log.info("SSE timeout callback");
            remove(userId);
        };
    }

    public static Runnable completionCallback(String userId) {
        return () -> {
            log.info("SSE completion callback");
            remove(userId);
        };
    }

    public static Consumer<Throwable> errorCallBack(String userId) {
        return (throwable) -> {
            log.info("SSE error callback");
            remove(userId);
        };
    }

    public static void remove(String userId) {
        sseClients.remove(userId);
        log.info("SSE Client Removed: {}", userId);
    }


}
