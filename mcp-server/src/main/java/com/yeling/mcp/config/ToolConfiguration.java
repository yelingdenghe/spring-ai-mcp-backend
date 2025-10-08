package com.yeling.mcp.config;

import com.yeling.mcp.tool.DateTool;
import com.yeling.mcp.tool.EmailTool;
import com.yeling.mcp.tool.ProductTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName ToolConfiguration
 * @Date 2025/10/7 07:59
 * @Version 1.0
 */
@Configuration
@Slf4j
public class ToolConfiguration {

    @Bean
    public ToolCallbackProvider DateTools(DateTool dateToolEntity, EmailTool emailTool, ProductTool productTool) {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(dateToolEntity, emailTool, productTool)
                .build();
    }
}
