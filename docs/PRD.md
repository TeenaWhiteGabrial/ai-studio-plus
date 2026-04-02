---
version: 1.0
projectName: AI Studio 平台
generatedAt: 2026-03-24
---

# AI Studio 平台 - 产品需求规格说明书

## 1. 项目概述

### 1.1 项目定位

面向团队的 AI Coding 资源统一管理平台，集中管理 Skills、MCPs、Plugins、教程资源，并提供成员产出统计与数据看板功能。

### 1.2 用户角色

| 角色 ID | 角色名称 | 描述 | 核心诉求 |
|--------|---------|------|---------|
| R01 | 超级管理员（SUPER_ADMIN） | 系统最高权限管理员 | 管理用户/角色、全量资源管理、查看全部数据 |
| R02 | 普通管理员（ADMIN） | 资源管理人员 | 管理 Skill/MCP/Plugin/教程资源内容 |
| R03 | 普通用户（USER） | 团队成员 | 浏览下载资源、新增 Skill/MCP/Plugin/教程资源、修改和删除自己创建的资源、录入个人当天产出数据 |

---

## 2. 功能模块清单

### 模块 M01：Skill 技能管理

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `skill-management` |
| 优先级 | P0 |
| 描述 | Skill 技能文件的增删改查、批量导入、下载统计 |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M01-F01 | Skill 列表查询 | admin/portal | 作为用户，我希望分页浏览 Skill 列表，以便快速找到所需技能 | 支持分页、按名称关键词搜索、按分类/状态筛选，返回总条数 |
| M01-F02 | 创建 Skill | admin/portal | 作为用户，我希望创建新 Skill，以便向团队共享技能文件 | 填写名称、描述、分类、来源、SKILL.md 内容后保存成功；名称重复时返回错误提示 |
| M01-F03 | 编辑 Skill | admin/portal | 作为用户，我希望更新 Skill 的信息，以便保持内容最新 | 管理员可修改任意 Skill；普通用户只能修改自己创建的 Skill，操作他人资源返回 403 |
| M01-F04 | 删除 Skill | admin/portal | 作为用户，我希望删除单个或批量删除 Skill，以便移除过时内容 | 管理员可删除任意 Skill；普通用户只能删除自己创建的 Skill，操作他人资源返回 403 |
| M01-F05 | Skill 详情查询 | admin/portal | 作为用户，我希望查看 Skill 完整信息，以便了解使用方式 | 返回 Skill 全部字段；ID 不存在时返回 404 |
| M01-F06 | Skill 下载 | admin/portal | 作为用户，我希望下载 Skill 文件，以便在本地使用 | 返回文件内容，`download_count` 自动加 1 |
| M01-F07 | 批量导入 Skill | admin/portal | 作为用户，我希望通过 ZIP 批量导入 Skills，以便高效初始化资源库 | 解析 ZIP 内多个 SKILL.md 文件，返回成功数量和失败数量 |

#### 2.2 数据实体定义

**实体 E01：技能（skill）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| name | String | VARCHAR(100) | 是 | Skill 名称（唯一） | "git-commit" |
| description | String | VARCHAR(500) | 否 | 描述 | "自动提交代码" |
| category | String | VARCHAR(50) | 否 | 分类 | "前端开发" |
| source | String | VARCHAR(200) | 否 | 来源 | "官方" |
| content_oss_key | String | VARCHAR(500) | 否 | SKILL.md 文件 OSS Key | - |
| content_url | String | VARCHAR(500) | 否 | SKILL.md 文件访问 URL | - |
| status | Integer | TINYINT | 是 | 状态：1-正常，0-禁用 | 1 |
| download_count | Integer | INT | 是 | 下载次数 | 0 |
| created_by | Long | BIGINT | 是 | 创建人 ID | - |
| created_at | LocalDateTime | DATETIME | 是 | 创建时间 | - |
| updated_at | LocalDateTime | DATETIME | 是 | 更新时间 | - |

---

### 模块 M02：MCP 服务器管理

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `mcp-management` |
| 优先级 | P0 |
| 描述 | MCP 服务器配置的增删改查及连接测试 |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M02-F01 | MCP 列表查询 | admin/portal | 作为用户，我希望分页浏览 MCP 服务器列表，以便了解可用服务 | 支持分页、按名称搜索、按状态筛选，返回总条数 |
| M02-F02 | 创建 MCP 服务器 | admin/portal | 作为用户，我希望录入 MCP 服务器配置，以便团队统一使用 | 填写名称、描述、API 端点、认证类型、配置信息后保存成功 |
| M02-F03 | 更新 MCP 服务器 | admin/portal | 作为用户，我希望修改 MCP 配置，以便维护最新连接信息 | 管理员可修改任意 MCP；普通用户只能修改自己创建的 MCP，操作他人资源返回 403 |
| M02-F04 | 删除 MCP 服务器 | admin/portal | 作为用户，我希望删除废弃的 MCP 服务器，以便保持列表整洁 | 管理员可删除任意 MCP；普通用户只能删除自己创建的 MCP，操作他人资源返回 403 |
| M02-F05 | 测试连接 | admin | 作为管理员，我希望测试 MCP 服务器连通性，以便验证配置正确 | 返回连接状态（成功/失败）和响应时间；超时时返回错误信息 |

#### 2.2 数据实体定义

**实体 E02：MCP 服务器（mcp_server）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| name | String | VARCHAR(100) | 是 | 服务器名称 | "GitHub MCP" |
| description | String | VARCHAR(500) | 否 | 描述 | - |
| api_endpoint | String | VARCHAR(500) | 是 | API 端点地址 | "https://api.example.com" |
| auth_type | String | VARCHAR(50) | 是 | 认证类型 | "Bearer/ApiKey/None" |
| config_json | String | TEXT | 否 | 配置信息（JSON） | - |
| status | Integer | TINYINT | 是 | 状态：1-正常，0-禁用 | 1 |
| created_by | Long | BIGINT | 是 | 创建人 ID | - |
| created_at | LocalDateTime | DATETIME | 是 | 创建时间 | - |
| updated_at | LocalDateTime | DATETIME | 是 | 更新时间 | - |

---

### 模块 M03：Plugin 插件管理

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `plugin-management` |
| 优先级 | P0 |
| 描述 | Plugin 插件文件的上传、版本管理、下载统计 |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M03-F01 | Plugin 列表查询 | admin/portal | 作为用户，我希望分页浏览插件列表，以便找到可用插件 | 支持分页、按名称搜索、按类型/状态筛选，返回总条数 |
| M03-F02 | 上传 Plugin | admin/portal | 作为用户，我希望上传插件文件，以便向团队提供新插件 | 文件上传至 OSS 并创建记录成功；超过 100MB 返回错误提示 |
| M03-F03 | 更新 Plugin | admin/portal | 作为用户，我希望更新插件版本，以便团队使用最新版本 | 管理员可更新任意 Plugin；普通用户只能更新自己创建的 Plugin，操作他人资源返回 403 |
| M03-F04 | 删除 Plugin | admin/portal | 作为用户，我希望删除废弃插件，以便保持列表整洁 | 管理员可删除任意 Plugin；普通用户只能删除自己创建的 Plugin，操作他人资源返回 403 |
| M03-F05 | Plugin 下载 | admin/portal | 作为用户，我希望下载插件文件，以便在本地安装使用 | 返回文件下载链接，`download_count` 自动加 1 |

#### 2.2 数据实体定义

**实体 E03：插件（plugin）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| name | String | VARCHAR(100) | 是 | 插件名称 | "code-formatter" |
| description | String | VARCHAR(500) | 否 | 描述 | - |
| type | String | VARCHAR(50) | 否 | 插件类型 | "editor/build/lint" |
| version | String | VARCHAR(50) | 是 | 版本号 | "1.0.0" |
| file_oss_key | String | VARCHAR(500) | 是 | 文件 OSS Key | - |
| file_url | String | VARCHAR(500) | 是 | 文件访问 URL | - |
| file_size | Long | BIGINT | 否 | 文件大小（字节） | 1048576 |
| status | Integer | TINYINT | 是 | 状态：1-正常，0-禁用 | 1 |
| download_count | Integer | INT | 是 | 下载次数 | 0 |
| created_by | Long | BIGINT | 是 | 创建人 ID | - |
| created_at | LocalDateTime | DATETIME | 是 | 创建时间 | - |
| updated_at | LocalDateTime | DATETIME | 是 | 更新时间 | - |

---

### 模块 M04：教程管理

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `tutorial-management` |
| 优先级 | P1 |
| 描述 | 教程文档的分类管理、在线阅读，内容存储于 OSS |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M04-F01 | 教程列表查询 | admin/portal | 作为用户，我希望分页浏览教程列表，以便找到学习资料 | 支持分页、按标题搜索、按分类/标签筛选，返回总条数 |
| M04-F02 | 创建教程 | admin/portal | 作为用户，我希望创建新教程，以便向团队分享知识 | 填写标题、分类、标签、Markdown 内容后，内容存储至 OSS 并创建记录 |
| M04-F03 | 更新教程 | admin/portal | 作为用户，我希望更新教程内容，以便保持知识库最新 | 管理员可更新任意教程；普通用户只能更新自己创建的教程，操作他人资源返回 403 |
| M04-F04 | 删除教程 | admin/portal | 作为用户，我希望删除教程，以便移除过时内容 | 管理员可删除任意教程；普通用户只能删除自己创建的教程，操作他人资源返回 403 |
| M04-F05 | 教程详情查询 | admin/portal | 作为用户，我希望查看教程完整内容，以便在线学习 | 返回教程完整信息，`view_count` 自动加 1 |

#### 2.2 数据实体定义

**实体 E04：教程（tutorial）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| title | String | VARCHAR(200) | 是 | 教程标题 | "Claude Code 入门指南" |
| category | String | VARCHAR(50) | 否 | 分类 | "AI Coding" |
| tags | String | VARCHAR(200) | 否 | 标签（逗号分隔） | "Claude,入门,MCP" |
| content_oss_key | String | VARCHAR(500) | 是 | Markdown 文件 OSS Key | - |
| content_url | String | VARCHAR(500) | 是 | 文件访问 URL | - |
| status | Integer | TINYINT | 是 | 状态：1-正常，0-禁用 | 1 |
| view_count | Integer | INT | 是 | 浏览次数 | 0 |
| created_by | Long | BIGINT | 是 | 创建人 ID | - |
| created_at | LocalDateTime | DATETIME | 是 | 创建时间 | - |
| updated_at | LocalDateTime | DATETIME | 是 | 更新时间 | - |

---

### 模块 M05：用户权限管理

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `user-permission` |
| 优先级 | P0 |
| 描述 | 用户管理、角色分配、基于菜单的 RBAC 权限控制、JWT 认证 |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M05-F01 | 用户登录 | portal/admin | 作为用户，我希望通过用户名密码登录系统，以便使用平台功能 | 凭证正确返回 JWT Token 和用户信息；密码错误返回 401；账号禁用返回 403 |
| M05-F02 | 用户列表查询 | admin | 作为超级管理员，我希望查看所有用户，以便管理团队成员 | 支持分页、按用户名搜索、按部门筛选；普通用户无权访问 |
| M05-F03 | 创建用户 | admin | 作为超级管理员，我希望创建新用户，以便为新成员开通账号 | 密码加密存储；用户名重复时返回错误提示 |
| M05-F04 | 更新用户 | admin | 作为超级管理员，我希望修改用户信息和状态，以便维护账号 | 更新成功；支持启用/禁用账号 |
| M05-F05 | 删除用户 | admin | 作为超级管理员，我希望删除用户，以便清理离职成员账号 | 删除用户记录及 `sys_user_role` 关联记录 |
| M05-F06 | 分配角色 | admin | 作为超级管理员，我希望给用户分配角色，以便控制功能权限 | 更新用户角色关联成功 |
| M05-F07 | 获取菜单树 | portal/admin | 作为用户，我希望系统按我的权限展示菜单，以便快速导航 | SUPER_ADMIN 返回全部菜单；USER 返回资源浏览、资源新增、产出录入菜单（不含其他用户资源管理入口） |
| M05-F08 | 角色列表查询 | admin | 作为管理员，我希望查询所有角色，以便分配权限时选择 | 返回所有角色列表 |

#### 2.2 数据实体定义

**实体 E05：用户（sys_user）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| username | String | VARCHAR(50) | 是 | 用户名（唯一） | "zhangsan" |
| password | String | VARCHAR(200) | 是 | 密码（BCrypt 加密） | - |
| real_name | String | VARCHAR(50) | 否 | 真实姓名 | "张三" |
| department | String | VARCHAR(100) | 否 | 所属部门 | "前端研发组" |
| email | String | VARCHAR(100) | 否 | 邮箱 | - |
| status | Integer | TINYINT | 是 | 状态：1-正常，0-禁用 | 1 |
| created_at | LocalDateTime | DATETIME | 是 | 创建时间 | - |
| updated_at | LocalDateTime | DATETIME | 是 | 更新时间 | - |

**实体 E06：角色（sys_role）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| role_code | String | VARCHAR(50) | 是 | 角色编码（唯一） | "SUPER_ADMIN" |
| role_name | String | VARCHAR(50) | 是 | 角色名称 | "超级管理员" |
| created_at | LocalDateTime | DATETIME | 是 | 创建时间 | - |

**实体 E07：用户角色关联（sys_user_role）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| user_id | Long | BIGINT | 是 | 用户 ID | - |
| role_id | Long | BIGINT | 是 | 角色 ID | - |

**实体 E08：菜单（sys_menu）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| parent_id | Long | BIGINT | 否 | 父菜单 ID | 0 |
| name | String | VARCHAR(50) | 是 | 菜单名称 | "Skill 管理" |
| path | String | VARCHAR(200) | 否 | 路由路径 | "/skill" |
| permission | String | VARCHAR(100) | 否 | 权限标识 | "skill:list" |
| sort | Integer | INT | 否 | 排序号 | 1 |

**实体 E09：角色菜单关联（sys_role_menu）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| role_id | Long | BIGINT | 是 | 角色 ID | - |
| menu_id | Long | BIGINT | 是 | 菜单 ID | - |

---

### 模块 M06：成员产出统计

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `member-output` |
| 优先级 | P0 |
| 描述 | 成员每日 AI Coding 产出数据录入与查询，仅允许录入当天数据 |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M06-F01 | 查询我的今日产出 | portal | 作为用户，我希望查看今天的产出数据，以便了解录入情况 | 返回当天产出记录；未录入则返回空数据 |
| M06-F02 | 提交今日产出 | portal | 作为用户，我希望录入当天的产出数据，以便参与团队统计 | 保存成功；`stat_date` 非今天返回"仅允许录入当天数据"；重复提交则更新记录 |
| M06-F03 | 查询我的产出历史 | portal | 作为用户，我希望查看历史产出记录，以便回顾个人成长 | 按日期范围返回历史记录列表 |
| M06-F04 | 产出列表查询（管理员） | admin | 作为管理员，我希望查看所有成员的产出，以便掌握团队整体情况 | 按指定日期返回所有成员产出记录；普通用户无权访问 |
| M06-F05 | 产出统计数据 | admin/portal | 作为用户，我希望查看产出汇总统计，以便了解整体趋势 | 按日期范围返回总计、平均值等汇总数据 |

#### 2.2 数据实体定义

**实体 E10：成员产出（member_output）**

| 字段名 | Java 类型 | MySQL 类型 | 必填 | 说明 | 示例 |
|-------|----------|-----------|------|------|------|
| id | Long | BIGINT | 是 | 主键，自增 | 1 |
| user_id | Long | BIGINT | 是 | 用户 ID | - |
| stat_date | LocalDate | DATE | 是 | 统计日期（唯一键与 user_id 联合） | "2026-03-24" |
| prd_count | Integer | INT | 否 | PRD 文档数 | 2 |
| api_count | Integer | INT | 否 | API 接口数 | 10 |
| java_lines | Integer | INT | 否 | Java 代码行数 | 300 |
| frontend_lines | Integer | INT | 否 | 前端代码行数 | 200 |
| remark | String | VARCHAR(500) | 否 | 备注 | - |
| created_at | LocalDateTime | DATETIME | 是 | 创建时间 | - |
| updated_at | LocalDateTime | DATETIME | 是 | 更新时间 | - |

> **约束**：`UNIQUE KEY uq_user_date (user_id, stat_date)` 防止同一用户同一天重复录入。

---

### 模块 M07：数据看板

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `dashboard-analytics` |
| 优先级 | P1 |
| 描述 | 资源概览统计、产出趋势分析、成员排行榜、部门/个人详细统计 |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M07-F01 | 概览数据查询 | admin/portal | 作为用户，我希望看到资源总量概览，以便了解平台规模 | 返回 Skills/MCPs/Plugins/Tutorials 各自的总数和总下载量 |
| M07-F02 | 产出趋势查询 | admin/portal | 作为用户，我希望查看产出趋势，以便分析团队生产力变化 | 支持按天/周/月粒度查询，返回对应时间段每日/每月数据 |
| M07-F03 | 成员排行榜 | admin/portal | 作为用户，我希望查看产出排行榜，以便激励团队成员 | 按指定周期（本周/本月）返回产出最高的前 N 名成员 |
| M07-F04 | 详细统计查询 | admin | 作为管理员，我希望按部门或个人查看产出明细，以便精细化管理 | 支持按 `groupBy=department` 或 `groupBy=user` 返回统计数据 |

---

### 模块 M08：OSS 文件存储集成

| 配置项 | 值 |
|-------|-----|
| 模块代码 | `oss-integration` |
| 优先级 | P0 |
| 描述 | 基于七牛云存储的前端直传方案，后端生成上传凭证（UpToken），前端直传至七牛云，后端仅存储文件元数据 |

#### 2.1 功能点列表

| 功能 ID | 功能名称 | 归属端 | 用户故事 | 验收标准 |
|--------|---------|-------|---------|---------|
| M08-F01 | 获取上传凭证（UpToken） | admin/portal | 作为已登录用户，我希望获取七牛云上传凭证，以便将文件直传至七牛云 | 后端使用七牛云 SDK 生成 UpToken 并返回（含 token、domain、key 前缀）；未认证返回 401 |
| M08-F02 | 前端直传七牛云 | admin/portal | 作为用户，我希望使用 UpToken 直接上传文件，以便减少后端带宽占用 | 使用 UpToken 上传文件至七牛云成功，返回文件访问 URL（`domain + key`） |
| M08-F03 | OSS 文件删除 | admin | 作为系统，我希望删除资源时联动删除 OSS 文件，以便节省存储空间 | 删除指定 key 的文件成功 |

---

## 3. 业务流程 / 用例分析

### 用例 UC01：用户登录与菜单加载

| 配置项 | 值 |
|-------|-----|
| 关联功能 | M05-F01、M05-F07 |
| 参与者 | R01、R02、R03 |
| 前置条件 | 用户账号已创建且状态为正常 |

#### 执行流程

| 步骤 | 动作 | 系统响应 |
|------|------|---------|
| 1 | 用户输入用户名和密码，点击登录 | 验证凭证，生成 JWT Token，返回用户基本信息 |
| 2 | 前端存储 Token，请求菜单树 | 根据用户角色返回有权限的菜单列表 |
| 3 | 前端根据菜单动态生成路由 | 跳转至首页/数据看板 |

#### 异常处理

| 异常情况 | 处理方式 |
|---------|---------|
| 用户名或密码错误 | 返回 401，提示"用户名或密码错误" |
| 账号已被禁用 | 返回 403，提示"用户已被禁用" |
| Token 过期 | 前端跳转登录页，提示重新登录 |

---

### 用例 UC02：用户发布 Skill

| 配置项 | 值 |
|-------|-----|
| 关联功能 | M01-F02、M08-F01、M08-F02 |
| 参与者 | R02（ADMIN）、R03（USER） |
| 前置条件 | 用户已登录 |

#### 执行流程

| 步骤 | 动作 | 系统响应 |
|------|------|---------|
| 1 | 用户点击"新建 Skill" | 显示 Skill 创建表单 |
| 2 | 填写名称、描述、分类，上传 SKILL.md 文件 | 前端向后端请求七牛云 UpToken |
| 3 | 前端使用 UpToken 直接上传文件到七牛云 | 七牛云返回文件访问 URL |
| 4 | 前端提交表单（含 OSS key/url） | 后端创建 Skill 记录，返回成功 |

#### 异常处理

| 异常情况 | 处理方式 |
|---------|---------|
| 名称重复 | 返回错误提示"Skill 名称已存在" |
| OSS 上传失败 | 提示"文件上传失败，请重试" |
| 操作他人资源 | 返回 403 |

---

### 用例 UC03：成员录入今日产出

| 配置项 | 值 |
|-------|-----|
| 关联功能 | M06-F01、M06-F02 |
| 参与者 | R03（USER） |
| 前置条件 | 用户已登录 |

#### 执行流程

| 步骤 | 动作 | 系统响应 |
|------|------|---------|
| 1 | 用户进入"我的产出"页面 | 查询今日产出，若已有数据则回填表单 |
| 2 | 填写 PRD 数、API 数、Java 行数、前端行数 | 表单校验通过 |
| 3 | 点击提交 | 后端校验 `stat_date` 为今天；新建或更新产出记录，返回成功 |

#### 异常处理

| 异常情况 | 处理方式 |
|---------|---------|
| 提交非今天日期 | 返回错误"仅允许录入当天数据" |
| 重复提交 | 更新已有记录，不报错 |

---

### 用例 UC04：用户下载 Plugin

| 配置项 | 值 |
|-------|-----|
| 关联功能 | M03-F05 |
| 参与者 | R03（USER） |
| 前置条件 | 用户已登录，Plugin 状态为正常 |

#### 执行流程

| 步骤 | 动作 | 系统响应 |
|------|------|---------|
| 1 | 用户在插件列表点击"下载" | 向后端发起下载请求 |
| 2 | 后端处理下载请求 | `download_count` 加 1，返回 OSS 文件下载链接 |
| 3 | 前端触发浏览器下载 | 文件下载到本地 |

#### 异常处理

| 异常情况 | 处理方式 |
|---------|---------|
| 文件不存在（OSS 删除） | 返回 404，提示"文件不存在" |
| 未登录访问 | 返回 401 |

---

## 4. 非功能性需求

| 类别 | 要求 |
|------|------|
| 权限控制 | 基于菜单的 RBAC 模型，三级角色（SUPER_ADMIN / ADMIN / USER），后端 `@PreAuthorize` 注解控制，前端动态路由过滤；USER 角色可新增所有资源，可修改和删除自己创建的资源（`created_by = currentUserId`），操作他人资源返回 403 |
| 认证方式 | JWT Token，前端 Axios 请求头携带 `Authorization: Bearer <token>` |
| 日志记录 | 操作日志（`sys_oper_log`），记录用户操作行为 |
| 性能要求 | 首屏加载 < 2s；列表接口响应 < 500ms |
| 文件存储 | 所有文件资源（SKILL.md、Plugin 文件、Tutorial Markdown）存储至七牛云对象存储；前端直传模式（UpToken），后端仅存储 key 和访问 URL；文件大小限制 100MB |
| 数据完整性 | `member_output` 使用联合唯一键 `(user_id, stat_date)` 防止重复录入；成员产出 `stat_date` 后端强制校验为当天 |
| 浏览器兼容 | 支持 Chrome 90+、Edge 90+、Firefox 88+ |
| 部署要求 | 前端：Vue 3 打包后部署 Nginx；后端：Spring Boot JAR 独立运行；支持 Nginx 反向代理 |
| 扩展性 | 预留按钮级权限扩展能力；后期可对接 Git API 自动统计代码产出 |
| OSS 高可用 | 配置 OSS 多可用区；本地临时降级方案备用 |

---

## 5. 技术栈说明

| 层级 | 技术选型 |
|------|---------|
| 前端框架 | Vue 3 + TypeScript + Element Plus + Pinia + Vue Router |
| 前端构建 | Vite |
| 后端框架 | Java 17 + Spring Boot 3.x + MyBatis Plus |
| 安全认证 | Spring Security + JWT |
| 数据库 | MySQL 8.0+ |
| 文件存储 | 七牛云对象存储（Qiniu Cloud Storage） |
| 部署 | Nginx + JAR 独立部署 |

## 6.秘钥配置

存放于项目根目录的dev.config配置文件中