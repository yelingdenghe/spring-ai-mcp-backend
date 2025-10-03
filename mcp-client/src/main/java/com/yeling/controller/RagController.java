package com.yeling.controller;

import com.yeling.service.DocumentService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName RagController
 * @Date 2025/10/2 11:14
 * @Version 1.0
 */
@RestController
@RequestMapping("rag")
class RagController {

    @Resource
    private DocumentService documentService;

    @PostMapping("/uploadRagDoc")
    public LeeResult uploadRagDoc(@RequestParam("file") MultipartFile file) {
        documentService.uploadText(file.getResource(), file.getOriginalFilename());
        return LeeResult.ok();
    }

}
