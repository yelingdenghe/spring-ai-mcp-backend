package com.yeling.evaluation;

/**
 * @author 夜凌
 * @Description: 评估器接口
 * @ClassName Evaluator
 * @Date 2025/10/18
 * @Version 1.0
 */
@FunctionalInterface
public interface Evaluator {
    
    /**
     * 执行评估
     * 
     * @param evaluationRequest 评估请求
     * @return 评估响应
     */
    EvaluationResponse evaluate(EvaluationRequest evaluationRequest);
}

