## Why

团队在使用 AI Coding 工具（Claude Code 等）过程中，Skills、MCP 服务器、Plugins、教程资源分散管理，成员产出数据缺乏统一录入与统计入口，导致资源复用率低、团队知识沉淀困难。需要一个统一的管理平台集中管理这些资源，并提供产出数据看板，提升团队整体 AI Coding 效能。

## What Changes

从零构建 AI Studio 平台，包含前端（Vue 3）和后端（Spring Boot 3）两个全新项目：

- **新增** Skill 技能管理：增删改查、批量 ZIP 导入、文件下载及下载次数统计
- **新增** MCP 服务器管理：服务器配置的增删改查、连接测试
- **新增** Plugin 插件管理：文件上传/下载、版本管理、下载次数统计
- **新增** 教程管理：Markdown 教程的增删改查、在线阅读、浏览次数统计
- **新增** 用户权限管理：JWT 认证、RBAC 三级角色（SUPER_ADMIN / ADMIN / USER）、基于菜单的动态权限
- **新增** 成员产出统计：每日 AI Coding 产出录入（PRD数、API数、Java行数、前端行数），仅允许录入当天数据
- **新增** 数据看板：资源概览、产出趋势、成员排行榜、部门/个人详细统计
- **新增** OSS 文件存储集成：七牛云前端直传方案（UpToken），后端仅存储文件元数据

## Capabilities

### New Capabilities

- `skill-management`: Skill 技能文件的增删改查、批量导入、下载统计
- `mcp-management`: MCP 服务器配置的增删改查及连接测试
- `plugin-management`: Plugin 插件文件上传、版本管理、下载统计
- `tutorial-management`: 教程文档分类管理、在线阅读，内容存储于 OSS
- `user-permission`: 用户管理、角色分配、基于菜单的 RBAC 权限控制、JWT 认证
- `member-output`: 成员每日 AI Coding 产出数据录入与查询
- `dashboard-analytics`: 资源概览统计、产出趋势分析、成员排行榜、部门/个人详细统计
- `oss-integration`: 七牛云对象存储集成，前端直传（UpToken）方案

### Modified Capabilities

（无，从零构建）

## Impact

- **新建后端项目**：`ai-studio-backend`，Spring Boot 3.x + MyBatis Plus，暴露 REST API
- **新建前端项目**：`ai-studio-frontend`，Vue 3 + TypeScript + Element Plus + Vite
- **数据库**：MySQL 8.0+，10 张业务表（skill、mcp_server、plugin、tutorial、member_output、sys_user、sys_role、sys_user_role、sys_menu、sys_role_menu）+ 1 张操作日志表（sys_oper_log）
- **外部依赖**：七牛云对象存储（Qiniu Java SDK），需配置 AccessKey / SecretKey / Bucket
- **部署**：前端 Nginx 静态托管，后端 JAR 独立运行，Nginx 反向代理 `/api` 到后端
