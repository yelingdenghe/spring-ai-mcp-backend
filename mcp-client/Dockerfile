# 使用官方的 Eclipse Temurin JDK 21 镜像
FROM eclipse-temurin:21-jdk-jammy
# 设置容器内的工作目录
WORKDIR /app
COPY target/mcp-client.jar mcp-client.jar
EXPOSE 9009
ENTRYPOINT ["java", "-jar", "mcp-client.jar"]