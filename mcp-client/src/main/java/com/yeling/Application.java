package com.yeling;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 夜凌
 * @ClassName Application
 * @Date 2025/9/28 20:00
 * @Version 1.0
 */
@SpringBootApplication
class Application {

    public static void main(String[] args) {

        // 加载 .env 文件
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // 把 .env文件的变量加载到环境变量中
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        SpringApplication.run(Application.class, args);
    }

}
