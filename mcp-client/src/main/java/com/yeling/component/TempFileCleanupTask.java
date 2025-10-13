package com.yeling.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.TimeUnit;

/**
 * @author 夜凌
 * @Description: TODO
 * @ClassName TempFileCleanupTask
 * @Date 2025/10/13 17:09
 * @Version 1.0
 */
@Component
@Slf4j
public class TempFileCleanupTask {

    // cron表达式：每天凌晨3点执行一次
    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupOldTempFiles() {
        log.info("开始执行临时文件清理任务...");
        String tempDir = System.getProperty("java.io.tmpdir");
        long cleanupThreshold = System.currentTimeMillis() - TimeUnit.HOURS.toMillis(24); // 删除24小时前的临时文件

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Path.of(tempDir), "frame_*")) {
            for (Path entry : stream) {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(entry, BasicFileAttributes.class);
                    if (attrs.creationTime().toMillis() < cleanupThreshold) {
                        Files.delete(entry);
                        log.info("成功删除过期临时文件: {}", entry.toAbsolutePath());
                    }
                } catch (IOException e) {
                    log.error("处理或删除临时文件失败: {}", entry.toAbsolutePath(), e);
                }
            }
        } catch (IOException e) {
            log.error("扫描临时文件目录失败", e);
        }
        log.info("临时文件清理任务执行完毕。");
    }
}