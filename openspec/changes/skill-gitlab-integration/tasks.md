## 1. 数据库变更

- [x] 1.1 修改 `skill` 表：新增 `gitlab_repo`, `gitlab_path`, `gitlab_commit_sha`, `sync_status`, `last_sync_at`, `sync_error`, `is_deleted`, `author`, `tags` 字段
- [x] 1.2 修改 `skill` 表：标记 `content_oss_key`, `content_url` 为废弃（保留数据但不使用）
- [x] 1.3 创建 `skill_sync_log` 同步日志表
- [x] 1.4 创建 Flyway 迁移脚本（V4__skill_gitlab_integration.sql）

## 2. GitLab 集成模块

- [x] 2.1 添加 GitLab4J 或 RestTemplate 依赖
- [x] 2.2 创建 `GitLabConfig` 配置类（读取 application.yml 配置）
- [x] 2.3 创建 `GitLabClient` 接口和实现类
- [x] 2.4 实现获取仓库文件树方法（递归遍历 skills/ 目录）
- [x] 2.5 实现获取文件内容和 commit 信息方法
- [x] 2.6 实现获取文件 Raw 内容方法（用于下载）
- [x] 2.7 添加 GitLab 连接健康检查接口

## 3. Skill 同步服务

- [x] 3.1 创建 `SkillSyncService` 接口
- [x] 3.2 实现全量同步方法（遍历 GitLab 文件，解析 YAML，更新数据库）
- [x] 3.3 实现 YAML frontmatter 解析工具类
- [x] 3.4 实现同步日志记录功能
- [x] 3.5 添加同步异常处理（单文件失败不阻断整体同步）
- [x] 3.6 实现删除标记逻辑（GitLab 中不存在的文件标记为已删除）

## 4. 定时任务配置

- [x] 4.1 添加 @EnableScheduling 启用定时任务
- [x] 4.2 创建 `SkillSyncJob` 定时任务类
- [x] 4.3 实现可配置的同步周期（从配置文件读取 cron 表达式）
- [x] 4.4 添加同步锁防止并发执行

## 5. Skill Controller 改造

- [x] 5.1 移除 `create` 接口（创建 Skill）
- [x] 5.2 移除 `update` 接口（编辑 Skill）
- [x] 5.3 移除 `delete` 和 `deleteBatch` 接口（删除 Skill）
- [x] 5.4 移除 `importZip` 接口（ZIP 导入）
- [x] 5.5 改造 `download` 接口：改为从 GitLab 拉取内容返回
- [x] 5.6 新增 `raw` 接口：返回文件 Raw 内容（供 Claude Code 使用）
- [x] 5.7 新增 `sync` 接口：手动触发同步（仅管理员）
- [x] 5.8 新增 `syncLog` 接口：查询同步历史

## 6. Skill Service 改造

- [x] 6.1 移除 `createSkill`, `updateSkill`, `deleteSkill` 方法
- [x] 6.2 改造 `downloadSkill` 方法：改为返回 GitLab Raw 内容
- [x] 6.3 改造 `listSkills` 方法：过滤 is_deleted = 0
- [x] 6.4 新增 `getSkillRawContent` 方法

## 7. Skill Entity 和 Mapper 调整

- [x] 7.1 更新 `Skill` 实体类字段
- [x] 7.2 更新 `SkillMapper` 接口（新增 upsert 方法）
- [x] 7.3 更新 MyBatis XML 映射文件

## 8. 前端 Skill 页面重构

- [x] 8.1 移除新建 Skill 按钮和表单弹窗
- [x] 8.2 移除编辑和删除按钮
- [x] 8.3 移除批量导入按钮
- [x] 8.4 新增"从 GitLab 同步"按钮（仅管理员可见）
- [x] 8.5 新增同步状态展示（最后同步时间）
- [x] 8.6 新增"复制命令"按钮（生成 claude config add skill 命令）
- [x] 8.7 改造下载功能（调用新的下载接口）
- [x] 8.8 新增详情页 Markdown 内容预览

## 9. 前端同步配置页面

- [ ] 9.1 创建 SyncConfig.vue 配置页面
- [ ] 9.2 实现 GitLab 连接配置表单（URL、Token、仓库、分支）
- [ ] 9.3 实现同步周期配置
- [ ] 9.4 添加配置验证功能（测试连接按钮）
- [ ] 9.5 添加路由和菜单项（仅管理员可见）

## 10. API 接口调整

- [x] 10.1 更新 `api/index.ts`：移除 create/update/delete/importZip 方法
- [x] 10.2 更新 `api/index.ts`：改造 download 方法
- [x] 10.3 新增 `api/index.ts`：sync、syncLog、getRawContent 方法

## 11. 配置和部署

- [x] 11.1 添加 GitLab 配置到 application.yml
- [x] 11.2 添加环境变量注入配置（GITLAB_URL, GITLAB_TOKEN）
- [x] 11.3 更新部署文档

## 12. 测试和验证

- [ ] 12.1 编写 GitLabClient 单元测试
- [ ] 12.2 编写 SkillSyncService 单元测试
- [ ] 12.3 验证手动同步功能
- [ ] 12.4 验证定时同步功能
- [ ] 12.5 验证下载功能
- [ ] 12.6 验证 Raw 接口可被 Claude Code 访问
- [ ] 12.7 验证前端复制命令功能

## 13. 文档更新

- [ ] 13.1 更新接口规格书（移除创建/编辑/删除/ZIP导入接口）
- [ ] 13.2 更新数据模型设计书（Skill 表结构变更）
- [ ] 13.3 编写 GitLab 集成使用说明
- [ ] 13.4 编写 SKILL.md 文件规范（YAML frontmatter 格式）
