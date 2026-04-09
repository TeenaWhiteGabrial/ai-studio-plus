# 任务列表: Skill OSS 版本管理系统

## 数据库层 ✓

- [x] **DB-1**: 修改 skill 表结构
  - [x] 添加版本管理字段 (latest_version_id, latest_version, total_versions)
  - [x] 添加归属字段 (dept_id)
  - [x] 添加软删除字段 (is_deleted, deleted_at, deleted_by)
  - [x] 移除 GitLab 相关字段 (gitlab_repo, gitlab_path, gitlab_commit_sha, sync_status, last_sync_at, sync_error)
  - [x] 保留 author/tags 字段
  - [x] 添加索引

- [x] **DB-2**: 创建 skill_version 表
  - [x] 创建表结构
  - [x] 添加外键约束
  - [x] 添加索引

- [x] **DB-3**: 数据迁移脚本
  - [x] 迁移脚本 V10__skill_oss_version_management.sql

## 后端层 ✓

- [x] **BE-1**: 修改 Skill 实体类
  - [x] 添加新字段 (版本管理、软删除)
  - [x] 移除废弃字段 (GitLab 相关)

- [x] **BE-2**: 创建 SkillVersion 实体类
  - [x] 定义所有字段
  - [x] 版本号解析逻辑

- [x] **BE-3**: 创建 SkillVersionMapper
  - [x] 基础 CRUD
  - [x] 查询最新版本方法
  - [x] 查询版本列表方法

- [x] **BE-4**: 修改 SkillMapper
  - [x] 添加软删除过滤
  - [x] 移除 GitLab 相关方法

- [x] **BE-5**: SkillService 版本管理
  - [x] 版本创建逻辑
  - [x] 版本删除逻辑
  - [x] 版本查询逻辑

- [x] **BE-6**: 重构 SkillService
  - [x] 修改创建技能逻辑（支持 ZIP 上传）
  - [x] 添加版本发布方法
  - [x] 添加版本号生成逻辑
  - [x] 修改删除逻辑（软删除）
  - [x] 添加权限检查

- [x] **BE-7**: 重构 SkillController
  - [x] 修改创建接口
  - [x] 添加版本发布接口
  - [x] 添加版本列表接口
  - [x] 添加版本删除接口
  - [x] 修改下载接口
  - [x] 添加上传接口

- [x] **BE-8**: OSS 集成
  - [x] 配置 OssConfig (AWS SDK 2.x)
  - [x] 实现 OssService
  - [x] 上传文件到浪潮云 OSS

- [x] **BE-9**: 清理废弃代码
  - [x] 删除 GitLab 同步相关代码
  - [x] 删除 SkillSyncService
  - [x] 删除 SkillSyncJob

## 前端层 ✓

- [x] **FE-1**: 修改技能列表页面
  - [x] 移除 GitLab 同步按钮
  - [x] 显示版本数量
  - [x] 显示创建者
  - [x] 下载按钮使用 oss_url

- [x] **FE-2**: 创建技能表单
  - [x] 基础信息输入（名称、描述、分类）
  - [x] ZIP 文件上传（后端代理上传 OSS）
  - [x] 创建提交

- [x] **FE-3**: 技能详情页面
  - [x] 显示最新版本信息
  - [x] 显示版本历史

- [x] **FE-4**: 发布新版本功能
  - [x] ZIP 上传
  - [x] 版本升级类型选择（PATCH/MINOR/MAJOR）
  - [x] 版本号预览
  - [x] Changelog 输入

- [x] **FE-5**: API 接口
  - [x] 添加版本相关接口
  - [x] 添加上传接口
  - [x] 适配 snake_case 命名

## 状态

**已完成** - 核心功能已全部实现并测试通过
