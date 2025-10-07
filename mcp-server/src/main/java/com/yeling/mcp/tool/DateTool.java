package com.yeling.mcp.tool;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Tool(description = "获取某城市时间,城市是可选参数，默认为当前时间")
    public String getCurrentTime(@Nullable String cityName, @Nullable String zoneId) {
        log.info("====== 调用MCP工具：getCurrentTime() ======");
        if (cityName == null) {

            String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            return String.format("当前时间是 %s", format);
        }

        ZoneId zone = ZoneId.of(zoneId);

        // 获取该时区对应的当前时段
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zone);

        String format = zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return String.format("当前%s的时间是 %s", cityName, format);
    }
}
