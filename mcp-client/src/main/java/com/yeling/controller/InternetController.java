package com.yeling.controller;

import com.yeling.entity.ChatEntity;
import com.yeling.service.ChatService;
import com.yeling.service.SearXngService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName InternetController
 * @Date 2025/10/5 10:33
 * @Version 1.0
 */
@RestController
@RequestMapping("internet")
class InternetController {

    @Resource
    private SearXngService searXngService;

    @Resource
    private ChatService chatService;

    @GetMapping("/test")
    public Object test(@RequestParam("query") String query) {
        return searXngService.search(query);
    }

    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chatEntity, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        chatService.doInternetSearch(chatEntity);
    }

}
