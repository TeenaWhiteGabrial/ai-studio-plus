## ADDED Requirements

### Requirement: 资源中心支持六类资源
Console 资源中心 SHALL 支持 Skill、MCP、Plugin、教程、安装包、视频六类资源，每类资源独立建表，支持多版本管理（视频除外）。

### Requirement: 浏览所有审核通过的资源
用户 SHALL 能够浏览所有已通过审核的资源（全部 Skill/MCP/Plugin/教程/安装包/视频），支持关键词搜索、标签筛选、排序（最新/最多下载/最多收藏）。

#### Scenario: 浏览 Skill 列表
- **WHEN** 用户访问 Skill 列表页
- **THEN** 系统展示所有 `status=已通过` 的 Skill，支持搜索/筛选/排序

### Requirement: 上传新资源
认证用户 SHALL 能够上传新资源，填写信息后提交审核，资源状态为"待审核"。

#### Scenario: 上传 Skill
- **WHEN** 用户填写 Skill 名称、描述、上传文件后点击提交
- **THEN** Skill 数据插入 `skill` 表，版本数据插入 `skill_version` 表，状态均为"待审核"

### Requirement: 管理我的上传
用户 SHALL 能够查看自己上传的资源及其审核状态，支持编辑、删除（仅待审核状态可删）、重新提交（被拒绝后）。

### Requirement: 版本管理
资源作者 SHALL 能够发布新版本（填写版本号、更新说明、上传文件），新版本提交后状态为"待审核"，审核通过后更新 `current_version`。

#### Scenario: 发布 Skill 新版本
- **WHEN** 作者选择 Skill 并上传新版本文件、填写版本号和更新说明后提交
- **THEN** 版本数据插入 `skill_version` 表，`status=待审核`；审核通过后 `skill.current_version` 更新

### Requirement: 版本历史查看
用户 SHALL 能够查看任意资源的所有版本历史，显示版本号、发布时间、审核状态。

### Requirement: 下载资源
用户 SHALL 能够下载资源（当前版本或指定历史版本），下载次数 +1。

### Requirement: 收藏资源
认证用户 SHALL 能够收藏自己喜欢的资源，收藏数 +1，同一资源不可重复收藏。

#### Scenario: 收藏 Skill
- **WHEN** 已登录用户点击 Skill 详情页的收藏按钮
- **THEN** 数据插入 `favorite` 表，`skill.favorite_count + 1`

### Requirement: 取消收藏
已收藏用户 SHALL 能够取消收藏，收藏数 -1。

### Requirement: 收藏列表查看
用户 SHALL 能够查看自己的所有收藏列表，支持按资源类型筛选。

### Requirement: 点赞资源
认证用户 SHALL 能够对资源点赞（不能重复点赞），点赞数 +1。

### Requirement: 资源文件规格
各资源类型 SHALL 严格遵守文件格式和大小限制：
| 类型 | 格式 | 大小限制 |
|------|------|---------|
| Skill | .zip | 20MB |
| MCP | .json/.yaml | 1MB |
| Plugin | .vsix/.jar/.zip | 50MB |
| 教程 | .md/.pdf/.zip | 20MB |
| 安装包 | .exe/.msi/.dmg/.pkg/.deb/.rpm/.AppImage | 200MB |
| 视频 | .mp4/.webm/.zip | 500MB |
