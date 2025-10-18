package com.yeling.evaluation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 夜凌
 * @Description: 评估请求
 * @ClassName EvaluationRequest
 * @Date 2025/10/18
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationRequest {
    
    /**
     * 用户的原始输入文本
     */
    private String userText;
    
    /**
     * 上下文数据列表
     */
    private List<String> dataList;
    
    /**
     * AI 模型的响应内容
     */
    private String responseContent;
}

