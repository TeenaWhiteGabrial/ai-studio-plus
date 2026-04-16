## ADDED Requirements

### Requirement: 资源中心页面

资源中心路由为 `/resources`，SHALL 显示 Skill 和 Plugin 两类资源的列表页。本次实现范围仅包含 Skill 和 Plugin，其余类型（MCP/教程/安装包/视频）不在本次范围内。

### Requirement: 资源类型 Tab 切换

顶部 SHALL 显示资源类型 Tab：Skill | Plugin。点击切换到对应类型的资源列表，同时更新 URL 为 `/resources/skill`、`/resources/plugin`。

### Requirement: 资源列表查询

资源列表 SHALL 支持以下查询参数：keyword（关键词搜索）、tag（标签筛选）、sort（排序方式：最新/最热/下载最多）、page（页码）、size（每页条数，默认 20）。列表以卡片网格展示，每行 3-4 个卡片。

### Requirement: 资源卡片内容

每个资源卡片 SHALL 显示：资源封面图（无封面则显示类型图标+渐变背景）、资源名称（截断 2 行）、资源描述（截断 2 行）、作者名称、下载次数/查看次数、标签（最多显示 3 个）。卡片hover SHALL 向上偏移 4px 并显示阴影。

### Requirement: 资源详情页

资源详情路由为 `/resources/[type]/[id]`，SHALL 显示资源完整信息。左侧/上方展示资源详情（名称、描述、分类、标签、版本信息、作者信息、发布时间）；右侧/下方展示操作区（立即下载、收藏、分享）。页面底部展示版本历史列表（仅 Skill 有版本概念）。

### Requirement: 资源下载

用户点击"立即下载"按钮 SHALL 调用 `/api/[type]/[id]/download` 接口返回文件下载地址或直接下载。对于 OSS 存储的资源，后端返回签名 URL，前端跳转到该 URL 下载。下载时 SHALL 增加下载计数。

### Requirement: 资源收藏

已登录用户点击"收藏"按钮 SHALL 调用 `/api/favorite` POST 接口将资源加入收藏。收藏成功后显示提示"已收藏"。已收藏状态下按钮变为"已收藏（取消）"，再次点击取消收藏。游客点击 SHALL 重定向到登录页。

### Requirement: 资源列表左侧边栏筛选

资源列表左侧边栏 SHALL 支持：子分类筛选（如 Skill 下有"前端类""后端类"等子分类，数据来自资源表 category 字段）；标签筛选（多选，点击标签跳转到搜索结果页含标签）；版本筛选（仅 Skill 显示版本号选择器）。

### Requirement: 资源列表右侧边栏

资源列表右侧边栏 SHALL 显示：本周热门资源（按下载/查看数排序 TOP 5）、相关推荐（与当前列表页首个资源同标签的其他资源 3-5 个）。

### Requirement: 资源类型过滤权限

资源中心 SHALL 对所有访客开放，无需登录即可浏览和下载资源。
