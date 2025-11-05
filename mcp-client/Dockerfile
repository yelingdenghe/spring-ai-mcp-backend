FROM gcr.io/distroless/java21:latest
# 设置容器内的工作目录为模型目录，使 ONNX Runtime 能找到 model.onnx_data
WORKDIR /app/models
COPY target/mcp-client.jar /app/mcp-client.jar
EXPOSE 9009
# 使用绝对路径启动 jar 文件
ENTRYPOINT ["java", "-jar", "/app/mcp-client.jar"]