package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ChatEntity
 * @Date 2025/10/1 19:07
 * @Version 1.0
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity {

    private String currentUserName;
    private String message;
    private String botMsgId;

    private String modelName; // 新增字段，可选（deepseek / qwen）

}
