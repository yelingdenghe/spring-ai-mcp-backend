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

    /**
     * @description: 调用搜索引擎进行搜索
     * @author: 夜凌
     * @date: 2025/10/31 17:50
     * @param: [query]
     * @return: List<SearchResult>
     **/
    List<SearchResult> search(String query);

}
