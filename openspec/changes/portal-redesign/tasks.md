## 1. 数据库迁移

- [x] 1.1 新增 `article` 表（标题、内容、作者、阅读数、点赞数、评论数、状态、创建/更新时间、逻辑删除）
- [x] 1.2 新增 `article_tag` 表（article_id、tag_id 联合主键）
- [x] 1.3 新增 `question` 表（标题、内容、作者、标签、回答数、浏览数、状态、最佳答案ID、创建/更新时间、逻辑删除）
- [x] 1.4 新增 `answer` 表（question_id、作者、内容、点赞数、是否最佳、创建/更新时间、逻辑删除）
- [x] 1.5 新增 `community_comment` 表（comment_type、target_id、parent_id、root_id、作者、内容、点赞数、创建时间、逻辑删除；区别于 V4 的 `comment` 资源表）
- [x] 1.6 新增 `article_like` 表（user_id、article_id 联合唯一索引）
- [x] 1.7 新增 `answer_like` 表（user_id、answer_id 联合唯一索引）
- [x] 1.8 新增 `community_comment_like` 表（user_id、comment_id 联合唯一索引；区别于 V4 的 `like_record` 资源点赞表）
- [x] 1.9 新增 `favorite` 表扩展（V4 已建 `favorite` 表，本次迁移说明扩展支持 ARTICLE/QUESTION 类型；字段已支持无需 DDL）
- [x] 1.10 新增 `user_follow` 表（follower_id、following_id、created_at）
- [x] 1.11 新增 `tag` 表（id、name、type、use_count、created_at）
- [x] 1.12 新增 `browse_history` 表（user_id、target_type、target_id、created_at，+ 去重索引）
- [x] 1.13 新增 `notification` 表（user_id、type、content、source_id、source_type、is_read、created_at）
- [x] 1.14 在 `sys_user`、`skill`、`plugin` 表新增 `avatar`/`bio`/`favorite_count`/`followers_count` 字段；在 `article`、`question` 表新增 `favorite_count`/`followers_count` 字段
- [x] 1.15 编写 Flyway 迁移脚本 V11__portal_community_tables.sql 并验证执行成功

## 2. 后端 - 基础架构

- [x] 2.1 新增 `TagController`：GET /api/tag/list（获取标签列表，支持 type 筛选）、POST /api/tag（创建标签）
- [x] 2.2 新增 `AuthController` 登录接口确认：POST /api/auth/login（RSA解密 + BCrypt校验 + JWT签发）
- [x] 2.3 新增 `UserController` 扩展接口：GET /api/user/profile/[id]（用户公开主页数据）、PUT /api/user/profile（更新个人资料）
- [x] 2.4 配置 Spring Security：/api/auth/login、/portal/resource/** 公开访问；/api/article/**（GET公开，POST/DELETE需登录）；/api/favorite/**、/api/comment/**、/api/question/** 需登录
- [x] 2.5 新增 `JwtAuthenticationFilter` 对 Portal API 请求的 JWT 校验逻辑

## 3. 后端 - 社区功能（文章+问答）

- [x] 3.1 新增 `ArticleController`：GET /api/article/list、GET /api/article/[id]（+ views_count++）、POST /api/article、PUT /api/article/[id]、DELETE /api/article/[id]、POST /api/article/[id]/like、PUT /api/admin/article/[id]/takedown（管理员下架）
- [x] 3.2 新增 `ArticleService` 及实现类：文章 CRUD、标签关联管理、点赞逻辑
- [x] 3.3 新增 `QuestionController`：GET /api/question/list、GET /api/question/[id]（+ views_count++）、POST /api/question、PUT /api/question/[id]、DELETE /api/question/[id]、PUT /api/admin/question/[id]/takedown（管理员下架）
- [x] 3.4 新增 `AnswerController`：POST /api/question/[id]/answer、PUT /api/answer/[id]、DELETE /api/answer/[id]、PUT /api/answer/[id]/accept、POST /api/answer/[id]/like
- [x] 3.5 新增 `QuestionService` 及实现类：问题 CRUD、回答数管理、采纳逻辑
- [x] 3.6 新增 `AnswerService` 及实现类：回答 CRUD、点赞逻辑、最佳答案标记

## 4. 后端 - 评论与收藏

- [x] 4.1 新增 `CommentController`：GET /api/comment/list、POST /api/comment、DELETE /api/comment/[id]、POST /api/comment/[id]/like
- [x] 4.2 新增 `CommentService` 及实现类：评论 CRUD、二级嵌套逻辑、通知触发
- [x] 4.3 新增 `FavoriteController`：POST /api/favorite、DELETE /api/favorite、GET /api/favorite/list、GET /api/favorite/check
- [x] 4.4 新增 `FavoriteService` 及实现类：收藏 CRUD、去重检查、关联资源信息 JOIN 查询

## 5. 后端 - 浏览记录与通知

- [x] 5.1 新增 `BrowseHistoryController`：POST /api/browse-history、GET /api/browse-history/list、DELETE /api/browse-history/clear
- [x] 5.2 新增 `BrowseHistoryService` 及实现类：记录创建（去重逻辑）、列表查询（按时间分组）、清空记录
- [x] 5.3 新增 `NotificationController`：GET /api/notification/list、PUT /api/notification/read、GET /api/notification/unread-count
- [x] 5.4 新增 `NotificationService` 及实现类：通知创建（评论/回复/采纳触发）、已读标记

## 6. 前端 - 基础布局与认证

- [x] 6.1 重构 `layouts/default.vue` 为三栏布局：顶部导航栏（60px固定）+ 左侧边栏（240px）+ 主内容区（flex:1）+ 右侧边栏（300px）
- [x] 6.2 重构 `nuxt.config.ts`：新增路由中间件配置、页面 meta 信息
- [x] 6.3 新增 `middleware/auth.ts`（路由守卫：未登录访问 /profile/* 或 /community/write 或 /community/ask 时重定向 /login）
- [x] 6.4 重构 `composables/useUser.ts`：确保 SSR 下 localStorage 访问安全（import.meta.client 守卫）、login/logout/initUserInfo/isLoggedIn
- [x] 6.5 新增 `pages/login.vue`：用户名+密码表单，调用 POST /api/auth/login，成功后存 token 跳转
- [x] 6.6 Header 组件：根据 isLoggedIn() 显示登录按钮或用户头像下拉菜单（个人中心/退出登录）
- [x] 6.7 响应式适配：< 768px 隐藏左侧栏、< 1024px 隐藏右侧栏、< 768px 汉堡菜单收起导航

## 7. 前端 - 首页

- [x] 7.1 实现首页左侧边栏：资源分类导航（Skill / Plugin）+ 热门标签列表（调用后端标签统计接口）
- [x] 7.2 实现首页右侧边栏：热门文章 TOP 10 + 热门 Skill TOP 10 + 精选推荐卡片
- [x] 7.3 实现首页主内容区：本周精选大卡片 Section + 资源最新发布 Tab 切换（Skill / Plugin）
- [x] 7.4 首页内容对接后端 API：精选推荐调用 `/portal/public/recommended`（如后端暂未实现则显示静态占位）

## 8. 前端 - 资源中心

- [x] 8.1 新增 `pages/resources/index.vue`：资源中心首页（默认显示 Skill 列表），顶部 Tab 切换类型
- [x] 8.2 实现资源列表页：调用 GET /portal/resource/{type}/list（已存在接口），支持 keyword/tag/sort/pagination
- [x] 8.3 实现资源卡片组件：封面图+标题+描述+作者+下载数，支持 hover 效果
- [x] 8.4 实现资源详情页 `pages/resources/[type]/[id].vue`：对接 GET /portal/resource/{type}/{id}，显示下载按钮、收藏按钮、版本选择（Skill）
- [x] 8.5 实现资源下载：调用 GET /api/{type}/[id]/download，前端跳转 OSS 签名 URL 或触发下载
- [x] 8.6 实现资源收藏：调用 POST /api/favorite，收藏状态切换（前端按钮状态管理）
- [x] 8.7 实现资源列表左侧边栏：子分类筛选（category）+ 标签筛选 + 版本筛选
- [x] 8.8 实现资源列表右侧边栏：本周热门资源 + 相关推荐

## 9. 前端 - 技术社区

- [x] 9.1 新增 `pages/community/index.vue`：文章/问答列表页，顶部 Tab 切换，调用 GET /api/article/list 或 GET /api/question/list
- [x] 9.2 实现文章列表页：卡片展示（封面+标题+摘要+作者+阅读数+评论数），分页 + 排序切换
- [x] 9.3 实现问答列表页：问题卡片展示（标题+摘要+标签+回答数+浏览数），分页 + 排序切换
- [x] 9.4 新增 `pages/community/article/[id].vue`：文章详情页（SSR），调用 GET /api/article/[id]，渲染富文本内容
- [x] 9.5 新增 `pages/community/question/[id].vue`：问题详情页（SSR），调用 GET /api/question/[id]，展示问题和回答列表
- [x] 9.6 实现文章详情页点赞：调用 POST /api/article/[id]/like，前端按钮状态切换
- [x] 9.7 实现文章详情页收藏：调用 POST /api/favorite
- [x] 9.8 实现回答提交：富文本编辑器 + 调用 POST /api/question/[id]/answer，AJAX 追加到列表
- [x] 9.9 实现回答采纳：问题作者看到"采纳"按钮，调用 PUT /api/answer/[id]/accept
- [x] 9.10 新增 `pages/community/write.vue`：文章发布页（需登录），富文本编辑器 + 标签选择器
- [x] 9.11 新增 `pages/community/ask.vue`：提问发布页（需登录），标题 + 富文本编辑器 + 标签选择器

## 10. 前端 - 评论系统

- [x] 10.1 新增 `components/CommentList.vue`：评论列表组件，支持二级嵌套展示（缩进显示）
- [x] 10.2 新增 `components/CommentForm.vue`：评论输入组件，支持 Markdown 简化语法预览
- [x] 10.3 评论列表对接 GET /api/comment/list（按 comment_type + target_id）
- [x] 10.4 评论提交对接 POST /api/comment，一级评论和二级回复共用接口（通过 parent_id 区分）
- [x] 10.5 评论点赞对接 POST /api/comment/[id]/like
- [x] 10.6 评论删除：作者看到删除按钮，调用 DELETE /api/comment/[id]

## 11. 前端 - 个人中心

- [x] 11.1 新增 `pages/profile/index.vue`：个人中心主页，两栏布局，左侧导航 + 右侧内容 Tab
- [x] 11.2 实现"我的资源"子页：调用 GET /api/article/list（authorId=当前用户）+ GET /api/skill/list（createdBy=当前用户）等，显示资源列表和操作按钮
- [x] 11.3 实现"收藏夹"子页：调用 GET /api/favorite/list，支持按 target_type 切换子 Tab（全部/Skill/Plugin/文章/问答），支持取消收藏
- [x] 11.4 实现"浏览记录"子页：调用 GET /api/browse-history/list，按时间分组展示，支持清空
- [x] 11.5 实现"消息通知"子页：调用 GET /api/notification/list，显示通知列表，点击跳转对应内容，已读/未读状态
- [x] 11.6 新增 `pages/profile/settings.vue`：个人设置页，头像上传（调用 OSS 服务）+ 昵称修改 + 简介修改 + 密码修改

## 12. 集成与测试

- [ ] 12.1 端到端测试：注册用户（Admin 后台）→ Portal 登录 → 浏览资源 → 收藏 → 发布文章 → 评论 → 查看个人中心 → 退出登录
- [ ] 12.2 游客访问测试：未登录访问首页正常 → 未登录访问资源中心正常 → 未登录点击收藏重定向到登录页 → 未登录访问 /profile 重定向到登录页
- [ ] 12.3 SSR 测试：文章详情页 /community/article/[id] 使用 curl 验证 HTML 中包含文章标题（SEO 可索引）
- [ ] 12.4 性能测试：资源列表页分页加载 < 500ms，首页加载 < 2s
- [ ] 12.5 移动端适配测试：Chrome DevTools 移动端模式验证各页面响应式布局正确
