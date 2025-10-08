package com.yeling;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName McpServerApplication
 * @Date 2025/10/7 07:36
 * @Version 1.0
 */
@SpringBootApplication
@MapperScan("com.yeling.mapper")
class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }

}
