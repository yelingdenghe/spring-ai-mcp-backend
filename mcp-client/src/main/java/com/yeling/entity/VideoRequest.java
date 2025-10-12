package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName VideoRequest
 * @Date 2025/10/12 17:12
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoRequest {
    String prompt;
    String model;
    String quality;
    String size;
    Integer fps;
    Boolean with_audio;
    Boolean watermark;
}
