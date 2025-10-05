package com.yeling.controller;

import com.yeling.service.SearXngService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName InternetController
 * @Date 2025/10/5 10:33
 * @Version 1.0
 */
@RestController
@RequestMapping("internet")
class InternetController {

    @Resource
    private SearXngService searXngService;

    @GetMapping("test")
    public Object test(@RequestParam("query") String query) {
        return searXngService.search(query);
    }

}
