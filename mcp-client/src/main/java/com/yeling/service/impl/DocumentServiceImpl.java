package com.yeling.service.impl;

import com.yeling.service.DocumentService;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    VectorStore vectorStore;

    /** {@inheritDoc} */
    @Override
    public void uploadText(Resource resource, String fileName) {
        TextReader reader = new TextReader(resource);
        reader.getCustomMetadata().put("fileName", fileName);
        List<Document> documents = reader.get();

    }
}
