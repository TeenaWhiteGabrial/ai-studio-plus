## ADDED Requirements

### Requirement: Banner 管理
管理员 SHALL 能够添加、编辑、删除、排序首页 Banner，每个 Banner SHALL 包含标题、图片链接、跳转地址、排序号。

#### Scenario: 新增 Banner
- **WHEN** 管理员填写 Banner 信息并保存
- **THEN** Banner 数据插入 `banner` 表，Portal 首页按排序展示

### Requirement: 推荐资源配置
管理员 SHALL 能够将某资源设为推荐，调整推荐资源的展示位置和排序。

### Requirement: 公告管理
管理员 SHALL 能够发布、编辑、撤回公告，公告 SHALL 包含标题、内容、发布时间、状态（草稿/已发布/已撤回）。

#### Scenario: 发布新公告
- **WHEN** 管理员填写公告内容后点击发布
- **THEN** 公告状态变为"已发布"，在 Portal 展示

### Requirement: 网站基本信息配置
管理员 SHALL 能够配置平台名称、Logo、描述、栏目等信息。

### Requirement: 首页运营模块权限
OP_ADMIN 和 SUPER_ADMIN 有权访问门户运营模块，DEPT_ADMIN 无权访问。
