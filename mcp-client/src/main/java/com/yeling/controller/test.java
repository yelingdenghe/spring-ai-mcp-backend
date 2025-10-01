package com.yeling.controller;

import com.yeling.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author 夜凌
 * @ClassName test
 * @Date 2025/9/28 20:04
 * @Version 1.0
 */
@RestController
@RequestMapping("hello")
class test {

    @Resource
    private ChatService chatService;

    @GetMapping("chat")
    public String Hello(@RequestParam(value = "message", defaultValue = "讲个笑话") String message) {
        return chatService.chatTest(message);
    }

    @GetMapping("chat/response")
    public Flux<String> HelloResponse(@RequestParam(value = "message", defaultValue = "讲个笑话") String message,
                                      HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        return chatService.steamString(message);
    }
}
