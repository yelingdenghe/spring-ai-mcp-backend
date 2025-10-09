package com.yeling.service.impl;

import cn.hutool.json.JSONUtil;
import com.yeling.entity.ChatEntity;
import com.yeling.entity.ChatResponseEntity;
import com.yeling.entity.SearchResult;
import com.yeling.enums.SSEMsgType;
import com.yeling.service.ChatService;
import com.yeling.service.MultiModelService;
import com.yeling.service.SearXngService;
import com.yeling.utils.SSEServe;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
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

    @Resource
    private SearXngService searXngService;

    @Resource
    private MultiModelService multiModelService;

    @Resource(name = "deepseekClient")
    private ChatClient deepseekClient;

    /** 获取对应模型的 ChatClient */
    private ChatClient getClient(String modelName) {
        return multiModelService.getChatClient(modelName);
    }

    /** {@inheritDoc} */
    @Override
    public String chatTest(String prompt) {
        // 使用 ChatClient 的链式 API 进行调用，代码更简洁
        return deepseekClient.prompt()
                .user(prompt) // 设置用户输入
                .call()       // 发起调用
                .content();   // 直接获取返回的文本内容
    }

    /** {@inheritDoc} */
    @Override
    public Flux<String> steamString(String prompt) {
        return deepseekClient.prompt()
                .user(u -> u
                    .text(prompt))
                .stream()
                .content();
    }

    /** {@inheritDoc} */
    @Override
    public void doChat(ChatEntity chat) {
        ChatClient chatClient = getClient(chat.getModelName());

        String userName = chat.getCurrentUserName();
        String prompt = chat.getMessage();
        String botMsgId = chat.getBotMsgId();

        Flux<String> content = chatClient.prompt().user(prompt).stream().content();

        List<String> list = content.toStream().peek(chatResponse -> {
            SSEServe.sendMsg(userName, SSEMsgType.ADD, chatResponse);
            log.info("content: {}", chatResponse);
        }).toList();

        String collect = String.join("", list);

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(collect, botMsgId);

        SSEServe.sendMsg(userName, SSEMsgType.FINISH, JSONUtil.toJsonStr(chatResponseEntity));
    }

    private static final String ragPROMPT = """
                基于上下文的知识库内容回答问题：
                【上下文】
                {context}
            
                【问题】
                {question}
            
                【输出】
                如果没有查到，请回复：不知道。
                如果查到，请回复具体的内容，不相关的近似内容不用提到.
            """;


    /** {@inheritDoc} */
    @Override
    public void doChatRagSearch(ChatEntity chat, List<Document> ragContext) {
        ChatClient chatClient = getClient(chat.getModelName());

        String userName = chat.getCurrentUserName();
        String question = chat.getMessage();
        String botMsgId = chat.getBotMsgId();

        // 构建提示词
        String context = null;
        if (ragContext!=null && !ragContext.isEmpty()) {
            context = ragContext.stream()
                    .map(Document::getText)
                    .collect(Collectors.joining("\n"));
        }

        // 组装提示词
        Prompt prompt = new Prompt(ragPROMPT
                .replace("{context}", context)
                .replace("{question}", question));
        System.out.println(prompt);

        Flux<String> content = chatClient.prompt(prompt).stream().content();

        List<String> list = content.toStream().peek(chatResponse -> {
            SSEServe.sendMsg(userName, SSEMsgType.ADD, chatResponse);
            log.info("content: {}", chatResponse);
        }).toList();

        String collect = String.join("", list);

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(collect, botMsgId);

        SSEServe.sendMsg(userName, SSEMsgType.FINISH, JSONUtil.toJsonStr(chatResponseEntity));

    }

    private static final String searXngPROMPT = """
                你是一个互联网搜索大师，请基于以下互联网返回的结果作为上下文，请根据你的理解结合用户的提问综合后，生成并且输出专业的回答：
                【上下文】
                {context}
            
                【问题】
                {question}
            
                【输出】
                如果没有查到，请回复：不知道。
                如果查到，请回复具体的内容.
            """;

    /** {@inheritDoc} */
    @Override
    public void doInternetSearch(ChatEntity chat) {
        ChatClient chatClient = getClient(chat.getModelName());

        String userName = chat.getCurrentUserName();
        String question = chat.getMessage();
        String botMsgId = chat.getBotMsgId();


        // 构建提示词
        List<SearchResult> searchResults = searXngService.search(question);
        String searXngPROMPT = buildSearXngPrompt(question, searchResults);

        // 组装提示词
        Prompt prompt = new Prompt(searXngPROMPT);

        System.out.println(prompt);

        Flux<String> content = chatClient.prompt(prompt).stream().content();

        List<String> list = content.toStream().peek(chatResponse -> {
            SSEServe.sendMsg(userName, SSEMsgType.ADD, chatResponse);
            log.info("content: {}", chatResponse);
        }).toList();

        String collect = String.join("", list);

        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(collect, botMsgId);

        SSEServe.sendMsg(userName, SSEMsgType.FINISH, JSONUtil.toJsonStr(chatResponseEntity));
    }

    private static String buildSearXngPrompt(String question, List<SearchResult> searchResults) {

        StringBuffer context = new StringBuffer();

        searchResults.forEach(searchResult -> {
            context.append(
                    String.format("<context>\n[来源] %s \n[摘要] %s \n</context>\n",
                            searchResult.getUrl(),
                            searchResult.getContent()));
        });

        return searXngPROMPT
                .replace("{context}", context)
                .replace("{question}", question);
    }
}