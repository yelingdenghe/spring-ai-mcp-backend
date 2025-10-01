package com.yeling.controller;

import com.yeling.entity.ChatEntity;
import com.yeling.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author 夜凌
 * @ClassName test
 * @Date 2025/9/28 20:04
 * @Version 1.0
 */
@RestController
@RequestMapping("chat")
class ChatController {

    @Resource
    private ChatService chatService;

    @PostMapping("doChat")
    public void doChat(@RequestBody ChatEntity chat) {
        chatService.doChat(chat);
    }

}
