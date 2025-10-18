package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 夜凌
 * @Description: 评估结果实体类
 * @ClassName EvaluationResult
 * @Date 2025/10/18
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResult {
    
    /**
     * 是否通过评估
     */
    private boolean passed;
    
    /**
     * 评估分数（0.0 - 1.0）
     */
    private float score;
    
    /**
     * 评估反馈信息
     */
    private String feedback;
    
    /**
     * 用户问题
     */
    private String userQuestion;
    
    /**
     * AI 响应
     */
    private String aiResponse;
    
    /**
     * 上下文文档
     */
    private String contextDocuments;
}

