package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 夜凌
 * @Description: 评估请求实体类
 * @ClassName EvaluationRequest
 * @Date 2025/10/18
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    
    /**
     * 用户的原始问题
     */
    private String userQuestion;
    
    /**
     * AI 模型的响应
     */
    private String aiResponse;
    
    /**
     * 从向量数据库检索的上下文文档
     */
    private String contextDocuments;
}

