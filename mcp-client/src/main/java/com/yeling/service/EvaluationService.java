package com.yeling.service;

import com.yeling.evaluation.EvaluationResponse;

/**
 * @author 夜凌
 * @Description: 模型评估服务接口
 * @ClassName EvaluationService
 * @Date 2025/10/18
 * @Version 1.0
 */
public interface EvaluationService {

    /**
     * 评估 RAG 流程的相关性
     * 
     * @param userQuestion 用户的原始问题
     * @param aiResponse AI 模型的响应
     * @param contextDocuments 从向量数据库检索的上下文文档
     * @return 评估结果
     */
    EvaluationResponse evaluateRagRelevancy(String userQuestion, String aiResponse, String contextDocuments);
}

