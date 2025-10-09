package com.yeling.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName FluxImageService
 * @Date 2025/10/9 08:39
 * @Version 1.0
 */
@Slf4j
@Service
public class FluxImageService {

    private static final String BASE_URL = "https://api-inference.modelscope.cn/";
    private static final String API_KEY = "ms-0b08dbea-3886-4a48-99a0-c9d652c2d14f";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    public byte[] generateImage(String prompt) {
        try {
            // 1️⃣ 提交异步任务
            Map<String, Object> body = new HashMap<>();
            body.put("model", "MusePublic/489_ckpt_FLUX_1");
            body.put("prompt", prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("X-ModelScope-Async-Mode", "true");

            HttpEntity<String> entity = new HttpEntity<>(MAPPER.writeValueAsString(body), headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    BASE_URL + "v1/images/generations", entity, String.class
            );

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("提交任务失败: " + response);
            }

            String taskId = MAPPER.readTree(response.getBody()).get("task_id").asText();
            log.info("📦 提交任务成功，task_id={}", taskId);

            // 2️⃣ 轮询任务状态
            while (true) {
                HttpHeaders pollHeaders = new HttpHeaders();
                pollHeaders.setBearerAuth(API_KEY);
                pollHeaders.add("X-ModelScope-Task-Type", "image_generation");

                ResponseEntity<String> pollResponse = restTemplate.exchange(
                        BASE_URL + "v1/tasks/" + taskId,
                        HttpMethod.GET,
                        new HttpEntity<>(pollHeaders),
                        String.class
                );

                JsonNode result = MAPPER.readTree(pollResponse.getBody());
                String status = result.get("task_status").asText();
                log.info("⏳ 当前任务状态：{}", status);

                if ("SUCCEED".equals(status)) {
                    String imageUrl = result.get("output_images").get(0).asText();
                    log.info("✅ 任务成功，图片地址：{}", imageUrl);

                    // 3️⃣ 下载图片字节流
                    ResponseEntity<byte[]> imageResp = restTemplate.getForEntity(imageUrl, byte[].class);
                    return imageResp.getBody();
                } else if ("FAILED".equals(status)) {
                    throw new RuntimeException("图像生成失败：" + result.toPrettyString());
                }

                TimeUnit.SECONDS.sleep(5);
            }
        } catch (Exception e) {
            log.error("🔥 图像生成异常：", e);
            return new byte[0];
        }
    }
}