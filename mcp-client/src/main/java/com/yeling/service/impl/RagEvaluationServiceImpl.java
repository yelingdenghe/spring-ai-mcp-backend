package com.yeling.service.impl;

import com.yeling.entity.EvaluationResult;
import com.yeling.evaluation.EvaluationResponse;
import com.yeling.service.DocumentService;
import com.yeling.service.EvaluationService;
import com.yeling.service.MultiModelService;
import com.yeling.service.RagEvaluationService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 夜凌
 * @Description: RAG 流程评估服务实现类
 * @ClassName RagEvaluationServiceImpl
 * @Date 2025/10/18
 * @Version 1.0
 */
@Slf4j
@Service
public class RagEvaluationServiceImpl implements RagEvaluationService {

    @Resource
    private DocumentService documentService;

    @Resource
    private MultiModelService multiModelService;

    @Resource
    private EvaluationService evaluationService;

    private static final String RAG_PROMPT = """
            基于上下文的知识库内容回答问题：
            【上下文】
            {context}
            
            【问题】
            {question}
            
            【输出】
            如果没有查到，请回复：不知道。
            如果查到，请回复具体的内容，不相关的近似内容不用提到。
            """;

    /**
     * 执行完整的 RAG 流程并评估结果
     * 
     * @param question 用户问题
     * @param modelName 使用的模型名称
     * @return 评估结果
     */
    @Override
    public EvaluationResult performRagWithEvaluation(String question, String modelName) {
        log.info("开始执行 RAG 流程，问题: {}, 模型: {}", question, modelName);

        // 1. 从向量数据库检索相关文档
        List<Document> documents = documentService.doSearch(question);
        log.info("检索到 {} 个相关文档", documents.size());

        // 2. 构建上下文
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        // 3. 获取 ChatClient 并生成响应
        ChatClient chatClient = multiModelService.getChatClient(modelName);
        
        String promptText = RAG_PROMPT
                .replace("{context}", context)
                .replace("{question}", question);
        
        Prompt prompt = new Prompt(promptText);
        
        String aiResponse = chatClient.prompt(prompt).call().content();
        log.info("AI 响应: {}", aiResponse);

        // 4. 评估 RAG 流程的相关性
        EvaluationResponse evaluationResponse = evaluationService.evaluateRagRelevancy(
                question,
                aiResponse,
                context
        );

        // 5. 构建并返回评估结果
        return new EvaluationResult(
                evaluationResponse.isPass(),
                evaluationResponse.getScore(),
                evaluationResponse.toString(),
                question,
                aiResponse,
                context
        );
    }
}

