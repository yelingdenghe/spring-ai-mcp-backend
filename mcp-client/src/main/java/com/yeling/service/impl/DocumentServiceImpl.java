package com.yeling.service.impl;

import com.yeling.service.DocumentService;
import com.yeling.utils.CustomTextSplitter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName DocumentServiceImpl
 * @Date 2025/10/2 11:36
 * @Version 1.0
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final RedisVectorStore redisVectorStore;

    /** {@inheritDoc} */
    @Override
    public void uploadText(Resource resource, String fileName) {
        // 普通文件转换为document对象
        TextReader reader = new TextReader(resource);
        reader.getCustomMetadata().put("fileName", fileName);
        List<Document> documents = reader.get();

        // 转换器组件，有五个默认值
        // defaultChunkSize：每个文本块的目标 Token 数（默认值：800）
        // minChunkSizeChars：每个文本块的最小字符数（默认值：350）
        // minChunkLengthToEmbed：可嵌入分块的最小长度要求（默认值：5）
        // maxNumChunks：单个文本生成的最大分块数限制（默认值：10000）
        // keepSeparator： 是否在数据块中保留分隔符（如换行符）（默认值： true）
        // TokenTextSplitter splitter = new TokenTextSplitter();
        // List<Document> documentList = splitter.apply(documents);

        // 自定义文本切分器
        CustomTextSplitter splitter = new CustomTextSplitter();
        List<Document> list = splitter.apply(documents);

        // 将文档添加到redis中--向量存储
        redisVectorStore.add(list);

        // 检索与查询相似的文档
        List<Document> spring = this.redisVectorStore.similaritySearch(SearchRequest.builder()
                        .query("Spring")
                        .topK(5)
                        .build());

    }
}
