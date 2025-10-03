package com.yeling.controller;

import com.yeling.entity.ChatEntity;
import com.yeling.service.ChatService;
import com.yeling.service.DocumentService;
import com.yeling.utils.LeeResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @Resource
    private ChatService chatService;

    @PostMapping("/uploadRagDoc")
    public LeeResult uploadRagDoc(@RequestParam("file") MultipartFile file) {
        documentService.uploadText(file.getResource(), file.getOriginalFilename());
        return LeeResult.ok();
    }

    @GetMapping("/doSearch")
    public LeeResult doSearch(@RequestParam String question) {
        return LeeResult.ok(documentService.doSearch(question));
    }

    @PostMapping("/search")
    public void search(@RequestBody ChatEntity chat, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        List<Document> documents = documentService.doSearch(chat.getMessage());
        chatService.doChatRagSearch(chat, documents);

    }



}
