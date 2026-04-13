## ADDED Requirements

### Requirement: 收藏资源（需登录）
认证用户 SHALL 能够收藏喜欢的资源，收藏数 +1。

#### Scenario: 已登录用户收藏 Skill
- **WHEN** 已登录用户点击 Skill 详情页的收藏按钮
- **THEN** 数据插入 `favorite` 表，`skill.favorite_count + 1`

#### Scenario: 未登录用户点击收藏
- **WHEN** 未登录用户点击收藏按钮
- **THEN** 前端跳转至登录页，登录成功后回顾资源详情页

### Requirement: 取消收藏
已登录用户 SHALL 能够取消收藏，收藏数 -1。

### Requirement: 点赞资源（需登录）
认证用户 SHALL 能够对资源点赞（不能重复点赞），点赞数 +1。

### Requirement: 评论互动（需登录）
认证用户 SHALL 能够在资源详情页发表评论，其他用户能够查看评论列表。

#### Scenario: 发表评论
- **WHEN** 已登录用户在资源详情页填写评论内容并提交
- **THEN** 评论数据插入数据库，在评论列表中展示

### Requirement: 分享资源
用户 SHALL 能够生成分享链接或二维码，将资源分享给其他人。

### Requirement: 登录后回顾上一页
用户从 Portal 未登录状态点击需认证操作后跳转登录，登录成功后 SHALL 自动回到前一页面。
