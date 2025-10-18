package com.yeling.evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 夜凌
 * @Description: 评估响应
 * @ClassName EvaluationResponse
 * @Date 2025/10/18
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    
    /**
     * 是否通过评估
     */
    private boolean pass;
    
    /**
     * 评估分数（0.0 - 1.0）
     */
    private float score;
    
    /**
     * 评估反馈信息
     */
    private String feedback;
    
    /**
     * 原始响应文本
     */
    private String rawResponse;
    
    @Override
    public String toString() {
        return String.format("评估结果: %s | 分数: %.2f | 反馈: %s", 
                pass ? "通过" : "未通过", score, feedback);
    }
}

