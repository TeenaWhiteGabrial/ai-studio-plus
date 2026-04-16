## ADDED Requirements

### Requirement: 个人中心页面

个人中心路由为 `/profile`，SHALL 仅对登录用户开放。未登录访问 SHALL 重定向到 /login?redirect=/profile。

### Requirement: 个人中心布局

个人中心采用两栏布局：左侧固定侧边栏（200px 宽，显示用户头像+昵称+个人简介+导航菜单）；右侧主内容区（显示当前选中 Tab 的内容）。

### Requirement: 个人中心导航

左侧导航 SHALL 包含：我的资源、收藏夹、浏览记录、消息通知、设置。每个菜单项点击后右侧内容区切换对应 Tab。

### Requirement: 我的资源 Tab

"我的资源" Tab SHALL 显示当前用户发布的所有资源（Skill/Plugin/教程）和社区内容（文章/问答），以 Tab 子页切换。每类内容以列表形式展示，显示资源名称/标题、发布时间、状态（已发布/草稿）、编辑/删除操作按钮。

### Requirement: 收藏夹 Tab

"收藏夹" Tab SHALL 显示当前用户收藏的所有内容。通过子 Tab 切换：全部 | Skill | Plugin | 教程 | 文章 | 问答。每个收藏项 SHALL 显示收藏时间 + 内容的基本信息（名称/标题/作者）。支持取消收藏操作。

### Requirement: 浏览记录 Tab

"浏览记录" Tab SHALL 显示当前用户的浏览历史，以时间分组（今天/昨天/更早）。每条记录显示资源/文章标题、类型、浏览时间。点击跳转对应详情页。"清空浏览记录"按钮 SHALL 清除当前用户所有浏览记录。

### Requirement: 消息通知 Tab

"消息通知" Tab SHALL 显示当前用户收到的系统通知：有人评论了我的文章/问题/回答、有人回复了我的评论、有人采纳了我的回答。通知 SHALL 按时间倒序，每条显示：通知类型图标、通知内容摘要、来源（评论者/回复者）、时间、已读/未读状态。点击通知 SHALL 跳转到对应内容页。未读通知 SHALL 标记为已读。

### Requirement: 通知已读 API

`PUT /api/notification/read` 接口 SHALL 将指定通知或所有未读通知标记为已读。请求体包含 notificationId（单个）或全部标记已读标识。

### Requirement: 个人设置页面

个人设置路由为 `/profile/settings`，SHALL 包含：头像上传（OSS）、昵称修改、简介修改（最多 200 字）、密码修改（需验证原密码）。

### Requirement: 个人设置 API

`PUT /api/user/profile` 接口 SHALL 仅对登录用户开放。更新 current_user 的 real_name、avatar、bio 字段。头像通过 OSS 上传后返回 OSS key 存储。

### Requirement: 用户主页

访问 `/profile/[userId]` SHALL 显示该用户的公开主页：头像、昵称、简介、粉丝数、关注数、发布的文章列表、发布的资源列表。任何访客均可访问（无需登录）。

### Requirement: 浏览记录数据模型

`browse_history` 表记录用户浏览行为：id、user_id、target_type（resource/article/question）、target_id、created_at。同一用户对同一目标在 30 分钟内的多次浏览只保留第一条记录（通过 (user_id, target_type, target_id, created_at) 去重）。

### Requirement: 浏览记录创建

用户访问资源/文章/问题详情页时，前端 SHALL 调用 `POST /api/browse-history` 记录浏览行为。后端根据去重规则决定是否插入新记录。
