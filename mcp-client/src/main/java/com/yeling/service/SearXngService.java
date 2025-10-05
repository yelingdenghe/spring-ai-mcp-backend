package com.yeling.service;

import com.yeling.entity.SearchResult;

import java.util.List;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName SearXngService
 * @Date 2025/10/5 09:42
 * @Version 1.0
 */
public interface SearXngService {

    List<SearchResult> search(String query);

}
