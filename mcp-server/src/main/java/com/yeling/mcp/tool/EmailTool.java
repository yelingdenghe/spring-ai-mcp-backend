
package com.yeling.mcp.tool;

import com.yeling.entity.EmailRequest;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * @author 夜凌
 * @Description: 一个持有日期工具相关数据传输对象的容器类。
 * @ClassName DateEntity
 * @Date 2025/10/7 07:59
 * @Version 1.0
 */
@Component
@Slf4j
public class EmailTool {

    private final JavaMailSender mailSender;

    private final String from;

    public EmailTool(JavaMailSender mailSender, @Value("${spring.mail.username}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Tool(description = "给指定邮箱发送邮件，email 为收件人， subject 为邮件标题， message 为邮件内容")
    public void sendMailMessage(@ToolParam(description = "映射了上面所需的三个参数") EmailRequest email) {
        log.info("====== 调用MCP工具：sendMailMessage() ======");
        log.info("====== 参数 email：{} ======", email);

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom(from);
            helper.setTo(email.getEmail());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMessage());

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("====== 发送邮件出错：{} ======", e.getMessage());
        }
    }

}
