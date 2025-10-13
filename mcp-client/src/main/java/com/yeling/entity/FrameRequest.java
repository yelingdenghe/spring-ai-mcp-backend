package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName FrameRequest
 * @Date 2025/10/13 15:57
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FrameRequest {
    MultipartFile firstFrame;
    MultipartFile lastFrame;
    String model;
    String prompt;
}
