package com.yeling.controller;

import com.yeling.entity.ChatEntity;
import com.yeling.entity.OperatorSummary;
import com.yeling.service.ChatService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author 夜凌
 * @ClassName test
 * @Date 2025/9/28 20:04
 * @Version 1.0
 */
@RestController
@RequestMapping("chat")
@Slf4j
class ChatController {

    @Resource
    private ChatService chatService;

    @PostMapping("doChat")
    public void chat(@RequestBody ChatEntity chatEntity) {

        String msg = chatEntity.getMessage();

        // 简单的判断逻辑
        if (msg.contains("明日方舟") || msg.contains("档案") || msg.contains("干员") ||
                msg.contains("PRTS")) { // 可以添加更多关键词

            log.info("检测到干员查询，转交 [doOperatorSearch]...");
            // 这个方法会立即返回，并在后台通过SSE发送消息
            chatService.doOperatorSearch(chatEntity);

        } else {
            // 如果不是查询干员，则执行普通聊天
            log.info("执行普通聊天 [doChat]...");
            chatService.doChat(chatEntity);
        }
    }
}
