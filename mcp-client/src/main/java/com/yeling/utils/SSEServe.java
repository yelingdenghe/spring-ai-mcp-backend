package com.yeling.utils;

import com.yeling.enums.SSEMsgType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collections;
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
        // 创建对象，设置超时时间，默认为30秒，0L表示永不超时，超时未完成任务则会抛出异常
        SseEmitter emitter = new SseEmitter(0L);

        // 设置超时回调
        emitter.onTimeout(timeOutCallback(userId));
        // 设置完成回调
        emitter.onCompletion(completionCallback(userId));
        // 添加错误回调
        emitter.onError(errorCallBack(userId));
        sseClients.put(userId, emitter);

        log.info("连接到用户 {}", userId);
        return emitter;
    }

    /**
     * @description: 发送SSE消息
     * @author: 夜凌
     * @date: 2025/10/30 15:45
     * @param: [userId, msgType, msg]
     * @return: void
     **/
    public static void sendMsg(String userId, SSEMsgType msgType, String msg) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }

        // 根据用户id来判断是哪个SseEmitter
        if (sseClients.containsKey(userId)) {
            SseEmitter emitter = sseClients.get(userId);
            sendEmitterMessage(emitter, userId, msg, msgType);
        }
    }

    /**
     * @description: 群发消息
     * @author: 夜凌
     * @date: 2025/10/30 15:46
     * @param: [msg] [要发送的消息]
     * @return: void
     **/
    public static void sendMsgToAllUsers(String msg) {
        if (CollectionUtils.isEmpty(sseClients)) {
            return;
        }

        // 遍历所有用户发送
        sseClients.forEach((userId, emitter) -> {
            sendEmitterMessage(emitter, userId, msg, SSEMsgType.MESSAGE);
        });
    }

    /**
     * @description: 发送给客户端SSE消息
     * @author: 夜凌
     * @date: 2025/10/30 15:46
     * @param: [sseEmitter, userId, message, msgType] [连接的客户端，用户id，发送的消息，事件类型]
     * @return: void
     **/
    private static void sendEmitterMessage(SseEmitter sseEmitter,
                                          String userId,
                                          String message,
                                          SSEMsgType msgType) {
        try {
            SseEmitter.SseEventBuilder msgEvent = SseEmitter.event()
                    .id(userId)
                    .data(message)
                    .name(msgType.type);
            sseEmitter.send(msgEvent);
        } catch (IOException e) {
            log.error("SSE错误: {}", e.getMessage());
            remove(userId);
        }
    }

    /**
     * @description: SSE超时回调
     * @author: 夜凌
     * @date: 2025/10/30 15:00
     * @param: [userId]
     * @return: Runnable
     **/
    public static Runnable timeOutCallback(String userId) {
        return () -> {
            log.warn("SSE超时");
            // 移除用户
            remove(userId);
        };
    }

    /**
     * @description: SSE完成回调
     * @author: 夜凌
     * @date: 2025/10/30 15:09
     * @param: [userId]
     * @return: Runnable
     **/
    public static Runnable completionCallback(String userId) {
        return () -> {
            log.info("SSE完成");
            // 移除用户
            remove(userId);
        };
    }

    /**
     * @description: SSE错误回调
     * @author: 夜凌
     * @date: 2025/10/30 15:09
     * @param: [userId]
     * @return: Consumer
     **/
    public static Consumer<Throwable> errorCallBack(String userId) {
        return (throwable) -> {
            log.error("SSE发生错误");
            // 移除用户
            remove(userId);
        };
    }

    /**
     * @description: 移除用户
     * @author: 夜凌
     * @date: 2025/10/30 15:09
     * @param: [userId]
     * @return: void
     **/
    public static void remove(String userId) {
        sseClients.remove(userId);
        log.info("SSE连接已被移除，用户id为: {}", userId);
    }


}
