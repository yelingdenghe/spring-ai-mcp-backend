package com.yeling.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName EmailSend
 * @Date 2025/10/7 10:23
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    @ToolParam(description = "收件人邮箱")
    String email;
    @ToolParam(description = "发送邮件的标题/主题")
    String subject;
    @ToolParam(description = "发送邮件的正文内容/消息")
    String message;
}
