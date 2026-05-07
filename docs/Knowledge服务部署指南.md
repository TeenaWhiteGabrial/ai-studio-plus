# Knowledge 服务部署指南

本文档用于在服务器上部署 `knowledge` 后端服务。当前服务位于仓库 `knowledge/` 目录，pnpm 包名为 `@knowledgebase/doc-manager`，对外提供知识库文档上传、向量索引、语义检索等能力。

## 1. 部署架构

推荐部署方式：

```text
Portal / Admin
    |
    | HTTPS
    v
Nginx
    |
    | 反向代理 /ai-studio/v1/knowledge
    v
Knowledge Fastify 服务，端口 3002
    |
    +-- data/biaoshu.db       文档元数据 SQLite
    +-- data/vectors          向量 SQLite
    +-- data/kb-input         上传文件
    +-- data/models           本地 embedding 模型缓存
```

建议端口：

| 服务 | 端口 |
| --- | --- |
| Portal 前端 | 3000 |
| Knowledge 服务 | 3002 |
| Service 后端 | 8080 |

## 2. 服务器准备

安装 Node.js，建议 Node 20 或以上：

```bash
node -v
```

启用 pnpm：

```bash
corepack enable
corepack prepare pnpm@9.0.0 --activate
pnpm -v
```

安装 PM2：

```bash
npm i -g pm2
```

## 3. 拉取代码并安装依赖

```bash
cd /opt
git clone <你的仓库地址> ai-studio-plus
cd /opt/ai-studio-plus

pnpm install
```

如果服务器不能直接访问 npm registry，需要提前配置 pnpm registry。

## 4. 配置环境变量

进入 knowledge 目录：

```bash
cd /opt/ai-studio-plus/knowledge
cp .env.example .env
```

推荐 `.env` 内容：

```bash
NODE_ENV=production
HOST=0.0.0.0
PORT=3002
API_PREFIX=/ai-studio/v1/knowledge

EMBEDDING_MODE=local
EMBEDDING_LOCAL_MODEL=Xenova/bge-small-zh-v1.5
EMBEDDING_MODEL=Xenova/bge-small-zh-v1.5
EMBEDDING_CACHE_DIR=./data/models
EMBEDDING_DIMENSION=384

VECTOR_STORE=sqlite
VECTOR_STORE_PATH=./data/vectors
DB_PATH=./data/biaoshu.db
KB_INPUT_PATH=./data/kb-input
TESSDATA_PREFIX=./lib/tessdata
```

创建持久化目录：

```bash
mkdir -p data/models data/vectors data/kb-input lib/tessdata
```

## 5. 模型文件准备

Knowledge 默认使用本地 embedding 模型：

```text
Xenova/bge-small-zh-v1.5
```

首次启动时服务会尝试下载模型。如果服务器访问 HuggingFace 不稳定，可以使用镜像：

```bash
HF_ENDPOINT=https://hf-mirror.com
```

也可以提前把模型放到服务器：

```text
/opt/ai-studio-plus/knowledge/data/models/Xenova/bge-small-zh-v1.5
```

模型目录建议随 `data/` 一起持久化，不要在发版时删除。

## 6. 构建检查

回到仓库根目录执行：

```bash
cd /opt/ai-studio-plus
pnpm --filter @knowledgebase/doc-manager build
```

当前 `build` 主要执行 TypeScript 检查，生产运行仍使用：

```bash
pnpm start
```

## 7. 使用 PM2 启动服务

项目已提供 `knowledge/ecosystem.config.cjs`，生产环境推荐使用该配置启动。配置文件已使用 pnpm、`PORT=3002` 和相对路径，部署到不同目录时不需要修改 `cwd`。

```bash
cd /opt/ai-studio-plus/knowledge
mkdir -p logs data/models data/vectors data/kb-input lib/tessdata

pm2 start ecosystem.config.cjs
```

查看状态：

```bash
pm2 status
pm2 logs ai-studio-knowledge
```

保存 PM2 进程：

```bash
pm2 save
pm2 startup
```

按 `pm2 startup` 输出的命令再执行一次，即可设置开机自启。

重启服务：

```bash
pm2 restart ai-studio-knowledge
```

停止服务：

```bash
pm2 stop ai-studio-knowledge
```

如果不使用 `ecosystem.config.cjs`，也可以从仓库根目录用命令启动：

```bash
cd /opt/ai-studio-plus

PORT=3002 HF_ENDPOINT=https://hf-mirror.com \
pm2 start "pnpm --filter @knowledgebase/doc-manager start" \
  --name ai-studio-knowledge
```

## 8. Nginx 反向代理

示例域名：

```text
https://example.com
```

Nginx 配置：

```nginx
location /ai-studio/v1/knowledge/ {
    proxy_pass http://127.0.0.1:3002/ai-studio/v1/knowledge/;
    proxy_http_version 1.1;
    proxy_set_header Host $host;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
}
```

如果上传大文件，需要增加请求体大小限制：

```nginx
client_max_body_size 100m;
```

检查并重载 Nginx：

```bash
nginx -t
systemctl reload nginx
```

## 9. Portal 对接 Knowledge

Portal 端需要把 knowledge API 地址指向服务器：

```bash
NUXT_KNOWLEDGE_API_BASE=https://example.com/ai-studio/v1/knowledge
```

如果该变量参与前端构建，需要重新构建并重启 portal。

本地调试时也可以临时指向服务器：

```bash
NUXT_KNOWLEDGE_API_BASE=http://服务器IP:3002/ai-studio/v1/knowledge
```

生产环境建议通过 Nginx HTTPS 域名访问，不建议直接暴露 `3002`。

## 10. 验证服务

本机验证：

```bash
curl http://127.0.0.1:3002/ai-studio/v1/knowledge/health
```

公网验证：

```bash
curl https://example.com/ai-studio/v1/knowledge/health
```

预期返回包含：

```json
{
  "status": "ok",
  "timestamp": "...",
  "uptime": 123
}
```

上传文档：

```bash
curl -X POST https://example.com/ai-studio/v1/knowledge/upload \
  -F "file=@/path/to/test.pdf"
```

搜索：

```bash
curl -X POST https://example.com/ai-studio/v1/knowledge/search \
  -H "Content-Type: application/json" \
  -d '{"query":"测试关键词","topK":5}'
```

查看文档列表：

```bash
curl https://example.com/ai-studio/v1/knowledge/documents
```

查看统计：

```bash
curl https://example.com/ai-studio/v1/knowledge/stats
```

## 11. 数据备份

Knowledge 的核心数据都在 `knowledge/data/` 下，建议定期备份：

```bash
cd /opt/ai-studio-plus/knowledge
tar -czf knowledge-data-$(date +%F).tar.gz data
```

建议备份内容：

```text
data/biaoshu.db
data/vectors/
data/kb-input/
data/models/
```

恢复时停止服务，替换 `data/` 后再启动：

```bash
pm2 stop ai-studio-knowledge
tar -xzf knowledge-data-YYYY-MM-DD.tar.gz
pm2 start ai-studio-knowledge
```

## 12. 常见问题

### 12.1 首次启动很慢

通常是模型下载或模型加载导致。可以查看日志：

```bash
pm2 logs ai-studio-knowledge
```

如果卡在 HuggingFace 下载，启动时加：

```bash
HF_ENDPOINT=https://hf-mirror.com
```

### 12.2 Portal 搜索失败

检查 portal 配置：

```bash
NUXT_KNOWLEDGE_API_BASE=https://example.com/ai-studio/v1/knowledge
```

再确认服务器接口可用：

```bash
curl https://example.com/ai-studio/v1/knowledge/health
```

### 12.3 上传失败或 413

Nginx 默认上传大小可能太小，需要配置：

```nginx
client_max_body_size 100m;
```

### 12.4 重启后数据消失

检查 `knowledge/data/` 是否被部署流程覆盖或删除。生产部署时必须把 `data/` 当作持久化目录。

### 12.5 端口冲突

确认 `3002` 是否被占用：

```bash
lsof -i :3002
```

如果被占用，修改 `.env` 的 `PORT`，并同步调整 Nginx `proxy_pass`。

## 13. 推荐发版流程

```bash
cd /opt/ai-studio-plus
git pull
pnpm install
pnpm --filter @knowledgebase/doc-manager build
pm2 restart ai-studio-knowledge
pm2 logs ai-studio-knowledge
```

发版前不要删除：

```text
knowledge/data/
knowledge/lib/tessdata/
```
