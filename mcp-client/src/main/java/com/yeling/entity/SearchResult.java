package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 夜凌
 * @Description: 联网搜索结果实体映射
 * @ClassName SearchResult
 * @Date 2025/10/5 09:40
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult {

    private String title;
    private String url;
    private String content;
    private double score;

}
