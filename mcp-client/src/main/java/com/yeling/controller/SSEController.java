package com.yeling.controller;

import com.yeling.enums.SSEMsgType;
import com.yeling.utils.SSEServe;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author 夜凌
 * @ClassName sdfsdf
 * @Date 2025/10/1 09:27
 * @Version 1.0
 */
@RestController
@RequestMapping("sse")
class SSEController {

    /**
     * @description: 前端发送连接的请求，连接SSE服务器
     * @author: 夜凌
     * @date: 2025/10/1 09:34
     * @param: [userId]
     * @return: SseEmitter
     **/
    @GetMapping(path = "connect", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter connect(@RequestParam String userId) {
        return SSEServe.connect(userId);
    }
    

    /**
     * @description: SSE发送单个消息
     * @author: 夜凌
     * @date: 2025/10/1 10:21
     * @param: [userId, message]
     * @return: Object
     **/
    @GetMapping("sendMessage")
    public Object sendMessage(@RequestParam String userId, @RequestParam String message) {
        SSEServe.sendMsg(userId, SSEMsgType.MESSAGE, message);
        return "Message sent";
    }



}
