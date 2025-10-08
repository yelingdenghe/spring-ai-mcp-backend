
package com.yeling.mcp.tool;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
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
    public void sendMailMessage(EmailRequest email) {
        log.info("====== 调用MCP工具：sendMailMessage() ======");
        log.info("====== 参数 email：{} ======", email);

        Integer contentType = email.getContentType();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom(from);
            helper.setTo(email.getEmail());
            helper.setSubject(email.getSubject());
            if (contentType == 1) {
                helper.setText(covert(email.getMessage()), true);
            } else if (contentType == 2) {
                helper.setText(email.getMessage(), true);
            } else {
                helper.setText(email.getMessage());
            }

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("====== 发送邮件出错：{} ======", e.getMessage());
        }
    }

    @Tool(description = "查询我的邮箱/邮件地址")
    public String getMyEmailAddress() {
        log.info("====== 调用MCP工具：getMyEmailAddress() ======");

        return "1453443780@qq.com";
    }

    /**
     * @description: MarkDown转HTML
     * @author: 夜凌
     * @date: 2025/10/8 07:50
     * @param: [markdown]
     * @return: String
     **/
    public static String covert(String markdown) {
        // flexmark 的配置对象，用于设置 Markdown 解析和渲染的选项。这里创建一个空配置，表示使用默认规则
        MutableDataSet options = new MutableDataSet();

        // 根据传入的 options 配置构建一个 Markdown 解析器   Markdown → AST（抽象语法树） 的解析器。
        Parser parser = Parser.builder(options).build();
        // HtmlRenderer 是 AST → HTML 的渲染器   读取 Node（语法树），并输出标准 HTML。
        HtmlRenderer renderer = HtmlRenderer.builder(options).build();

        // 把 Markdown 文本传给 parser.parse()，解析成一个 语法树结构（Node）
        Node document = parser.parse(markdown);

        // 把语法树交给渲染器 renderer，返回HTML字符串
        return renderer.render(document);
    }

}
