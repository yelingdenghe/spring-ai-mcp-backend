# PyTorch 库导出与挂载操作指南

## 📋 目标

从云服务器上的Docker容器中导出PyTorch库文件，保存到本地，以后部署时直接挂载使用，避免重复下载。

---

## 🎯 操作流程概览

```
云服务器容器下载 → 导出到服务器文件系统 → 下载到本地 → 上传使用时直接挂载
    (5-10分钟)        (1分钟)            (5分钟)      (5秒启动)
```

---

## ⚡ 第一步：等待容器下载完成

### 1.1 监控下载进度

在云服务器上执行：

```bash
# SSH连接到云服务器
ssh ubuntu@81.70.133.180

# 查看容器日志
docker logs -f mcp-client
```

### 1.2 识别下载完成标志

看到以下日志表示**下载成功**：

```
========================================
Embedding模型预热完成！耗时: xxxxx 毫秒
PyTorch库和模型已加载到内存
========================================
```

> ⏱️ 预计等待时间：5-10分钟（取决于网络速度）

---

## 📦 第二步：从容器导出到云服务器

### 2.1 创建导出目录

```bash
# 在云服务器上创建目录
mkdir -p /home/ubuntu/ai/pytorch-libs-export
```

### 2.2 从容器复制文件

```bash
# 方法1：直接复制（推荐）
docker cp mcp-client:/root/.djl.ai/. /home/ubuntu/ai/pytorch-libs-export/

# 验证文件
ls -lh /home/ubuntu/ai/pytorch-libs-export/pytorch/2.5.1/cpu/linux-x86_64/native/lib/
```

**预期输出：**
```
-rwxr-xr-x 1 root root  23M libc10.so
-rwxr-xr-x 1 root root 178M libtorch_cpu.so
... 其他文件 ...
```

### 2.3 打包压缩

```bash
# 打包成 tar.gz（方便下载）
cd /home/ubuntu/ai
tar czf pytorch-libs.tar.gz pytorch-libs-export/

# 查看压缩包大小
ls -lh pytorch-libs.tar.gz
```

> 📦 压缩包大小约：150-200MB

---

## ⬇️ 第三步：从云服务器下载到本地

### 3.1 使用 SCP 下载

**在本地电脑（Windows）上执行：**

```bash
# 使用 Git Bash 或 PowerShell
scp ubuntu@81.70.133.180:/home/ubuntu/ai/pytorch-libs.tar.gz F:/study/java/2025/pytorch-libs-backup/
```

### 3.2 或使用 WinSCP / FileZilla

1. 连接到服务器：`81.70.133.180`
2. 导航到：`/home/ubuntu/ai/`
3. 下载文件：`pytorch-libs.tar.gz`
4. 保存到本地：`F:/study/java/2025/pytorch-libs-backup/`

> ⏱️ 下载时间：约5分钟（取决于带宽）

---

## 🚀 第四步：后续部署时上传并使用

### 4.1 上传到新服务器

当你需要在新服务器或重新部署时：

```bash
# 从本地上传到服务器
scp F:/study/java/2025/pytorch-libs-backup/pytorch-libs.tar.gz ubuntu@新服务器IP:/home/ubuntu/ai/
```

### 4.2 解压到指定目录

在服务器上：

```bash
# 解压
cd /home/ubuntu/ai
tar xzf pytorch-libs.tar.gz

# 重命名为标准目录名
mv pytorch-libs-export pytorch-libs

# 验证
ls -lh /home/ubuntu/ai/pytorch-libs/pytorch/2.5.1/cpu/linux-x86_64/native/lib/
```

### 4.3 修改 docker-compose.yml 使用目录挂载

编辑 `docker-compose.yml` 文件：

```yaml
volumes:
  - /home/ubuntu/ai/models/qwen-embedding:/app/models
  
  # 注释掉 Docker 卷（或删除）
  # - djl-cache:/root/.djl.ai
  
  # 使用目录挂载（取消注释）
  - /home/ubuntu/ai/pytorch-libs:/root/.djl.ai
```

### 4.4 启动容器

```bash
cd /home/ubuntu/ai/SpringAI-MCP-RAG-Dev
docker-compose up -d
```

**验证启动速度：**

```bash
# 查看日志，应该几秒内就完成预热
docker logs -f mcp-client
```

**预期日志：**
```
========================================
开始预热Embedding模型...
========================================
（直接加载，无下载过程）
========================================
Embedding模型预热完成！耗时: 2000-5000 毫秒
========================================
```

> ⚡ 启动时间：从 10分钟 降低到 5秒！

---

## 📋 完整命令速查

### 在云服务器上：

```bash
# 1. 等待下载完成
docker logs -f mcp-client

# 2. 导出文件
mkdir -p /home/ubuntu/ai/pytorch-libs-export
docker cp mcp-client:/root/.djl.ai/. /home/ubuntu/ai/pytorch-libs-export/

# 3. 打包
cd /home/ubuntu/ai
tar czf pytorch-libs.tar.gz pytorch-libs-export/

# 4. 查看
ls -lh pytorch-libs.tar.gz
```

### 在本地电脑上：

```bash
# 下载到本地
scp ubuntu@81.70.133.180:/home/ubuntu/ai/pytorch-libs.tar.gz F:/study/java/2025/pytorch-libs-backup/
```

### 部署到新服务器时：

```bash
# 1. 上传
scp F:/study/java/2025/pytorch-libs-backup/pytorch-libs.tar.gz ubuntu@新服务器:/home/ubuntu/ai/

# 2. SSH 到新服务器
ssh ubuntu@新服务器

# 3. 解压
cd /home/ubuntu/ai
tar xzf pytorch-libs.tar.gz
mv pytorch-libs-export pytorch-libs

# 4. 修改 docker-compose.yml（参考上面 4.3）

# 5. 启动
cd SpringAI-MCP-RAG-Dev
docker-compose up -d
```

---

## ✅ 验证清单

- [ ] 容器下载完成（看到预热成功日志）
- [ ] 文件导出成功（`ls` 命令能看到 .so 文件）
- [ ] 压缩包创建成功（大小约 150-200MB）
- [ ] 下载到本地成功
- [ ] 上传到新服务器成功
- [ ] 解压后文件结构正确
- [ ] docker-compose.yml 修改正确
- [ ] 容器启动快速（5秒内完成预热）

---

## ⚠️ 注意事项

### 1. 文件权限

导出后的文件权限必须正确：

```bash
# 如果遇到权限问题
sudo chown -R root:root /home/ubuntu/ai/pytorch-libs
sudo chmod -R 755 /home/ubuntu/ai/pytorch-libs
```

### 2. 路径一致性

确保所有服务器上的路径一致：
- ✅ 推荐：`/home/ubuntu/ai/pytorch-libs`
- ❌ 避免：每个服务器路径不同

### 3. 版本匹配

导出的库文件版本必须匹配：
- PyTorch 版本：**2.5.1**
- 平台：**linux-x86_64**
- 类型：**CPU**

如果应用更新需要不同版本，需要重新下载。

### 4. Docker 卷清理（可选）

如果不再使用 Docker 卷，可以删除：

```bash
# 查看卷
docker volume ls | grep djl-cache

# 删除卷（释放空间）
docker volume rm springai-mcp-rag-dev_djl-cache
```

---

## 🎯 时间效益对比

| 场景 | 使用 Docker 卷 | 使用目录挂载 | 节省时间 |
|------|---------------|-------------|---------|
| 首次部署 | 10分钟 | 5秒 | **9分55秒** |
| 重启容器 | 5秒 | 5秒 | 0秒 |
| 重建容器 | 10分钟 | 5秒 | **9分55秒** |
| 新服务器部署 | 10分钟 | 5秒 | **9分55秒** |

**一次性投入：** 10分钟下载 + 5分钟备份 = 15分钟  
**长期收益：** 每次部署节省 10分钟

---

## 💡 推荐工作流

1. **第一次部署**：让容器自动下载（当前正在进行）
2. **下载完成后**：立即导出并备份到本地
3. **后续所有部署**：使用备份文件，无需重新下载
4. **定期备份**：将 `pytorch-libs.tar.gz` 加入版本控制或云存储

---

## 📞 故障排查

### 问题1：导出后文件大小不对

```bash
# 检查原始文件大小
docker exec mcp-client du -sh /root/.djl.ai

# 重新导出
rm -rf /home/ubuntu/ai/pytorch-libs-export
docker cp mcp-client:/root/.djl.ai/. /home/ubuntu/ai/pytorch-libs-export/
```

### 问题2：挂载后容器仍然下载

检查路径是否正确：

```bash
# 进入容器检查
docker exec -it mcp-client ls -la /root/.djl.ai/pytorch/

# 应该能看到文件，如果为空说明挂载路径错误
```

### 问题3：容器启动报错

```bash
# 查看详细日志
docker logs mcp-client

# 检查文件完整性
ls -lh /home/ubuntu/ai/pytorch-libs/pytorch/2.5.1/cpu/linux-x86_64/native/lib/
```

---

## 🎉 总结

遵循这个流程，你可以：
- ✅ 只下载一次 PyTorch 库
- ✅ 永久保存在本地备份
- ✅ 任何时候快速部署
- ✅ 多个服务器共享使用
- ✅ 节省大量时间和流量

**现在你需要做的就是：等待当前容器下载完成，然后执行第二步！** 🚀

