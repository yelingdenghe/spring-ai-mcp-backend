package com.yeling.entity.request;

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
    @ToolParam(description = "邮件的内容是否为html还是为markdown格式。如果是markdown格式，则为1，如果是html格式，则为2，其他格式为0")
    private Integer contentType;
}
