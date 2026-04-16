## Why

当前 AI Studio Portal 定位不清晰，仅有静态页面，无法满足用户资源发现和社区交流需求。与 CSDN 等成熟技术社区相比，缺少技术博客、问答互动、收藏分享等核心功能。同时 Portal 与 Admin/Console 用户体系割裂，需独立维护。亟需重新设计 Portal 端架构，打造统一的技术资源与社区平台。

## What Changes

- **全新三栏 CSDN 风格布局**：顶部导航 + 左侧分类导航 + 中间主内容区 + 右侧热门排行/推荐栏
- **去掉 Banner**：首页不再需要轮播 Banner
- **资源中心重构**：Skill 和 Plugin 两类资源（本次范围），支持分类筛选、标签筛选、搜索、分页
- **新建技术社区**：
  - 技术博客（文章发布、阅读、评论、点赞）
  - 问答系统（发布提问、回答、采纳、评论、点赞）
  - 标签系统（文章/问题标签筛选）
  - 作者体系（所有注册用户可发文）
- **收藏功能扩展**：资源（Skill/Plugin）和社区内容（文章/问题）统一收藏，个人中心分类展示
- **个人中心**：我的资源 / 收藏夹 / 浏览记录 / 消息通知 / 个人设置
- **统一认证体系**：Portal 与 Admin/Console 共用一套用户体系，Portal 需登录访问（/login 复用 Admin 登录页）
- **蓝紫色系**：主色 #667eea，深色 #764ba2，背景 #f5f7fa

## Capabilities

### New Capabilities

- `portal-home`: 首页（资源精选 + 社区最新文章混排，三栏布局）
- `portal-resources`: 资源中心（Skill 和 Plugin 列表 + 详情页，含收藏功能，无评论；本次仅实现此两类）
- `portal-community`: 技术社区（文章列表 + 文章详情 + 提问列表 + 问题详情，含评论、点赞）
- `portal-article`: 技术博客系统（文章发布/编辑/删除，富文本编辑器支持）
- `portal-question`: 问答系统（提问/回答/采纳，富文本编辑器支持）
- `portal-comment`: 评论系统（文章/问题/回答下的评论，含点赞）
- `portal-favorite`: 统一收藏（支持 Skill/Plugin/Article/Question，按类型分组展示）
- `portal-profile`: 个人中心（我的资源/收藏/浏览记录/消息通知/设置）
- `portal-auth`: Portal 认证（与 Admin 共用登录页，页面级 auth guard）

### Modified Capabilities

- `admin-user-management`: 无需修改，后台创建用户功能已完备，Portal 直接复用

## Impact

- **前端**：`apps/ai-studio-portal-web` 需全面重构，新建页面：/resources、/community、/community/article/[id]、/community/question/[id]、/community/ask、/community/write、/profile、/profile/settings；复用 /login；废弃现有 /search、/resource/[type]/ 页面
- **后端**：`ai-studio-service` 需新增 Controller 和 Service：ArticleController、QuestionController、AnswerController、CommentController、FavoriteController；新增数据库表：article、question、answer、comment、favorite、user_follow、tag、article_tag、question_tag、browse_history、notification；本次范围仅包含 Skill 和 Plugin 资源，不包含 MCP/教程/安装包/视频
- **数据库**：新增 11 张表，参考设计文档数据模型章节
- **认证**：复用现有 JWT + BCrypt 认证体系，Portal 前端页面加 auth guard，调用现有 `/api/auth/login`
- **API 路由**：`/portal/resource/*` 公开可访问；`/api/article/*`、`/api/question/*`、`/api/favorite/*` 需登录
