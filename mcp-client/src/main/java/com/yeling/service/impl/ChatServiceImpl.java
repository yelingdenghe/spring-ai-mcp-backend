package com.yeling.service.impl;

import cn.hutool.json.JSONUtil;
import com.yeling.entity.ChatEntity;
import com.yeling.entity.ChatResponseEntity;
import com.yeling.enums.SSEMsgType;
import com.yeling.service.ChatService;
import com.yeling.utils.SSEServe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 夜凌
 * @ClassName ChatServiceImpl
 * @Date 2025/9/29 08:38
 * @Version 1.0
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient chatClient) {
        // 使用 Builder 来创建一个 ChatClient 实例
        this.chatClient = chatClient;
    }

    /** {@inheritDoc} */
    @Override
    public String chatTest(String prompt) {
        // 使用 ChatClient 的链式 API 进行调用，代码更简洁
        return chatClient.prompt()
                .user(prompt) // 设置用户输入
                .call()       // 发起调用
                .content();   // 直接获取返回的文本内容
    }


    /** {@inheritDoc} */
    @Override
    public Flux<String> steamString(String prompt) {
        return chatClient.prompt()
                .user(u -> u
                    .text(prompt))
                .stream()
                .content();
    }

    /** {@inheritDoc} */
    @Override
    public void doChat(ChatEntity chat) {
        String userName = chat.getCurrentUserName();
        String prompt = chat.getMessage();
        String botMsgId = chat.getBotMsgId();

        Flux<String> content = chatClient.prompt().user(prompt).stream().content();

        List<String> list = content.toStream().map(chatResponse -> {
            String string = chatResponse.toString();
            SSEServe.sendMsg(userName, SSEMsgType.ADD, string);
            log.info("content: {}", string);
            return string;
        }).toList();

        String collect = String.join("", list);

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(collect, botMsgId);

        SSEServe.sendMsg(userName, SSEMsgType.FINISH, JSONUtil.toJsonStr(chatResponseEntity));


    }
}