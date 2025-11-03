# 使用官方的 Eclipse Temurin JDK 21 镜像
FROM eclipse-temurin:21-jdk-jammy
# 设置容器内的工作目录
WORKDIR /app
# 复制 mcp-server 的 jar 包
COPY target/mcp-server.jar mcp-server.jar
# 声明 server 服务可能使用的端口
EXPOSE 9060
# 容器启动命令
ENTRYPOINT ["java", "-jar", "mcp-server.jar"]