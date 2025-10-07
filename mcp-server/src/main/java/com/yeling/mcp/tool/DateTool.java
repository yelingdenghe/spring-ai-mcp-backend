package com.yeling.mcp.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 夜凌
 * @Description: 一个持有日期工具相关数据传输对象的容器类。
 * @ClassName DateEntity
 * @Date 2025/10/7 07:59
 * @Version 1.0
 */
@Component
@Slf4j
public class DateTool {

    @Tool(description = "获取当前时间")
    public String getCurrentTime() {
        log.info("======调用MCP工具：getCurrentTime()======");

        String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return String.format("当前时间是 %s", format);
    }

}
