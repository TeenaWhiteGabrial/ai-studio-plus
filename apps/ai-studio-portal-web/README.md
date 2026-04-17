# 从原始需求到完整项目 - 全工作流程设计

## 概述

本项目提供一套完整的 AI 驱动开发流程，将原始需求（几段文字描述）通过技能化工具链，转化为完整的全栈项目。

### 技术栈

| 端 | 技术栈 |
|---|--------|
| **门户端前端** | Nuxt 3 + TypeScript + Element Plus + UnoCSS |
| **管理端前端** | Vue 3 + TypeScript + Element Plus + UnoCSS |
| **后端** | Java (Spring Boot) |
| **数据库** | MySQL |

---

## 流程总览

```
┌──────────────┐    ┌──────────────┐    ┌──────────────┐    ┌──────────────┐
│  原始需求.md  │ →  │   PRD.md     │ →  │ 数据模型设计书 │ →  │  完整前端代码  │
│              │    │              │    │ 接口规格书.md │    │              │
└──────────────┘    └──────────────┘    └──────────────┘    └──────────────┘
     ↓                    ↓                    ↓                    ↓
  prd-generator          ↓              docs-generator       code-generator
                        ↓                    ↓                    ↓
                   产品需求规格书      技术设计文档            Web 应用代码
```

### 技能工具链

| 技能 | 输入 | 输出 | 目录 |
|------|------|------|------|
| `prd-generator` | 原始需求.md | PRD.md | `.claude/skills/prd-generator/` |
| `docs-generator` | PRD.md | 3.数据模型设计书.md<br>4.接口规格书.md | `.claude/skills/docs-generator/` |
| `code-generator` | PRD.md<br>3.数据模型设计书.md<br>4.接口规格书.md | 完整前端代码 | `.claude/skills/code-generator/` |

---

## 阶段一：需求结构化

**目标**：将原始需求转化为结构化的产品需求规格说明书（PRD）

**触发方式**：
```bash
/prd-generator
# 或将原始需求文档放置在 docs/1.原始需求.md 后执行技能
```

**输入**：`docs/1.原始需求.md`（原始需求文本）

**输出**：`docs/PRD.md`

### 阶段一输出内容

PRD.md 包含以下内容：

1. **项目概述** - 项目定位、目标用户
2. **用户角色定义** - 角色 ID、名称、描述、核心诉求
3. **功能模块清单** - 模块划分、优先级、功能点列表
4. **数据实体定义** - 实体名称、字段、类型、约束
5. **业务流程/用例分析** - 执行流程、异常处理
6. **非功能性需求** - 性能、安全、兼容性要求

### 示例

```markdown
# 工业人工智能技术支持社区平台 - 产品需求规格说明书

## 用户角色
| 角色 ID | 角色名称 | 描述 | 核心诉求 |
|--------|---------|------|---------|
| R01 | 平台运营人员 | 负责政策搜集、内容审核 | 高效管理政策内容 |
| R02 | 企业用户 | 获取政策和技术支持 | 及时获取政策信息 |

## 功能模块
### 模块 M01：政策服务系统
- M01-F01: 政策汇聚 (portal)
- M01-F02: 政策分类浏览 (portal)
- M01-F03: 政策推送服务 (portal)
```

---

## 阶段二：技术设计

**目标**：将 PRD 转化为可执行的技术设计文档

**触发方式**：
```bash
/docs-generator
```

**输入**：`docs/PRD.md`

**输出**：
- `docs/3.数据模型设计书.md`
- `docs/4.接口规格书.md`

### 阶段二输出内容

#### 3.数据模型设计书

1. **实体关系图 (ERD)** - ASCII 艺术展示表关系
2. **实体清单** - 表名、说明
3. **数据表详细定义**
   - 字段名、类型、约束、默认值
   - 索引设计
   - 外键关系
4. **数据字典** - 枚举值定义

#### 4.接口规格书

1. **接口清单**
   - 门户端接口（API-P-xx）
   - 管理端接口（API-A-xx）
2. **接口详细定义**
   - 请求参数、请求体
   - 响应结构、类型定义
3. **通用响应格式** - 统一 API 响应规范
4. **错误码定义** - 通用和业务错误码

### 示例

```markdown
# 数据模型设计书

## 表：policies
| 字段名 | 类型 | 约束 | 说明 |
|-------|------|------|------|
| id | BIGINT | PRIMARY KEY | 主键 |
| title | VARCHAR(500) | NOT NULL | 政策标题 |
| content | TEXT | NOT NULL | 政策内容 |
| status | VARCHAR(20) | NOT NULL | 状态 |

# 接口规格书

## API-P-01: 获取政策列表
- 方法：GET
- 路径：/api/policies
- 认证：无需
- 参数：page, limit, level, category
```

---

## 阶段三：代码生成

**目标**：根据技术设计文档生成完整的前端 Web 应用代码

**触发方式**：
```bash
/code-generator
```

**输入**：
- `docs/PRD.md` - 产品需求规格说明书
- `docs/3.数据模型设计书.md` - 数据模型设计
- `docs/4.接口规格书.md` - API 接口定义

**输出**：完整的前端代码

### 阶段三输出内容

#### 1. 类型定义 (`shared/types/`)

```typescript
// shared/types/policy.ts
export interface Policy {
  id: number
  title: string
  content: string
  status: PolicyStatus
  // ...
}
```

#### 2. Composables (`composables/`)

```typescript
// composables/usePolicy.ts
export function usePolicy() {
  const getPolicyList = (params: PolicyListParams) =>
    useSimpleFetch<PolicyListResponse>('/api/policies', { query: params })

  const getPolicyDetail = (id: number) =>
    useSimpleFetch<Policy>(`/api/policies/${id}`)

  return { getPolicyList, getPolicyDetail }
}
```

#### 3. Mock 数据 (`server/mock/`)

```typescript
// server/mock/policy.mock.ts
export function generatePolicyMock(count: number): Policy[] {
  // 生成模拟数据
}
```

#### 4. 页面组件 (`app/pages/`)

| 模块 | 门户页面 | 管理页面 |
|------|---------|---------|
| 政策 | `/policy` 列表<br>`/policy/:id` 详情 | `/admin/policy` 管理 |
| 知识 | `/knowledge` 列表<br>`/knowledge/:id` 详情 | `/admin/knowledge` 管理 |
| 问答 | `/question` 列表<br>`/question/:id` 详情 | `/admin/question` 管理 |

#### 5. 业务组件 (`components/`)

```
components/
├── Policy/
│   ├── PolicyCard.vue
│   ├── PolicyList.vue
│   └── PolicyForm.vue
├── Knowledge/
├── Question/
└── ...
```

---

## 快速开始

### 步骤 1：准备原始需求

将原始需求文档放置在 `docs/1.原始需求.md`

### 步骤 2：执行技能链

```bash
# 方式一：逐步执行，每步审查结果
/prd-generator          # 生成 PRD
# 审查 PRD.md，必要时修改
/docs-generator         # 生成技术文档
# 审查数据模型和接口规格
/code-generator         # 生成代码

# 方式二：文档已准备好，直接生成代码
/code-generator
```

### 步骤 3：启动项目

```bash
# 安装依赖
pnpm install

# 开发模式
pnpm run dev

# 生产构建
pnpm run build:prod
```

---

## 项目结构

```
industrial-vitesse-web-demo/
├── docs/                          # 文档目录
│   ├── 1.原始需求.md
│   ├── PRD.md
│   ├── 3.数据模型设计书.md
│   └── 4.接口规格书.md
├── .claude/skills/                # AI 技能目录
│   ├── prd-generator/
│   ├── docs-generator/
│   └── code-generator/
├── app/                           # 前端应用
│   ├── components/
│   ├── composables/
│   ├── pages/
│   └── stores/
├── server/                        # 服务端
│   ├── api/
│   └── mock/
├── shared/types/                  # 类型定义
└── ...
```

---

## 技能使用说明

### prd-generator

将原始需求转化为结构化 PRD 文档。

**触发词**：原始需求、需求文档、生成 PRD

### docs-generator

根据 PRD 生成数据模型设计书和接口规格书。

**触发词**：数据模型、接口文档、技术设计

### code-generator

根据三份文档生成完整前端代码。

**触发词**：生成代码、前端代码、Web 应用

---

## 开发规范

### 代码规范

- 使用 `<script setup lang="ts">` 语法
- 类型定义使用 `interface`
- 组件命名使用 PascalCase
- 文件命名使用 kebab-case

### 样式规范

- 使用 UnoCSS/Tailwind CSS 原子类
- 悬浮动效：`hover:scale-102` + `hover:shadow-lg`
- 可点击元素：`cursor-pointer`

### 接口调用规范

```typescript
// 外部接口
const { data } = await useSimpleFetch<ResponseType>('/api/endpoint')

// 内部 server 接口
const data = await $fetch('/api/endpoint')
```

---

## 文档版本

| 文档 | 版本 | 日期 |
|------|------|------|
| PRD.md | 1.0 | 2026-03-19 |
| 3.数据模型设计书.md | 1.0 | 2026-03-19 |
| 4.接口规格书.md | 1.0 | 2026-03-19 |

---

## 相关资源

- [Nuxt 3 文档](https://nuxt.com/docs)
- [Element Plus 文档](https://element-plus.org)
- [UnoCSS 文档](https://uno.antfu.me)
