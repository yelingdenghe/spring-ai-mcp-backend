package com.yeling.utils;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;


/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName OkHttpClientUtil
 * @Date 2025/10/5 10:22
 * @Version 1.0
 */
@Slf4j
@Component
public class OkHttpClientUtil {

    @Resource
    private OkHttpClient getOkHttpClient;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static OkHttpClient okHttpClient;

    @PostConstruct
    public void init() {
        okHttpClient = getOkHttpClient;
    }

    public static <T> T doGet(String url, String query, Class<T> clazz) {
        // 构建url
        HttpUrl build = HttpUrl.get(url)
                .newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                .build();

        // 构建request
        Request request = new Request.Builder()
                .url(build)
                .build();


        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            if (response.body() != null) {
                String responseBody = response.body().string();

                // 使用JSON工具将响应体转化为对应的类型
                return JSONUtil.toBean(responseBody, clazz);
            }
            log.info("搜索失败: {}", response.message());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return null;
    }

}
