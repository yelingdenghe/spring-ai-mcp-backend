package com.yeling.service.impl;

import com.yeling.service.DocumentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
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
import java.util.List;
import java.util.function.Function;

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

                // 3. 使用临时文件的URI来实例化MarkdownDocumentReader
                MarkdownDocumentReader markdownReader = new MarkdownDocumentReader(tempFile.toUri().toString());
                List<Document> documents = markdownReader.get();

                // 4. 执行ETL处理
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
        documents.forEach(document -> document.getMetadata().put("fileName", fileName));

        var summaryEnricher = new SummaryMetadataEnricher(chatModel, List.of(SummaryMetadataEnricher.SummaryType.CURRENT));
        var keywordEnricher = new KeywordMetadataEnricher(chatModel, 5);
        var textSplitter = new TokenTextSplitter(800, 350, 5, 10000, true);

        Function<List<Document>, List<Document>> transformerChain = summaryEnricher
                .andThen(keywordEnricher)
                .andThen(textSplitter);

        List<Document> processedDocuments = transformerChain.apply(documents);
        log.info("文件 '{}' 已被处理并分割成 {} 个文档块。", fileName, processedDocuments.size());

        redisVectorStore.add(processedDocuments);
        log.info("{} 个文档块已成功加载到Redis向量数据库。", processedDocuments.size());
    }


    @Override
    public List<Document> doSearch(String question) {
        return this.redisVectorStore.similaritySearch(question);
    }
}