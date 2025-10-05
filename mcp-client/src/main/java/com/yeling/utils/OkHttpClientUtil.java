package com.yeling.utils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

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

    public static HttpUrl doGet(String url, String query) {
        return HttpUrl.get(url)
                .newBuilder()
                .addQueryParameter("q", query)
                .addQueryParameter("format", "json")
                .build();
    }

    public static String doGet(String url, Map<String, String> params, Map<String, String> headers) {
        return null;
    }

}
