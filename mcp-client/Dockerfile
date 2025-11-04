FROM gcr.io/distroless/java21:latest
# 设置容器内的工作目录
WORKDIR /app
COPY target/mcp-client.jar /app/mcp-client.jar
EXPOSE 9009
ENTRYPOINT ["java", "-jar", "mcp-client.jar"]