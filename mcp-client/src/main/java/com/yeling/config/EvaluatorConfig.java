package com.yeling.config;

import com.yeling.evaluation.RelevancyEvaluator;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 夜凌
 * @Description: 模型评估配置类
 * @ClassName EvaluatorConfig
 * @Date 2025/10/18 
 * @Version 1.0
 */
@Configuration
public class EvaluatorConfig {

    /**
     * 创建 RelevancyEvaluator Bean，用于评估 RAG 流程的相关性
     * 使用智谱 AI 评估模型进行评估
     */
    @Bean("relevancyEvaluator")
    public RelevancyEvaluator relevancyEvaluator(@Qualifier("zhipuEvalClient") ChatClient zhipuEvalClient) {
        return new RelevancyEvaluator(zhipuEvalClient);
    }
}

