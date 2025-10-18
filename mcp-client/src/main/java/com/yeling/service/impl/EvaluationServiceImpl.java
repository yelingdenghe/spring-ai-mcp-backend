package com.yeling.service.impl;

import com.yeling.evaluation.EvaluationRequest;
import com.yeling.evaluation.EvaluationResponse;
import com.yeling.evaluation.RelevancyEvaluator;
import com.yeling.service.EvaluationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 夜凌
 * @Description: 模型评估服务实现类
 * @ClassName EvaluationServiceImpl
 * @Date 2025/10/18
 * @Version 1.0
 */
@Slf4j
@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final RelevancyEvaluator relevancyEvaluator;

    public EvaluationServiceImpl(@Qualifier("relevancyEvaluator") RelevancyEvaluator relevancyEvaluator) {
        this.relevancyEvaluator = relevancyEvaluator;
    }

    /**
     * 评估 RAG 流程的相关性
     * 判断 AI 模型的响应是否与用户输入和检索到的上下文相关联
     *
     * @param userQuestion 用户的原始问题
     * @param aiResponse AI 模型的响应
     * @param contextDocuments 从向量数据库检索的上下文文档（字符串格式）
     * @return 评估结果，包含是否通过评估、评估分数等信息
     */
    @Override
    public com.yeling.evaluation.EvaluationResponse evaluateRagRelevancy(String userQuestion, String aiResponse, String contextDocuments) {
        log.info("开始评估 RAG 流程相关性");
        log.info("用户问题: {}", userQuestion);
        log.info("AI 响应: {}", aiResponse);
        log.info("上下文文档: {}", contextDocuments);

        // 创建评估请求
        EvaluationRequest evaluationRequest = new EvaluationRequest(
                userQuestion,              // 用户的原始问题
                List.of(contextDocuments), // 上下文数据列表
                aiResponse                 // AI 模型的响应
        );

        // 执行评估
        EvaluationResponse evaluationResponse = relevancyEvaluator.evaluate(evaluationRequest);

        log.info("评估结果: {}", evaluationResponse.isPass() ? "通过" : "未通过");
        log.info("评估分数: {}", evaluationResponse.getScore());

        return evaluationResponse;
    }
}

