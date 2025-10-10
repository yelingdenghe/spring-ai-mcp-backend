package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName AsrUrlRequest
 * @Date 2025/10/10 08:10
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsrUrlRequest {
    private String audioUrl;
    private String asrModel;
}