package com.yeling.service;

import com.yeling.entity.EvaluationResult;

/**
 * @author 夜凌
 * @Description: RAG 流程评估服务接口
 * @ClassName RagEvaluationService
 * @Date 2025/10/18
 * @Version 1.0
 */
public interface RagEvaluationService {

    /**
     * 执行完整的 RAG 流程并评估结果
     * 包括：文档检索、AI 生成响应、相关性评估
     * 
     * @param question 用户问题
     * @param modelName 使用的模型名称
     * @return 评估结果，包含检索的文档、AI 响应和评估信息
     */
    EvaluationResult performRagWithEvaluation(String question, String modelName);
}

