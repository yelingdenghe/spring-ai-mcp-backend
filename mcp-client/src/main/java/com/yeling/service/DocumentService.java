package com.yeling.service;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName DocumentService
 * @Date 2025/10/2 11:36
 * @Version 1.0
 */
public interface DocumentService {

    /**
     * @description: 加载文档，保存数据到知识库
     * @author: 夜凌
     * @date: 2025/10/3 11:03
     * @param: [resource, fileName]
     * @return: void
     **/
    void uploadText(Resource resource, String fileName);

    /**
     * @description: 根据提问从知识库中进行查询
     * @author: 夜凌
     * @date: 2025/10/3 15:40
     * @param: [question]
     * @return: void
     **/
    List<Document> doSearch(String question);

}
