package com.yeling.service.audio;

import jakarta.websocket.Session;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName RealTimeAsrService
 * @Date 2025/10/17 17:39
 * @Version 1.0
 */
public interface RealTimeAsrService {

    /**
     * @description: 处理WebSocket连接建立
     * @author: 夜凌
     * @date: 2025/10/17 17:43
     * @param: [session]
     * @return: void
     **/
    void onOpen(Session session);

    /**
     * @description: 处理传入的音频数据
     * @author: 夜凌
     * @date: 2025/10/17 17:43
     * @param: [session, audioData] [当前会话, 音频数据]
     * @return: void
     **/
    void onMessage(Session session, byte[] audioData);

    /**
     * @description: 处理WebSocket连接关闭
     * @author: 夜凌
     * @date: 2025/10/17 17:43
     * @param: [session] [当前会话]
     * @return: void
     **/
    void onClose(Session session);

    /**
     * @description: 处理错误
     * @author: 夜凌
     * @date: 2025/10/17 17:43
     * @param: [session, throwable] [当前会话, 异常]
     * @return: void
     **/
    void onError(Session session, Throwable throwable);


}
