# Skill 管理功能规格

## 概述
Skill 管理功能支持用户上传 ZIP 包创建技能，支持版本控制，技能归属记录。

## 需求

### 功能需求

#### F1: 创建技能
- 用户可以创建新技能
- 上传 ZIP 包到 OSS
- 自动生成版本 1.0.0
- 记录创建者和部门信息

#### F2: 版本管理
- 支持发布新版本
- 自动生成版本号（根据升级类型）
- 支持 PATCH/MINOR/MAJOR 升级
- 记录版本变更日志

#### F3: 版本查看权限
- 超级管理员可查看所有版本
- 普通用户只能查看最新版本
- 非最新版本无法下载

#### F4: 删除管理
- 技能删除为软删除
- 保留历史版本和 OSS 文件
- 超级管理员可物理删除历史版本

#### F5: 下载统计
- 按技能统计下载次数
- 无需详细下载日志

### 非功能需求
- ZIP 包大小限制：100MB
- 上传凭证有效期：5分钟
- 下载链接有效期：5分钟
- 无需技能内容校验

## 数据模型

### Skill 实体
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 技能名称（唯一） |
| description | String | 描述 |
| category | String | 分类 |
| latestVersionId | Long | 最新版本ID |
| latestVersion | String | 最新版本号 |
| totalVersions | Integer | 版本总数 |
| createdBy | Long | 创建者ID |
| deptId | Long | 部门ID |
| downloadCount | Integer | 下载次数 |
| isDeleted | Integer | 软删除标记 |

### SkillVersion 实体
| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| skillId | Long | 技能ID |
| version | String | 版本号 |
| major/minor/patch | Integer | 版本号各部分 |
| versionNumber | Integer | 数字表示 |
| ossKey | String | OSS key |
| fileSize | Long | 文件大小 |
| changelog | String | 变更日志 |
| createdBy | Long | 发布者ID |

## 接口

见 design.md API 设计部分

## 权限矩阵

见 design.md 权限设计部分
