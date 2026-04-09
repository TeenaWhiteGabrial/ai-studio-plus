# 设计文档: Skill OSS 版本管理系统

## 架构概览

```
┌─────────────────────────────────────────────────────────────┐
│                    Skill 版本管理架构                         │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  前端 (Vue.js)                                               │
│  ├── 技能列表/搜索                                           │
│  ├── 创建技能（上传 ZIP）                                     │
│  ├── 发布新版本                                              │
│  └── 版本管理（超管可见）                                     │
│         │                                                   │
│         ▼                                                   │
│  后端 (Spring Boot)                                          │
│  ├── SkillController                                         │
│  ├── SkillService                                            │
│  └── OSS 集成                                                │
│         │                                                   │
│         ▼                                                   │
│  存储层                                                      │
│  ├── MySQL (skill, skill_version 表)                         │
│  └── 七牛云 OSS (ZIP 包存储)                                  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## 数据模型

### 表结构

**skill 表（主表）**
- 基础信息：id, name, description, category
- 版本管理：latest_version_id, latest_version, total_versions
- 归属信息：created_by, dept_id
- 统计：download_count
- 软删除：is_deleted, deleted_at, deleted_by

**skill_version 表（版本表）**
- 关联：skill_id
- 版本号：version, major, minor, patch, version_number
- OSS：oss_key, oss_url, file_size
- 发布信息：changelog, created_by, created_at

## 权限设计

| 操作 | 超级管理员 | 管理员 | 创建者 | 其他 |
|------|----------|--------|--------|------|
| 查看技能 | ✅ | ✅ | ✅ | ✅ |
| 查看所有版本 | ✅ | ❌ | ❌ | ❌ |
| 创建技能 | ✅ | ✅ | ✅ | ❌ |
| 编辑技能 | ✅ | ✅ | 自己的 | ❌ |
| 删除技能 | ✅ | ✅ | 自己的 | ❌ |
| 删除历史版本 | ✅ | ❌ | ❌ | ❌ |

## 版本号策略

```
当前: 1.2.3

升级选择:
- 修订版本 (PATCH): 1.2.3 → 1.2.4
- 次要版本 (MINOR): 1.2.3 → 1.3.0
- 主要版本 (MAJOR): 1.2.3 → 2.0.0
```

## OSS 存储结构

```
ai-studio-bucket/
└── skills/
    ├── {skill-name}/
    │   ├── v1.0.0/
    │   │   └── skill.zip
    │   ├── v1.1.0/
    │   │   └── skill.zip
    │   └── v2.0.0/
    │       └── skill.zip
```

## 关键流程

### 创建技能
1. 前端获取 OSS 上传凭证
2. 前端直传 ZIP 包到 OSS
3. 提交创建请求（含 OSS key）
4. 后端创建 skill 记录 + 初始版本 1.0.0

### 发布新版本
1. 前端获取 OSS 上传凭证
2. 前端直传新 ZIP 包
3. 选择版本升级类型（PATCH/MINOR/MAJOR）
4. 后端自动生成版本号并创建版本记录
5. 更新 skill 主表最新版本信息

### 删除技能
- 软删除：标记 is_deleted=1，保留 OSS 文件
- 仅创建者/管理员可删除

### 删除版本
- 仅超级管理员可操作
- 物理删除 OSS 文件
- 删除数据库记录

## API 设计

### 管理端 API
- `GET /api/skills` - 技能列表
- `POST /api/skills` - 创建技能
- `GET /api/skills/{id}` - 技能详情
- `PUT /api/skills/{id}` - 更新技能
- `DELETE /api/skills/{id}` - 删除技能
- `GET /api/skills/{id}/versions` - 版本列表（权限控制）
- `POST /api/skills/{id}/versions` - 发布新版本
- `DELETE /api/skills/{id}/versions/{version}` - 删除版本（超管）
- `GET /api/skills/{id}/download` - 下载技能
- `GET /api/skills/upload-token` - 获取上传凭证

## 安全考虑

1. OSS 上传凭证有过期时间（5分钟）
2. 下载链接使用预签名 URL（5分钟有效）
3. 权限检查在服务端完成
4. 版本删除仅超管可操作
