FROM gcr.io/distroless/java21:latest
# 设置容器内的工作目录
WORKDIR /app
# 复制 mcp-server 的 jar 包
COPY target/mcp-server.jar app/mcp-server.jar
# 声明 server 服务可能使用的端口
EXPOSE 9060
# 容器启动命令
ENTRYPOINT ["java", "-jar", "mcp-server.jar"]