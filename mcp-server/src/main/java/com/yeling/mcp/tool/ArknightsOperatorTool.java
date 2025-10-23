package com.yeling.mcp.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ArknightsOperatorTool
 * @Date 2025/10/23 09:41
 * @Version 1.0
 */
@Component
@Slf4j
public class ArknightsOperatorTool {

    // 匹配 {{人员档案 ... }} 整个块
    private static final Pattern ARCHIVE_BLOCK_PATTERN = Pattern.compile(
            "\\{\\{人员档案(.*?)\\}\\}",
            Pattern.DOTALL
    );

    // 匹配块内的 |key=value 字段
    // 它会匹配 |key = value 直到下一个 | 或 }}
    private static final Pattern FIELD_PATTERN = Pattern.compile(
            "\\|\\s*([^=]+?)\\s*=\\s*(.*?)(?=\\n\\s*\\||\\n\\s*\\}\\})",
            Pattern.DOTALL
    );

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ArknightsOperatorTool(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * MCP工具的入口点：查询干员信息
     *
     * @param operatorName 干员名称 (例如 "真言", "阿米娅")
     * @return 包含干员档案信息的JSON字符串，如果失败则返回错误信息的JSON。
     */
    @Tool(description = "查询干员信息")
    public String queryOperator(String operatorName) {
        try {
            // 1. 获取Wikitext
            String wikitext = fetchRawWikitext(operatorName);
            log.info("Wikitext: {}", wikitext);
            if (wikitext == null || wikitext.isEmpty()) {
                return objectMapper.writeValueAsString(Map.of("error", "未找到干员 " + operatorName + " 的Wikitext。"));
            }

            // 2. 解析Wikitext
            Map<String, String> archiveData = parseWikitext(wikitext);
            log.info("ArchiveData: {}", archiveData);
            if (archiveData.isEmpty()) {
                return objectMapper.writeValueAsString(Map.of("error", "在 " + operatorName + " 的页面上未找到 {{人员档案}} 模板。"));
            }

            // 3. 返回结构化的JSON字符串
            return objectMapper.writeValueAsString(archiveData);

        } catch (Exception e) {
            e.printStackTrace(); // 在实际应用中建议使用 @Slf4j
            try {
                return objectMapper.writeValueAsString(Map.of("error", "查询时发生内部错误: " + e.getMessage()));
            } catch (Exception ex) {
                return "{\"error\":\"查询时发生内部错误且无法序列化错误信息\"}";
            }
        }
    }

    /**
     * 步骤1: 从PRTS API获取原始Wikitext
     */
    private String fetchRawWikitext(String operatorName) throws Exception {
        String encodedTitle = URLEncoder.encode(operatorName, StandardCharsets.UTF_8);
        String apiUrl = "https://prts.wiki/api.php?action=query&titles=" + encodedTitle +
                "&prop=revisions&rvprop=content&format=json";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP请求失败: " + response.statusCode());
        }

        // 解析JSON以获取内容
        JsonNode root = objectMapper.readTree(response.body());
        JsonNode pages = root.path("query").path("pages");
        if (pages.isEmpty()) {
            return null;
        }

        // 遍历 "pages" 节点 (因为我们不知道 pageId)
        JsonNode pageData = pages.elements().next();
        if (pageData.has("missing")) {
            return null; // 页面不存在
        }

        return pageData.path("revisions").get(0).path("*").asText(null);
    }

    /**
     * 步骤2: 解析Wikitext, 提取 {{人员档案}} 模板中的字段
     */
    private Map<String, String> parseWikitext(String wikitext) {
        Map<String, String> data = new LinkedHashMap<>(); // 使用LinkedHashMap保持字段顺序

        Matcher blockMatcher = ARCHIVE_BLOCK_PATTERN.matcher(wikitext);
        while (blockMatcher.find()) {
            String archiveContent = blockMatcher.group(1); // 获取 {{人员档案 ... }} 内部的所有内容

            // 确保在内容的开头和结尾有换行符，以便正确匹配最后一个字段
            String paddedContent = "\n" + archiveContent + "\n}}";

            Matcher fieldMatcher = FIELD_PATTERN.matcher(paddedContent);
            while (fieldMatcher.find()) {
                String key = fieldMatcher.group(1).trim();
                String value = fieldMatcher.group(2).trim();

                // 简单清理：去除Wikitext链接标记，例如 [[萨尔贡]] -> 萨尔贡
                value = value.replaceAll("\\[\\[(.*?)\\|(.*?)\\]\\]", "$2") // [[页面|文本]] -> 文本
                        .replaceAll("\\[\\[(.*?)\\]\\]", "$1");     // [[页面]] -> 页面

                // 简单清理：去除 <br> <br/>
                value = value.replaceAll("<br\\s*/?>", "\n").trim();

                data.put(key, value);
            }
        }
        return data;
    }
}
