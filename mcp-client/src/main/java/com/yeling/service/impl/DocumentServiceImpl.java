package com.yeling.service.impl;

import com.yeling.service.DocumentService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName DocumentServiceImpl
 * @Date 2025/10/2 11:36
 * @Version 1.0
 */
@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {

    private final RedisVectorStore redisVectorStore;
    private final ChatModel chatModel;


    public DocumentServiceImpl(RedisVectorStore redisVectorStore,
                               @Qualifier("deepseekChatModel") ChatModel chatModel) {
        this.redisVectorStore = redisVectorStore;
        this.chatModel = chatModel;
    }

    @Override
    public void uploadText(Resource resource, String fileName) {

        String fileExtension = StringUtils.getFilenameExtension(fileName);

        // 对于需要文件路径的Reader，我们需要先将上传的资源保存到临时文件
        if ("md".equalsIgnoreCase(fileExtension)) {
            Path tempFile = null;
            try {
                // 1. 创建一个临时文件
                tempFile = Files.createTempFile("upload-", "-" + fileName);
                // 2. 将上传文件的内容复制到临时文件中
                Files.copy(resource.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

                // 3. 配置MarkdownDocumentReaderConfig
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true) // 是否根据水平线创建文档块
                        .withIncludeCodeBlock(false) // 是否包含代码块
                        .withIncludeBlockquote(false) // 是否包含引用
                        .build();

                // 4. 使用临时文件的URI来实例化MarkdownDocumentReader
                MarkdownDocumentReader markdownReader = new MarkdownDocumentReader(tempFile.toUri().toString(), config);
                List<Document> documents = markdownReader.get();

                // 5. 执行ETL处理
                processAndLoadDocuments(documents, fileName);

            } catch (IOException e) {
                throw new RuntimeException("处理Markdown临时文件时出错: " + fileName, e);
            } finally {
                // 5. 确保在最后删除临时文件
                if (tempFile != null) {
                    try {
                        Files.deleteIfExists(tempFile);
                    } catch (IOException e) {
                        log.error("无法删除临时文件: {}", tempFile, e);
                    }
                }
            }
        } else if ("json".equalsIgnoreCase(fileExtension)) {
            // 定义需要提取到元数据的特定字段
            List<String> metadataKeys = List.of
                    ("id", "technology_name", "field", "inventor", "launch_date", "description", "time", "desc");

            // 创建一个自定义的元数据生成器
            JsonMetadataGenerator metadataGenerator = (jsonMap) -> {
                Map<String, Object> metadata = new HashMap<>();
                for (String key : metadataKeys) {
                    if (jsonMap.containsKey(key)) {
                        metadata.put(key, jsonMap.get(key));
                    }
                }
                return metadata;
            };

            JsonReader jsonReader = new JsonReader(resource, metadataGenerator);
            List<Document> documents = jsonReader.get();
            log.info("文件 '{}' 已被处理并分割成 {} 个文档块。", fileName, documents.size());
            log.info("文件内容为 {} ", documents);
            processAndLoadDocuments(documents, fileName);
        } else {
            // TikaDocumentReader 可以直接处理 Resource 输入流，无需创建临时文件
            TikaDocumentReader tikaReader = new TikaDocumentReader(resource);
            List<Document> documents = tikaReader.get();
            processAndLoadDocuments(documents, fileName);
        }
    }

    /**
     * 将ETL的转换和加载阶段封装成一个私有方法，以提高代码复用性
     */
    private void processAndLoadDocuments(List<Document> documents, String fileName) {
        log.info("====== 开始处理文档 '{}' ======", fileName);
        log.info("初始文档数量: {}", documents.size());
        
        // 1. 添加文件名元数据
        documents.forEach(document -> document.getMetadata().put("fileName", fileName));
        log.info("已为所有文档添加fileName元数据");

        // 2. 创建转换器
        log.info("开始创建ETL转换器...");
        var summaryEnricher = new SummaryMetadataEnricher(chatModel, List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        var keywordEnricher = new KeywordMetadataEnricher(chatModel, 5);
        var textSplitter = new TokenTextSplitter(800, 350, 5, 10000, true);
        log.info("ETL转换器创建完成");

        // 3. 执行摘要增强（耗时操作）
        log.info("开始执行摘要增强处理，这可能需要较长时间...");
        long startTime = System.currentTimeMillis();
        List<Document> summaryEnrichedDocs = summaryEnricher.apply(documents);
        long summaryTime = System.currentTimeMillis() - startTime;
        log.info("摘要增强完成，耗时: {} ms，文档数量: {}", summaryTime, summaryEnrichedDocs.size());

        // 4. 执行关键词提取（耗时操作）
        log.info("开始执行关键词提取...");
        startTime = System.currentTimeMillis();
        List<Document> keywordEnrichedDocs = keywordEnricher.apply(summaryEnrichedDocs);
        long keywordTime = System.currentTimeMillis() - startTime;
        log.info("关键词提取完成，耗时: {} ms，文档数量: {}", keywordTime, keywordEnrichedDocs.size());

        // 5. 执行文本分割
        log.info("开始执行文本分割...");
        startTime = System.currentTimeMillis();
        List<Document> processedDocuments = textSplitter.apply(keywordEnrichedDocs);
        long splitTime = System.currentTimeMillis() - startTime;
        log.info("文本分割完成，耗时: {} ms，最终文档块数量: {}", splitTime, processedDocuments.size());

        // 6. 计算文档总大小（用于诊断）
        try {
            long totalContentSize = processedDocuments.stream()
                    .mapToLong(doc -> doc.getFormattedContent().length())
                    .sum();
            log.info("所有文档块的总内容大小: {} 字符 ({} KB)", totalContentSize, totalContentSize / 1024);
        } catch (Exception e) {
            log.warn("无法计算文档总大小: {}", e.getMessage());
        }

        // 7. 分批写入Redis，避免一次性写入过多数据导致连接中断
        int batchSize = 5; // 每批处理5个文档
        int totalBatches = (int) Math.ceil((double) processedDocuments.size() / batchSize);
        log.info("开始分批写入Redis，总批次: {}，每批大小: {}", totalBatches, batchSize);

        for (int i = 0; i < processedDocuments.size(); i += batchSize) {
            int currentBatch = (i / batchSize) + 1;
            int endIndex = Math.min(i + batchSize, processedDocuments.size());
            List<Document> batch = processedDocuments.subList(i, endIndex);
            
            log.info("正在写入第 {}/{} 批，包含 {} 个文档块 (索引 {} - {})", 
                    currentBatch, totalBatches, batch.size(), i, endIndex - 1);
            
            try {
                startTime = System.currentTimeMillis();
                redisVectorStore.add(batch);
                long writeTime = System.currentTimeMillis() - startTime;
                log.info("第 {}/{} 批写入成功，耗时: {} ms", currentBatch, totalBatches, writeTime);
            } catch (Exception e) {
                log.error("第 {}/{} 批写入Redis失败，尝试重试一次...", currentBatch, totalBatches, e);
                
                // 重试一次
                try {
                    Thread.sleep(1000); // 等待1秒后重试
                    redisVectorStore.add(batch);
                    log.info("第 {}/{} 批重试写入成功", currentBatch, totalBatches);
                } catch (Exception retryException) {
                    log.error("第 {}/{} 批重试写入仍然失败，跳过该批次", currentBatch, totalBatches, retryException);
                    throw new RuntimeException("写入Redis失败: 批次 " + currentBatch + "/" + totalBatches, retryException);
                }
            }
        }

        log.info("====== 文档 '{}' 处理完成，共 {} 个文档块已成功加载到Redis ======", fileName, processedDocuments.size());
    }


    @Override
    public List<Document> doSearch(String question) {
        return this.redisVectorStore.similaritySearch(question);
    }
}