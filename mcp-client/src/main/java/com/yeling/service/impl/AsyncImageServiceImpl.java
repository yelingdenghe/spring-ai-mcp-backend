package com.yeling.service.impl;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeling.entity.ChatResponseEntity;
import com.yeling.enums.SSEMsgType;
import com.yeling.service.AsyncImageService;
import com.yeling.service.MultiModelService;
import com.yeling.utils.SSEServe;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName AsyncImageServiceImpl
 * @Date 2025/10/9 09:27
 * @Version 1.0
 */
@Slf4j
@Service
public class AsyncImageServiceImpl implements AsyncImageService {
    @Resource
    private MultiModelService multiModelService;

    private static final String BASE_URL = "https://api-inference.modelscope.cn/";
    private static final String API_KEY = "ms-0b08dbea-3886-4a48-99a0-c9d652c2d14f";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public byte[] generateImage(String prompt, String model) {
        try {
            String modelId;
            if ("flux_1".equalsIgnoreCase(model)) {
                modelId = "MusePublic/489_ckpt_FLUX_1";
            } else if ("qwen".equalsIgnoreCase(model)) {
                modelId = "Qwen/Qwen-Image";
            } else {
                throw new IllegalArgumentException("不支持的模型：" + model);
            }
            // 提交异步任务
            Map<String, Object> body = new HashMap<>();
            body.put("model", modelId);
            body.put("prompt", prompt);

            // 准备请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 开启异步模式
            headers.add("X-ModelScope-Async-Mode", "true");

            // 封装请求
            HttpEntity<String> entity = new HttpEntity<>(MAPPER.writeValueAsString(body), headers);

            // 发送POST请求
            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URL + "v1/images/generations", entity, String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("提交任务失败: " + response);
            }

            // 解析响应，获取task_id
            String taskId = MAPPER.readTree(response.getBody()).get("task_id").asText();
            log.info("提交任务成功，task_id={}", taskId);

            // 轮询任务状态
            while (true) {
                // 准备轮询请求的头
                HttpHeaders pollHeaders = new HttpHeaders();
                pollHeaders.setBearerAuth(API_KEY);
                pollHeaders.add("X-ModelScope-Task-Type", "image_generation");

                // 发送GET请求来查询任务状态
                ResponseEntity<String> pollResponse = restTemplate.exchange(
                        BASE_URL + "v1/tasks/" + taskId,
                        HttpMethod.GET,
                        new HttpEntity<>(pollHeaders),
                        String.class
                );

                // 解析状态
                JsonNode result = MAPPER.readTree(pollResponse.getBody());
                String status = result.get("task_status").asText();
                log.info("当前任务状态：{}", status);

                if ("SUCCEED".equals(status)) {
                    String imageUrl = result.get("output_images").get(0).asText();
                    log.info("任务成功，图片地址：{}", imageUrl);

                    // 下载图片字节流
                    ResponseEntity<byte[]> imageResp = restTemplate.getForEntity(imageUrl, byte[].class);
                    return imageResp.getBody();
                } else if ("FAILED".equals(status)) {
                    throw new RuntimeException("图像生成失败：" + result.toPrettyString());
                }

                // 等待5秒再进行下一次查询
                TimeUnit.SECONDS.sleep(5);
            }
        } catch (Exception e) {
            log.error("图像生成异常：", e);
            return new byte[0];
        }
    }

    @Override
    public void doImage(String query, MultipartFile file, String userId, String botMsgId) throws IOException {

        ChatClient chatClient = multiModelService.getChatClient("zhipu");

        // 从 MultipartFile 中获取字节数组
        byte[] imageBytes = file.getBytes();
        // 使用字节数组创建一个 ByteArrayResource 实例
        // 它是一个 Resource，可以被 .media() 方法接受
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            // 重写 getFilename 方法可以提供原始文件名，这对于某些处理流程可能是必要的
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        // 获取流
        Flux<String> contentStream = chatClient.prompt()
                .user(u -> u
                        .text(query)
                        .media(MimeTypeUtils.parseMimeType(file.getContentType()), imageResource)
                )
                .stream()
                .content();
        // 订阅流并使用 SSEServe 推送数据
        List<String> list = contentStream.toStream().peek(chunk -> {
            SSEServe.sendMsg(userId, SSEMsgType.ADD, chunk);
            log.info("ImageDesc content: {}", chunk);
        }).toList();

        // 拼接完整消息
        String finalContent = String.join("", list);

        // 创建最终的响应实体
        ChatResponseEntity chatResponseEntity = new ChatResponseEntity(finalContent, botMsgId);

        // 发送结束信号
        SSEServe.sendMsg(userId, SSEMsgType.FINISH, JSONUtil.toJsonStr(chatResponseEntity));
    }
}
