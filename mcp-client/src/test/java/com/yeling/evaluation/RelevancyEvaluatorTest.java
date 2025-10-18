package com.yeling.evaluation;

import com.yeling.service.DocumentService;
import com.yeling.service.EvaluationService;
import com.yeling.service.RagEvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author 夜凌
 * @Description: 相关性评估器测试类
 * @ClassName RelevancyEvaluatorTest
 * @Date 2025/10/18
 * @Version 1.0
 */
@Slf4j
@SpringBootTest
public class RelevancyEvaluatorTest {

    @Autowired
    @Qualifier("relevancyEvaluator")
    private RelevancyEvaluator relevancyEvaluator;

    @Autowired
    private EvaluationService evaluationService;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RagEvaluationService ragEvaluationService;

    /**
     * 测试相关性评估器 - 相关的响应
     */
    @Test
    public void testRelevancyEvaluator_Relevant() {
        log.info("测试相关性评估器 - 相关的响应");

        String userQuestion = "Spring AI 是什么？";
        String context = "Spring AI 是一个面向 AI 工程的应用程序框架。它提供了一个友好的 API 和抽象，用于开发 AI 应用程序。";
        String aiResponse = "Spring AI 是一个专门为 AI 工程设计的应用程序框架，它提供了友好的 API 和抽象层，帮助开发者轻松构建 AI 应用程序。";

        EvaluationRequest request = new EvaluationRequest(
                userQuestion,
                List.of(context),
                aiResponse
        );

        EvaluationResponse response = relevancyEvaluator.evaluate(request);

        log.info("评估结果: {}", response);
        
        assertNotNull(response);
        assertTrue(response.isPass(), "预期响应应该与上下文相关");
        assertEquals(1.0f, response.getScore(), "预期评分应该是 1.0");
    }

    /**
     * 测试相关性评估器 - 不相关的响应
     */
    @Test
    public void testRelevancyEvaluator_Irrelevant() {
        log.info("测试相关性评估器 - 不相关的响应");

        String userQuestion = "Spring AI 是什么？";
        String context = "Spring AI 是一个面向 AI 工程的应用程序框架。";
        String aiResponse = "今天天气不错，适合出门游玩。";

        EvaluationRequest request = new EvaluationRequest(
                userQuestion,
                List.of(context),
                aiResponse
        );

        EvaluationResponse response = relevancyEvaluator.evaluate(request);

        log.info("评估结果: {}", response);
        
        assertNotNull(response);
        assertFalse(response.isPass(), "预期响应应该与上下文不相关");
        assertEquals(0.0f, response.getScore(), "预期评分应该是 0.0");
    }

    /**
     * 测试 RAG 流程评估服务
     * 注意：此测试需要向量数据库中有数据
     */
    @Test
    public void testRagWithEvaluation() {
        log.info("测试 RAG 流程评估");

        String question = "什么是 Spring AI？";
        
        try {
            // 执行完整的 RAG 流程并评估
            com.yeling.entity.EvaluationResult result = ragEvaluationService.performRagWithEvaluation(
                    question,
                    "deepseek"
            );

            log.info("RAG 评估结果: {}", result);
            
            assertNotNull(result);
            assertNotNull(result.getAiResponse());
            assertNotNull(result.getContextDocuments());
            
            log.info("是否通过评估: {}", result.isPassed());
            log.info("评估分数: {}", result.getScore());
            log.info("AI 响应: {}", result.getAiResponse());
            
        } catch (Exception e) {
            log.warn("测试跳过：可能是向量数据库中没有数据", e);
        }
    }

    /**
     * 测试评估服务
     */
    @Test
    public void testEvaluationService() {
        log.info("测试评估服务");

        String userQuestion = "什么是 RAG？";
        String context = "RAG（Retrieval-Augmented Generation，检索增强生成）是一种结合了信息检索和文本生成的AI技术。它通过从知识库中检索相关信息，然后将这些信息作为上下文提供给生成模型，从而提高生成内容的准确性和相关性。";
        String aiResponse = "RAG 是检索增强生成技术，它结合了信息检索和文本生成，通过从知识库检索相关信息来提高生成内容的质量。";

        EvaluationResponse response = evaluationService.evaluateRagRelevancy(
                userQuestion,
                aiResponse,
                context
        );

        log.info("评估结果: {}", response);
        
        assertNotNull(response);
        assertTrue(response.isPass());
    }
}

