package com.yeling.controller;

import com.yeling.entity.EvaluationResult;
import com.yeling.service.RagEvaluationService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author 夜凌
 * @Description: RAG 流程评估控制器
 * @ClassName RagEvaluationController
 * @Date 2025/10/18
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/rag/evaluation")
public class RagEvaluationController {

    @Resource
    private RagEvaluationService ragEvaluationService;

    /**
     * 执行完整的 RAG 流程并评估结果
     * 包括：文档检索、AI 生成响应、相关性评估
     * 
     * @param request RAG 评估请求
     * @return 评估结果
     */
    @PostMapping("/perform")
    public LeeResult performRagWithEvaluation(@RequestBody RagEvaluationRequest request) {
        log.info("收到 RAG 评估请求 - 问题: {}, 模型: {}", request.getQuestion(), request.getModelName());
        
        try {
            EvaluationResult result = ragEvaluationService.performRagWithEvaluation(
                    request.getQuestion(),
                    request.getModelName()
            );

            return LeeResult.ok(result);
        } catch (Exception e) {
            log.error("RAG 评估过程中发生错误", e);
            return LeeResult.errorMsg("RAG 评估失败: " + e.getMessage());
        }
    }

    /**
     * RAG 评估请求实体
     */
    @Data
    public static class RagEvaluationRequest {
        /**
         * 用户问题
         */
        private String question;
        
        /**
         * 使用的模型名称（deepseek、qwen、zhipu）
         */
        private String modelName = "deepseek";
    }
}

