package com.yeling.mcp.config;

import com.yeling.mcp.tool.DateToolEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

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
    @Description("获取当前的日期和时间")
    public Function<DateToolEntity.Request, DateToolEntity.Response> currentTimeFunction() {
        return (request -> {
            log.info("======调用函数式工具：currentTimeFunction()======");

            String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            String currentTime = String.format("当前时间是 %s", format);

            return new DateToolEntity.Response(currentTime);
        });
    }
}
