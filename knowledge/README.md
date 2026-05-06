# 文档管理系统

基于向量检索的离线文档知识库管理系统。

## 需求

### 功能需求
1. **文档上传与管理**
   - 支持 PDF、DOCX、XLSX、TXT、MD 等格式
   - 通过 API 上传文档
   - 文档列表查看、删除

2. **文档处理**
   - 文档解析：提取文本内容
   - 文档分片：按规则分割为 chunks
   - 向量化：使用本地模型生成向量嵌入

3. **向量检索**
   - 语义搜索：输入查询文本，返回相似文档
   - 基于余弦相似度排序

4. **Web 管理界面**
   - 文档列表
   - 上传文档
   - 搜索
   - 统计信息

### 技术约束
- **完全离线**：不依赖任何外部 API
- **本地模型**：使用 `@xenova/transformers` 的 `bge-small-zh-v1.5` 模型
- **纯向量检索**：不支持图检索、混合检索

## 详细设计

### 架构

```
┌─────────────────────────────────────────────────────────────┐
│                      Web 管理界面                            │
│                  (HTML + JavaScript)                         │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP
┌──────────────────────────▼──────────────────────────────────┐
│                    Fastify API 服务                          │
│               (src/api/server.ts)                            │
│                                                             │
│  /health       - 健康检查                                    │
│  /api/kb/search        - 向量搜索                           │
│  /api/kb/upload        - 文档上传                           │
│  /api/kb/documents     - 文档列表                           │
│  /api/kb/stats         - 统计信息                           │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│               KnowledgeBaseManager                          │
│                  (src/kb/kb-manager.ts)                     │
│                                                             │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  文档解析    │  │  文档分片   │  │  向量嵌入   │        │
│  │document-    │  │document-    │  │ embedding.ts│        │
│  │ parser.ts   │  │ processor.ts│  │             │        │
│  └─────────────┘  └─────────────┘  └─────────────┘        │
└──────────────────────────┬──────────────────────────────────┘
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                      存储层                                  │
│                                                             │
│  ┌─────────────────┐    ┌─────────────────┐                │
│  │   向量存储       │    │   SQLite 数据库  │                │
│  │ sqlite-vector-  │    │  kb-file.       │                │
│  │ store.ts        │    │  repository.ts  │                │
│  └─────────────────┘    └─────────────────┘                │
└─────────────────────────────────────────────────────────────┘
```

### 模块设计

#### 1. API 服务 (`src/api/server.ts`)

| 接口 | 方法 | 参数 | 返回 |
|------|------|------|------|
| `/health` | GET | - | `{status, timestamp, uptime}` |
| `/api/kb/search` | POST | `{query, topK?, filters?}` | 搜索结果列表 |
| `/api/kb/upload` | POST | FormData (file) | `{id, filename, path, size}` |
| `/api/kb/documents` | GET | - | 文档列表 |
| `/api/kb/documents/:id` | GET | - | 文档详情 |
| `/api/kb/documents/:id` | DELETE | - | 删除结果 |
| `/api/kb/documents/:id/reindex` | POST | - | 重新索引 |
| `/api/kb/stats` | GET | - | 统计信息 |
| `/api/config` | GET | - | 配置信息 |

#### 2. 知识库管理器 (`src/kb/kb-manager.ts`)

```typescript
class KnowledgeBaseManager {
  // 初始化
  async initialize(): Promise<void>

  // 索引文件
  async indexFile(filePath: string, fileId?: string): Promise<void>

  // 删除索引
  async removeFileIndex(fileId: string): Promise<void>

  // 语义搜索
  async search(query: string, topK: number = 10): Promise<SearchResult[]>

  // 获取统计
  async getStats(): Promise<Stats>

  // 释放资源
  async dispose(): Promise<void>
}
```

#### 3. 向量模型 (`src/kb/embedding.ts`)

- 使用 `Xenova/bge-small-zh-v1.5` 本地模型
- 向量维度：384
- 支持批量处理

#### 4. 文档处理 (`src/kb/document-parser.ts`, `document-processor.ts`)

- **解析器**：支持 PDF、DOCX、XLSX、TXT、MD
- **分片器**：按段落和最大长度分片，支持重叠

#### 5. 向量存储 (`src/vector/sqlite-vector-store.ts`)

- 基于 SQLite 存储向量
- 支持添加、搜索、删除

### 数据流

```
上传文档
    ↓
document-parser 解析文本
    ↓
document-processor 分片 chunks
    ↓
embedding 生成向量
    ↓
sqlite-vector-store 存储
    ↓
kb-file-repository 记录元数据
```

```
搜索请求
    ↓
embedding 生成查询向量
    ↓
sqlite-vector-store 相似度搜索
    ↓
返回结果
```

### 数据存储

```
data/
├── vectors/           # 向量数据 (SQLite)
└── db/                # SQLite 数据库
    └── biaoshu.db     # 文档元数据
```

### 配置项 (.env)

```bash
PORT=3000
HOST=0.0.0.0
VECTOR_STORE_PATH=./data/vectors
EMBEDDING_MODEL=Xenova/bge-small-zh-v1.5
EMBEDDING_DIMENSION=384
LOG_LEVEL=info
```

## 快速开始

```bash
# 安装依赖
cd @knowledgebase
npm install

# 启动服务
npm run dev

# 访问管理界面
http://localhost:3000
```

## API 使用示例

### 上传文档
```bash
curl -X POST http://localhost:3000/api/kb/upload \
  -F "file=@/path/to/document.pdf"
```

### 搜索
```bash
curl -X POST http://localhost:3000/api/kb/search \
  -H "Content-Type: application/json" \
  -d '{"query": "查询关键词", "topK": 5}'
```

### 获取文档列表
```bash
curl http://localhost:3000/api/kb/documents
```

### 获取统计
```bash
curl http://localhost:3000/api/kb/stats
```