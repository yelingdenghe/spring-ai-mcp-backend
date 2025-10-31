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
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.yeling.entity.OperatorSummary;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.Message;
import java.util.ArrayList;

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

    // 搜索服务
    @Resource
    private SearXngService searXngService;

    // 多模型服务
    @Resource
    private MultiModelService multiModelService;

    // 默认模型
    @Resource(name = "deepseekClient")
    private ChatClient deepseekClient;

    /** 获取对应模型的 ChatClient */
    private ChatClient getClient(String modelName) {
        return multiModelService.getChatClient(modelName);
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

    // 知识库搜索提示词
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
    public void  doChatRagSearch(ChatEntity chat, List<Document> ragContext) {
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

    // 联网搜索提示词
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

    /**
     * @description: 构建联网搜索提示词
     * @author: 夜凌
     * @date: 2025/10/30 15:18
     * @param: [question, searchResults]
     * @return: String
     **/
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

    /** {@inheritDoc} */
    @Override
    public void doOperatorSearch(ChatEntity chat) {
        String userName = chat.getCurrentUserName();
        String botMsgId = chat.getBotMsgId();

        try {
            log.info("【SSE模式】开始结构化查询干员: {}", chat.getMessage());

            // 1. [Stream] 立即发送一个 "正在查询" 的占位符消息
            // 这样前端会立刻显示 "正在查询..."，而不是空白
            SSEServe.sendMsg(userName, SSEMsgType.ADD, "正在查询【" + chat.getMessage() + "】的档案，请稍候...");

            // 2. [Block] 调用我们现有的结构化方法 (这会花费几秒钟，包含Tool call + AI总结)
            OperatorSummary summary = this.getStructuredOperatorInfo(chat);

            // 3. [Block] 格式化为漂亮的文本
            String formattedText = this.formatSummaryAsText(summary);

            // 4. [Stream] 发送 "FINISH" 消息
            // 您的前端JS逻辑应该会用 FINISH 消息的完整内容 替换掉 "正在查询..." 的占位符
            ChatResponseEntity finalResponse = new ChatResponseEntity(formattedText, botMsgId);
            SSEServe.sendMsg(userName, SSEMsgType.FINISH, JSONUtil.toJsonStr(finalResponse));

            log.info("【SSE模式】结构化查询发送完毕, botMsgId: {}", botMsgId);

        } catch (Exception e) {
            log.error("【SSE模式】结构化查询失败，将错误信息流式返回", e);
            // 降级：如果结构化失败，就发送一个错误消息
            String errorMsg = "抱歉，查询【" + chat.getMessage() + "】的档案时出错。请确保 mcp-server 已启动并正确配置。错误: " + e.getMessage();
            ChatResponseEntity errorResponse = new ChatResponseEntity(errorMsg, botMsgId);
            SSEServe.sendMsg(userName, SSEMsgType.FINISH, JSONUtil.toJsonStr(errorResponse));
        }
    }

    /**
     * @description: 获取结构化查询干员信息
     * @author: 夜凌
     * @date: 2025/10/30 15:17
     * @param: [chatEntity]
     * @return: OperatorSummary
     **/
    public OperatorSummary getStructuredOperatorInfo(ChatEntity chatEntity) {
        log.info("调用方法 getStructuredOperatorInfo");

        // 1. 复用现有的多模型服务
        ChatClient chatClient = this.multiModelService.getChatClient(chatEntity.getModelName());

        // 2. 构造一个“智能”的提示词 (Prompt)
        String userMessage = "请查询干员 " + chatEntity.getMessage() + " 的详细档案信息。" +
                "你必须使用 'queryOperator' 工具来获取数据。" +
                "获取数据后，请**不要**直接返回原始JSON，" +
                "而是要根据工具返回的**所有**信息（特别是'档案文本'），" +
                "帮我总结并严格按照我要求的 " + OperatorSummary.class.getSimpleName() + " 格式返回。";

        // 3. 创建一个临时的、Spring AI 兼容的 history
        List<Message> springAiHistory = new ArrayList<>();
        springAiHistory.add(new UserMessage(userMessage));

        Prompt prompt = new Prompt(springAiHistory); // 使用 Spring AI 兼容的 History

        // 4. 发起调用，并使用 .entity() 来指定结构化输出
        OperatorSummary summary = chatClient.prompt(prompt)
                .call()
                .entity(OperatorSummary.class);

        log.info("【SSE模式】结构化查询成功，返回结果: {}", summary);

        return summary;
    }

    /**
     * @description: 格式化干员文本
     * @author: 夜凌
     * @date: 2025/10/30 15:17
     * @param: [summary]
     * @return: String
     **/
    public String formatSummaryAsText(OperatorSummary summary) {
        log.info("【SSE模式】开始格式化结构化查询结果为文本");
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("### 干员档案【%s】\n\n", summary.name())); // Markdown 三级标题

        sb.append("**基础档案 (Base Profile)**\n"); // Markdown 加粗
        sb.append(String.format("- **性别**: %s\n", summary.baseInfo().gender()));
        sb.append(String.format("- **种族**: %s\n", summary.baseInfo().race()));
        sb.append(String.format("- **身高**: %s\n", summary.baseInfo().height()));
        sb.append(String.format("- **出身地**: %s\n", summary.baseInfo().birthplace()));
        sb.append(String.format("- **战斗经验**: %s\n\n", summary.baseInfo().combatExperience()));

        sb.append("**客观履历 (Resume)**\n");
        // 使用 Markdown 引用块 >
        sb.append(String.format("> %s\n\n", summary.resume().replaceAll("\n", "\n> ")));

        sb.append("**综合体检 (Physical Exam)**\n");
        sb.append(String.format("- **物理强度**: %s\n", summary.physicalExam().physicalStrength()));
        sb.append(String.format("- **战场机动**: %s\n", summary.physicalExam().mobility()));
        sb.append(String.format("- **生理耐受**: %s\n", summary.physicalExam().physiologicalEndurance()));
        sb.append(String.format("- **战术规划**: %s\n", summary.physicalExam().tacticalPlanning()));
        sb.append(String.format("- **战斗技巧**: %s\n", summary.physicalExam().combatSkill()));
        sb.append(String.format("- **源石技艺适应性**: %s\n\n", summary.physicalExam().artsAdaptability()));

        sb.append("**临床诊断 (Clinical Analysis)**\n");
        sb.append(String.format("- **感染情况**: %s\n", summary.infectionInfo().statusDesc()));
        sb.append(String.format("- **体细胞与源石融合率**: %s\n", summary.infectionInfo().fusionRate()));
        sb.append(String.format("- **血液源石结晶密度**: %s\n", summary.infectionInfo().crystalDensity()));
        sb.append(String.format("- **分析**: %s\n", summary.infectionInfo().analysisSummary()));

        return sb.toString();
    }

}