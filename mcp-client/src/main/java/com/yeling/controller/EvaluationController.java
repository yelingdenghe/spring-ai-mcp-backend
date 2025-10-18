package com.yeling.controller;

import com.yeling.entity.EvaluationResult;
import com.yeling.evaluation.EvaluationResponse;
import com.yeling.service.EvaluationService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author 夜凌
 * @Description: 模型评估控制器
 * @ClassName EvaluationController
 * @Date 2025/10/18
 * @Version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Resource
    private EvaluationService evaluationService;

    /**
     * 评估 RAG 流程的相关性
     * 
     * @param request 评估请求，包含用户问题、AI 响应、上下文文档
     * @return 评估结果
     */
    @PostMapping("/rag/relevancy")
    public LeeResult evaluateRagRelevancy(@RequestBody com.yeling.entity.EvaluationRequest request) {
        log.info("收到 RAG 相关性评估请求");
        
        try {
            EvaluationResponse response = evaluationService.evaluateRagRelevancy(
                    request.getUserQuestion(),
                    request.getAiResponse(),
                    request.getContextDocuments()
            );

            // 构建返回结果
            EvaluationResult result = new EvaluationResult(
                    response.isPass(),
                    response.getScore(),
                    response.toString(),
                    request.getUserQuestion(),
                    request.getAiResponse(),
                    request.getContextDocuments()
            );

            return LeeResult.ok(result);
        } catch (Exception e) {
            log.error("评估过程中发生错误", e);
            return LeeResult.errorMsg("评估失败: " + e.getMessage());
        }
    }
}

