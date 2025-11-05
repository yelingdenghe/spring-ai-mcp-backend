package com.yeling.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author 夜凌
 * @Description: Embedding模型预热组件
 * 在应用启动时预先加载PyTorch库和模型，避免首次请求时下载导致超时
 * @ClassName EmbeddingModelWarmup
 * @Date 2025/11/5
 * @Version 1.0
 */
@Slf4j
@Component
public class EmbeddingModelWarmup {

    private final EmbeddingModel embeddingModel;

    public EmbeddingModelWarmup(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    /**
     * 应用启动完成后执行模型预热
     * 使用ApplicationReadyEvent确保所有Bean都已初始化完成
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmupEmbeddingModel() {
        log.info("========================================");
        log.info("开始预热Embedding模型...");
        log.info("========================================");
        
        try {
            long startTime = System.currentTimeMillis();
            
            // 执行一次简单的embedding操作来触发模型加载
            // 这将会自动下载所需的PyTorch native库（如果尚未下载）
            embeddingModel.embed("预热测试文本");
            
            long endTime = System.currentTimeMillis();
            log.info("========================================");
            log.info("Embedding模型预热完成！耗时: {} 毫秒", endTime - startTime);
            log.info("PyTorch库和模型已加载到内存");
            log.info("========================================");
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("Embedding模型预热失败！请检查配置和模型文件", e);
            log.error("========================================");
            // 不抛出异常，允许应用继续启动
            // 错误会在首次实际使用时再次出现
        }
    }
}

