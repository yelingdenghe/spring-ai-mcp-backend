package com.yeling.evaluation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

/**
 * @author 夜凌
 * @Description: 相关性评估器
 * 用于评估 AI 生成响应与提供上下文的相关性
 * @ClassName RelevancyEvaluator
 * @Date 2025/10/18
 * @Version 1.0
 */
@Slf4j
public class RelevancyEvaluator implements Evaluator {

    private final ChatClient chatClient;

    /**
     * 默认的评估提示词模板
     * 与 Spring AI 官方文档中的模板保持一致
     */
    private static final String DEFAULT_EVALUATION_PROMPT = """
            你的任务是评估给定查询的响应是否与提供的上下文信息一致。
            
            你只有两个选项来回答：YES（是）或 NO（否）。
            
            如果查询的响应与上下文信息一致，请回答 YES，否则回答 NO。
            
            查询：
            {query}
            
            响应：
            {response}
            
            上下文：
            {context}
            
            回答（只能是 YES 或 NO）：
            """;

    public RelevancyEvaluator(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    /**
     * 评估响应的相关性
     *
     * @param evaluationRequest 评估请求，包含用户问题、响应和上下文
     * @return 评估响应
     */
    @Override
    public EvaluationResponse evaluate(EvaluationRequest evaluationRequest) {
        log.info("开始相关性评估");
        log.debug("用户问题: {}", evaluationRequest.getUserText());
        log.debug("AI 响应: {}", evaluationRequest.getResponseContent());

        // 构建评估提示词
        String evaluationPrompt = buildEvaluationPrompt(evaluationRequest);
        
        log.debug("评估提示词: {}", evaluationPrompt);

        // 调用评估模型
        String rawResponse = chatClient.prompt()
                .user(evaluationPrompt)
                .call()
                .content();

        log.info("评估模型原始响应: {}", rawResponse);

        // 解析评估结果
        return parseEvaluationResponse(rawResponse);
    }

    /**
     * 构建评估提示词
     */
    private String buildEvaluationPrompt(EvaluationRequest request) {
        String context = request.getDataList() != null && !request.getDataList().isEmpty()
                ? String.join("\n", request.getDataList())
                : "无上下文";

        return DEFAULT_EVALUATION_PROMPT
                .replace("{query}", request.getUserText())
                .replace("{response}", request.getResponseContent())
                .replace("{context}", context);
    }

    /**
     * 解析评估响应
     * 从模型的响应中提取 YES/NO 判断和其他信息
     */
    private EvaluationResponse parseEvaluationResponse(String rawResponse) {
        boolean pass = false;
        float score = 0.0f;
        String feedback = rawResponse;

        // 规范化响应文本
        String normalizedResponse = rawResponse.toUpperCase().trim();

        // 检查是否包含 YES 或 NO
        if (normalizedResponse.contains("YES") || normalizedResponse.contains("是")) {
            pass = true;
            score = 1.0f;
            feedback = "响应与上下文相关";
        } else if (normalizedResponse.contains("NO") || normalizedResponse.contains("否")) {
            pass = false;
            score = 0.0f;
            feedback = "响应与上下文不相关";
        } else {
            // 如果无法明确判断，尝试从响应中提取更多信息
            log.warn("无法从评估响应中明确提取 YES/NO: {}", rawResponse);
            feedback = "评估结果不明确: " + rawResponse;
        }

        log.info("解析后的评估结果 - 通过: {}, 分数: {}", pass, score);

        return new EvaluationResponse(pass, score, feedback, rawResponse);
    }
}

