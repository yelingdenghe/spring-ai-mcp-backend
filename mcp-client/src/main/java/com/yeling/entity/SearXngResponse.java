package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName SearchResult
 * @Date 2025/10/5 09:40
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearXngResponse {

    private String query;
    private List<SearchResult> results;
}
