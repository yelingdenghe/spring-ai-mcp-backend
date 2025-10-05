package com.yeling.service.impl;

import cn.hutool.json.JSONUtil;
import com.yeling.entity.SearXngResponse;
import com.yeling.entity.SearchResult;
import com.yeling.service.SearXngService;
import com.yeling.utils.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName SearXngServiceImpl
 * @Date 2025/10/5 09:42
 * @Version 1.0
 */
@Service
@Slf4j
class SearXngServiceImpl implements SearXngService {

    @Value("${internet.websearch.searxng.url}")
    private String SEARXNG_URL;

    @Value("${internet.websearch.searxng.counts}")
    private Integer counts;

    @Override
    public List<SearchResult> search(String query) {
        List<SearchResult> results = OkHttpClientUtil.doGet(SEARXNG_URL, query, SearXngResponse.class).getResults();

        if (results == null || results.isEmpty()) {
            return Collections.emptyList();
        }

        return results;
    }
}
