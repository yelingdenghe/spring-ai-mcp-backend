# SpringAI-MCP-RAG-Dev

<div align="center">

![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.3-green.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

**基于 Spring AI 的智能对话系统，集成 MCP 协议和 RAG 技术**

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [技术架构](#-技术架构) • [API 文档](#-api-文档) • [部署指南](#-部署指南)

</div>

---

## 📖 项目简介

SpringAI-MCP-RAG-Dev 是一个基于 Spring AI 框架开发的现代化智能对话系统，集成了 **MCP（Model Context Protocol）协议** 和 **RAG（检索增强生成）技术**。该项目支持多种主流 AI 模型，提供丰富的多模态能力，包括文本对话、语音识别/合成、图像理解、视频分析等功能。

### 💡 为什么选择本项目？

- 🚀 **开箱即用**：完整的配置示例和详细文档
- 🔌 **多模型支持**：OpenAI、DeepSeek、通义千问、智谱 AI、Ollama
- 🎯 **多模态能力**：文本、语音、图像、视频全覆盖
- 🛠️ **MCP 协议**：支持工具调用和外部服务集成
- 📚 **RAG 技术**：基于向量数据库的知识库检索
- 🌐 **互联网搜索**：集成 SearXNG 实现实时信息检索
- 🔄 **实时通信**：支持 SSE 和 WebSocket
- 🐳 **容器化部署**：Docker Compose 一键部署

---

## ✨ 功能特性

### 🤖 核心对话能力

- **智能对话**：支持流式输出、上下文管理、多轮对话
- **RAG 检索**：基于 Redis 向量数据库的知识库问答
- **文档解析**：支持 PDF、DOCX、Markdown 等多种文档格式
- **互联网搜索**：实时获取最新信息并生成回答

### 🎤 音频处理

- **ASR（语音识别）**：
  - 实时语音识别（WebSocket）
  - 音频文件转文字
  - 支持通义千问语音模型
  
- **TTS（语音合成）**：
  - 文字转语音
  - 多种音色选择

### 🖼️ 图像处理

- **图像理解**：基于视觉语言模型的图像描述和问答
- **图像描述生成**：自动生成详细的图像描述
- **多图对比分析**：支持多张图片的对比分析

### 🎬 视频处理

- **视频分析**：基于帧提取的视频内容理解
- **帧级处理**：关键帧提取和分析
- **视频问答**：针对视频内容的智能问答

### 🔧 MCP 工具调用

- **邮件发送**：集成邮件服务
- **数据库操作**：通过 MCP 协议操作数据库
- **文件系统**：MCP 文件系统服务器集成
- **可扩展性**：支持自定义 MCP 工具

---

## 🏗️ 技术架构

### 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                         前端层                               │
│                  (Vue 3 + TypeScript)                       │
└─────────────────────┬───────────────────────────────────────┘
                      │
                      │ HTTP/WebSocket/SSE
                      ▼
┌─────────────────────────────────────────────────────────────┐
│                      MCP Client                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Chat Service │  │ Audio Service│  │ Video Service│     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │  RAG Service │  │ Image Service│  │Search Service│     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────┬──────────────────────┬──────────────────────┘
              │                      │
              │ MCP Protocol         │ AI Model API
              ▼                      ▼
┌──────────────────────┐   ┌──────────────────────────┐
│    MCP Server        │   │   AI Model Providers     │
│  ┌────────────────┐  │   │  ┌────────────────────┐ │
│  │  Email Tool    │  │   │  │ OpenAI/DeepSeek    │ │
│  │  Database Tool │  │   │  │ 通义千问 (Qwen)     │ │
│  │  Custom Tools  │  │   │  │ 智谱 AI (GLM)       │ │
│  └────────────────┘  │   │  │ Ollama (Local)     │ │
└──────────────────────┘   │  └────────────────────┘ │
                           └──────────────────────────┘
              │                      │
              ▼                      ▼
┌──────────────────────┐   ┌──────────────────────────┐
│    MySQL Database    │   │  Redis Vector Store      │
│   (结构化数据存储)     │   │   (向量数据库)            │
└──────────────────────┘   └──────────────────────────┘
```

### 技术栈

#### 后端 (Java)

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.5.6 | 基础框架 |
| Spring AI | 1.0.3 | AI 集成框架 |
| Java | 21 | 开发语言 |
| MyBatis-Plus | 3.5.14 | ORM 框架 |
| Redis | - | 向量数据库 |
| MySQL | - | 关系型数据库 |
| WebFlux | - | 响应式编程 |

#### 前端 (Vue)

| 技术 | 说明 |
|------|------|
| Vue 3 | 前端框架 |
| TypeScript | 类型系统 |
| Vite | 构建工具 |
| Element Plus | UI 组件库 |

#### AI 模型支持

- **OpenAI API Compatible**: GPT-4, GPT-3.5, DeepSeek
- **阿里通义千问**: Qwen-32B, 语音、视频模型
- **智谱 AI**: GLM-4.1v-thinking-flash
- **Ollama**: 支持本地部署的各种开源模型

#### 工具与中间件

- **Docker & Docker Compose**: 容器化部署
- **SearXNG**: 元搜索引擎
- **Ollama**: 本地模型运行时
- **Nginx**: 前端服务器

---

## 🚀 快速开始

### 前置要求

- ✅ Java 21+
- ✅ Maven 3.8+
- ✅ Node.js 18+
- ✅ Docker & Docker Compose（可选，用于部署）
- ✅ Redis 服务器
- ✅ MySQL 8.0+

### 本地开发

#### 1️⃣ 克隆项目

```bash
git clone https://github.com/your-username/SpringAI-MCP-RAG-Dev.git
cd SpringAI-MCP-RAG-Dev
```

#### 2️⃣ 配置环境变量

**MCP Client 配置：**

```bash
cd mcp-client/src/main/resources/
cp .env.example .env
```

编辑 `.env` 文件，填入你的 API Keys：

```properties
# OpenAI / DeepSeek 配置
OPENAI_API_KEY=your_api_key_here
OPENAI_BASE_URL=https://api.deepseek.com
OPENAI_MODEL=deepseek-chat

# Qwen 配置
QWEN_API_KEY=your_qwen_api_key
QWEN_BASE_URL=https://api-inference.modelscope.cn
QWEN_MODEL=Qwen/Qwen3-32B

# Redis 配置
REDIS_PASSWORD=your_redis_password
REDIS_HOST=your_redis_host
REDIS_PORT=6379

# 其他配置...
```

**MCP Server 配置：**

```bash
cd mcp-server/src/main/resources/
cp .env.example .env
```

编辑 `.env` 文件，填入数据库和邮箱配置：

```properties
# MySQL 配置
MYSQL_USER=root
MYSQL_PASSWORD=your_password
MYSQL_HOST=localhost
MYSQL_PORT=3306

# 邮箱配置
MAIL_USERNAME=your_email@example.com
MAIL_PASSWORD=your_email_password
MAIL_HOST=smtp.example.com
MAIL_PORT=465
```

**MCP 服务器配置：**

```bash
cd mcp-client/src/main/resources/
cp mcp-servers.json.example mcp-servers.json
```

根据你的操作系统修改 `mcp-servers.json`。

#### 3️⃣ 启动后端服务

```bash
# 启动 MCP Server
cd mcp-server
mvn spring-boot:run

# 启动 MCP Client（新开一个终端）
cd mcp-client
mvn spring-boot:run
```

#### 4️⃣ 启动前端

```bash
cd ../spring-ai-frontend-vue
npm install
npm run dev
```

访问 `http://localhost:5173` 即可使用。

---

## 🐳 Docker 部署

### 一键部署所有服务

项目提供了完整的 Docker Compose 配置，可以一键部署所有服务。

#### 1️⃣ 准备工作

确保已配置好 `.env` 和 `mcp-servers.json` 文件（参考上面的配置步骤）。

#### 2️⃣ 构建并启动

```bash
# 创建 Docker 网络
docker network create springai

# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f
```

#### 3️⃣ 访问服务

- **前端界面**: http://localhost:5500
- **MCP Client API**: http://localhost:9009
- **MCP Server API**: http://localhost:9060

#### 4️⃣ 停止服务

```bash
docker-compose down
```

### Docker Compose 服务说明

| 服务 | 端口 | 说明 |
|------|------|------|
| frontend | 5500 | Vue 前端应用 |
| client | 9009 | MCP Client 服务 |
| server | 9060 | MCP Server 服务 |

**注意**：Redis、MySQL、Ollama、SearXNG 等服务需要单独部署或使用云服务。

---

## 📡 API 文档

### 聊天对话

#### 流式对话

```http
POST /chat/stream
Content-Type: application/json

{
  "message": "你好，请介绍一下 Spring AI",
  "sessionId": "user-session-123",
  "model": "openai"
}
```

**响应**：SSE 流式输出

#### 普通对话

```http
POST /chat
Content-Type: application/json

{
  "message": "你好",
  "sessionId": "user-session-123",
  "model": "openai"
}
```

### RAG 知识库

#### 上传文档

```http
POST /rag/upload
Content-Type: multipart/form-data

file: [文件]
namespace: "my-knowledge-base"
```

#### RAG 问答

```http
POST /rag/chat
Content-Type: application/json

{
  "question": "什么是 Spring AI？",
  "namespace": "my-knowledge-base",
  "model": "openai"
}
```

### 语音处理

#### 语音识别（ASR）

```http
POST /audio/asr
Content-Type: multipart/form-data

file: [音频文件]
```

#### 语音合成（TTS）

```http
POST /audio/tts
Content-Type: application/json

{
  "text": "你好，世界",
  "voice": "longxiaochun"
}
```

#### 实时语音识别（WebSocket）

```javascript
// WebSocket 连接
const ws = new WebSocket('ws://localhost:9009/audio/realtime-asr');

// 发送音频数据
ws.send(audioBuffer);

// 接收识别结果
ws.onmessage = (event) => {
  console.log('识别结果:', event.data);
};
```

### 图像处理

#### 图像描述

```http
POST /image/describe
Content-Type: multipart/form-data

file: [图片文件]
prompt: "描述这张图片"
```

### 视频处理

#### 视频分析

```http
POST /video/analyze
Content-Type: multipart/form-data

file: [视频文件]
question: "这个视频讲的是什么？"
```

#### 帧级分析

```http
POST /video/frame-analyze
Content-Type: application/json

{
  "frameUrls": ["url1", "url2", "url3"],
  "question": "这些画面展示了什么？"
}
```

### 互联网搜索

#### 搜索并生成回答

```http
POST /internet/search
Content-Type: application/json

{
  "query": "2024年人工智能发展趋势",
  "model": "openai"
}
```

---

## 🔧 配置说明

### application.yml 主要配置

```yaml
spring:
  ai:
    # 向量数据库配置
    vectorstore:
      redis:
        initialize-schema: true
        index-name: my-vector-index
        prefix: 'embedding:'
    
    # OpenAI 配置
    openai:
      api-key: ${OPENAI_API_KEY}
      base-url: ${OPENAI_BASE_URL}
      chat:
        options:
          model: ${OPENAI_MODEL}
    
    # MCP Client 配置
    mcp:
      client:
        type: async
        sse:
          connections:
            server1:
              url: http://localhost:9060
              sse-endpoint: /sse
    
    # Embedding 模型配置
    model:
      embedding: ollama
    
    ollama:
      base-url: http://localhost:11434
      embedding:
        options:
          model: qwen3-embedding:0.6b

  # Redis 配置
  data:
    redis:
      host: ${REDIS_HOST}
      port: ${REDIS_PORT}
      password: ${REDIS_PASSWORD}
```

### 环境变量说明

| 变量名 | 说明 | 必需 |
|--------|------|------|
| `OPENAI_API_KEY` | OpenAI/DeepSeek API Key | ✅ |
| `QWEN_API_KEY` | 通义千问 API Key | ✅ |
| `ZHIPU_API_KRY` | 智谱 AI API Key | ✅ |
| `REDIS_HOST` | Redis 主机地址 | ✅ |
| `REDIS_PASSWORD` | Redis 密码 | ✅ |
| `MYSQL_HOST` | MySQL 主机地址 | ✅ |
| `MYSQL_PASSWORD` | MySQL 密码 | ✅ |
| `MAIL_USERNAME` | 邮箱用户名 | ❌ |
| `MAIL_PASSWORD` | 邮箱密码 | ❌ |

---

## 📚 使用场景

### 1. 智能客服系统

基于 RAG 技术构建企业知识库，提供准确的客户咨询服务。

### 2. 文档问答助手

上传企业内部文档，快速检索并回答相关问题。

### 3. 多模态内容理解

处理文本、图像、音频、视频等多种类型的内容，提供智能分析。

### 4. 语音交互应用

实时语音识别和合成，打造语音交互体验。

### 5. 视频内容分析

自动分析视频内容，生成摘要和问答。

### 6. 智能搜索引擎

结合互联网搜索和 AI 生成，提供更智能的搜索体验。

---

## 🗂️ 项目结构

```
SpringAI-MCP-RAG-Dev/
├── mcp-client/                    # MCP 客户端（主服务）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yeling/
│   │   │   │   ├── controller/    # REST API 控制器
│   │   │   │   ├── service/       # 业务逻辑层
│   │   │   │   ├── config/        # 配置类
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   └── utils/         # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       ├── .env.example   # 环境变量示例
│   │   │       └── mcp-servers.json.example
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── mcp-server/                    # MCP 服务器（工具服务）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/yeling/
│   │   │   │   ├── mcp/           # MCP 工具实现
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   └── mapper/        # 数据访问层
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── .env.example
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml             # Docker 编排配置
├── .gitignore                     # Git 忽略配置
├── README.md                      # 项目文档（本文件）
└── pom.xml                        # Maven 父 POM
```

---

## 🤝 贡献指南

我们欢迎所有形式的贡献！

### 如何贡献

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 遵循 Java 代码规范
- 保持代码简洁和可读性
- 添加必要的注释和文档
- 编写单元测试

---

## 📝 待办事项

- [ ] 支持更多 AI 模型（Claude, Gemini 等）
- [ ] 实现会话管理和持久化
- [ ] 添加用户认证和权限管理
- [ ] 优化 RAG 检索算法
- [ ] 支持多语言国际化
- [ ] 添加完整的单元测试和集成测试
- [ ] 性能监控和日志分析
- [ ] Kubernetes 部署支持

---

## 🔗 相关链接

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [Model Context Protocol (MCP)](https://modelcontextprotocol.io/)
- [通义千问](https://help.aliyun.com/zh/model-studio/)
- [智谱 AI](https://open.bigmodel.cn/)
- [Ollama](https://ollama.ai/)

---

## ❓ 常见问题

### Q1: 如何获取 API Keys？

**A**: 访问以下链接注册并获取：
- OpenAI: https://platform.openai.com/api-keys
- DeepSeek: https://platform.deepseek.com/
- Qwen/ModelScope: https://www.modelscope.cn/
- 智谱 AI: https://open.bigmodel.cn/

### Q2: Redis 连接超时怎么办？

**A**: 检查以下几点：
1. Redis 服务是否正常运行
2. 防火墙是否开放对应端口
3. `.env` 文件中的 Redis 配置是否正确
4. 如果使用云 Redis，检查白名单配置

### Q3: 如何本地运行 Ollama 模型？

**A**: 
```bash
# 安装 Ollama
curl -fsSL https://ollama.ai/install.sh | sh

# 拉取模型
ollama pull qwen3-embedding:0.6b
ollama pull llama3:8b

# 启动服务（默认端口 11434）
ollama serve
```

### Q4: Docker 部署时内存不足？

**A**: 在 `docker-compose.yml` 中已经配置了内存限制：
- MCP Client: 256MB
- MCP Server: 150MB

如需调整，修改 `JAVA_OPTS` 环境变量：
```yaml
environment:
  - JAVA_OPTS=-Xmx512m  # 调整为 512MB
```

### Q5: 如何添加自定义 MCP 工具？

**A**: 在 `mcp-server` 模块中：
1. 创建新的工具类，实现 MCP 工具接口
2. 添加 `@McpTool` 注解
3. 实现工具逻辑
4. 重启服务即可自动注册

---

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源许可证。

---

## 💬 联系方式

如有问题或建议，请通过以下方式联系：

- 📧 Email: your-email@example.com
- 🐛 Issues: [GitHub Issues](https://github.com/your-username/SpringAI-MCP-RAG-Dev/issues)
- 💬 Discussions: [GitHub Discussions](https://github.com/your-username/SpringAI-MCP-RAG-Dev/discussions)

---

## 🌟 Star History

如果这个项目对你有帮助，请给个 ⭐️ Star 支持一下！

[![Star History Chart](https://api.star-history.com/svg?repos=your-username/SpringAI-MCP-RAG-Dev&type=Date)](https://star-history.com/#your-username/SpringAI-MCP-RAG-Dev&Date)

---

<div align="center">

**Made with ❤️ by Yeling**

[⬆ 回到顶部](#springai-mcp-rag-dev)

</div>

