package com.yeling.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName TaskStatusResponse
 * @Date 2025/10/13 17:12
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // JSON序列化时忽略null字段
public class TaskStatusResponse {

    private String taskId;
    private String taskStatus;
    private String videoUrl;
    private String message;

    // 静态工厂方法，用于创建不同状态的响应
    public static TaskStatusResponse running(String taskId) {
        return new TaskStatusResponse(taskId, "RUNNING", null, "视频正在生成中...");
    }

    public static TaskStatusResponse success(String taskId, String videoUrl) {
        return new TaskStatusResponse(taskId, "SUCCEEDED", videoUrl, "视频生成成功");
    }

    public static TaskStatusResponse failed(String taskId, String message) {
        return new TaskStatusResponse(taskId, "FAILED", null, message);
    }
}