package com.yeling.mcp.tool;

import com.fasterxml.jackson.annotation.JsonClassDescription;

/**
 * @author 夜凌
 * @Description: 一个持有日期工具相关数据传输对象的容器类。
 * @ClassName DateEntity
 * @Date 2025/10/7 07:59
 * @Version 1.0
 */
public class DateToolEntity {

    @JsonClassDescription("获取当前时间的请求，不需要任何参数")
    public record Request(
    ) {}

    /**
     * 定义工具的输出结果。
     */
    public record Response(String dateInfo) {}

}
