## Context

AI Studio Portal 当前仅有静态页面，资源列表/详情/搜索均未对接后端 API，Banner 等功能为 TODO。CSDN 风格三栏布局需全面重构页面结构，同时新增技术社区（文章+问答）系统，Portal 用户体系需与 Admin/Console 统一。

**当前状态：**
- 前端：Nuxt 3 SSR，4个页面（首页/搜索/资源列表/资源详情），大量静态数据未联调
- 后端：PortalController 仅 Skill/Plugin/教程 3 类资源可用，其余均为 TODO
- 认证：Portal 面向所有访客，与 Admin/Console 用户体系完全割裂

**约束：**
- 技术栈不变：Nuxt 3 + Element Plus + Spring Boot + MyBatis Plus
- 共用数据库和用户体系，不新建用户表
- 文章和问答系统同步建设，不分期

## Goals / Non-Goals

**Goals:**
- 实现 CSDN 风格三栏布局（顶栏导航 + 左侧边栏 + 中主内容 + 右侧边栏）
- 建设完整技术社区：文章发布/阅读/评论/点赞 + 问答发布/回答/采纳/评论/点赞
- 资源中心支持六大资源类型 + 统一收藏功能
- Portal 需登录访问，个人中心展示用户资源/收藏/浏览记录
- 蓝紫色系主调（#667eea）

**Non-Goals:**
- 不新建注册流程（管理员后台创建）
- 复用 Admin 登录页面的 UI
- 不实现私信/消息推送系统（仅站内通知）
- Skill/Plugin 不支持评论（只支持收藏）
- Banner 功能不在本次范围内，移除Banner功能

## Decisions

### D1：三栏布局采用 CSS Flexbox + 固定宽度侧边栏

**决定**：左侧边栏 240px 固定，右侧边栏 300px 固定，中间主内容区 flex:1 最大宽度 1400px 居中。

**理由**：Nuxt 3 + Element Plus 组合，Flexbox 布局响应式控制最灵活。固定侧边栏在滚动时保持可见，符合 CSDN 浏览体验。媒体查询在 < 1024px 时隐藏右侧栏，< 768px 时隐藏左侧栏。

**替代方案**：
- Grid 布局：代码复杂度和 Flexbox 相当，但响应式断点控制不如 Flexbox 直观
- 纯 CSS 两栏（float）：已被 Flexbox 取代

### D2：技术社区内容与资源中心完全独立表

**决定**：文章系统使用独立 `article` 表，问答系统使用 `question` + `answer` 表，不复用 Tutorial 表。

**理由**：Tutorial（教程）是资源类型，有版本管理和审核流程，属于 Console 管理范围。社区文章/问答是用户生成内容，发布即上线（无需审核或轻审核），两者业务逻辑差异大，合并会导致复杂度激增。

**替代方案**：
- 复用 Tutorial 表：通过 type 字段区分"教程"和"博客文章" → 业务语义混乱，审核逻辑冲突
- 只建 article 表，问答用 article.type='question' → 不利于独立扩展（如回答采纳机制）

### D3：收藏表使用单一 polymorphic 关联

**决定**：`favorite` 表通过 `target_type` + `target_id` 两个字段关联任意资源/内容类型。

**理由**：符合 RESTful polymorphic 关联设计，新增资源类型无需改表结构。查询时 WHERE target_type='article' AND target_id=? 即可。

**替代方案**：
- 每类资源独立收藏表（favorite_skill/favorite_article） → 表爆炸，查询复杂
- JSONB 字段存储 target_ids → 不符合 MySQL 范式，索引效率低

### D4：评论采用单表自关联（parent_id 实现嵌套）

**决定**：`comment` 表通过 `parent_id` 实现二级嵌套（不支持无限嵌套），`comment_type` 区分文章/问题/回答下的评论。

**理由**：大多数社区评论嵌套不超过二级，单表自关联实现简单且性能可控。

**替代方案**：
- 无限嵌套树（Closure Table） → 实现复杂，大多数场景不需要
- JSONB 路径存储 → 同 D3 问题

### D5：Portal 登录页直接复用 Admin 登录页 UI，调用同一套 `/api/auth/login`

**决定**：Portal 登录页 `/login` 的 Vue 组件模板和 CSS 完全复用 Admin 登录页源码（`apps/ai-studio-admin-web/src/views/login/index.vue`），不做任何 UI 改动。实现方式为：将该组件文件复制到 Portal 的 `pages/login.vue`，仅替换其中调用 `userStore.login()` 的登录逻辑为 Portal 的 `useUser().login()`，替换跳转目标 `router.push('/dashboard')` 为 `router.push(redirect || '/')`。

**理由**：Admin 登录页 UI 精致复杂（粒子动画/渐变/SVG波浪），重建成本高。Admin 用 Vue 2 Options API，Portal 用 Nuxt 3 Composition API，但 Vue 组件模板语法兼容，可直接复制 `.vue` 文件仅替换 `<script setup>` 逻辑部分。

### D6：文章/问题富文本编辑器选型

**决定**：使用 `@vueup/vue-quill`（Vue 3 Quill 编辑器），支持 Markdown 切换。

**理由**：Quill 是成熟开源富文本编辑器，生态好，支持图片上传到 OSS。Vue-Quill 是其 Vue 3 封装，与 Nuxt 3 兼容性好。

**替代方案**：
- TinyMCE → 商业授权复杂
- wangEditor → 生态较弱
- Markdown Editor（textarea） → 用户门槛高

## Risks / Trade-offs

| Risk | Description | Mitigation |
|------|-------------|------------|
| 富文本 XSS | 用户提交 HTML 内容可能导致 XSS | 后端富文本内容转义，OSS 图片 token 鉴权 |
| 社区内容泛滥 | 用户大量发布低质量文章/问答 | 后端添加发布频率限制（同一用户 5分钟 1 篇） |
| 收藏表查询性能 | polymorphic 关联无法建联合索引 | 对 (user_id, target_type) 建索引，target_id 单独建索引 |
| SEO 问题 | 社区文章详情页需 SSR | Nuxt SSR 模式，article/[id] 页面使用 `useFetch` 服务端获取 |
| SSR 认证 | 页面级 auth guard 在 SSR 时 `localStorage` 不可用 | 已修复 useUser.ts 的 `import.meta.client` 守卫 |

## Migration Plan

**Phase 1：基础框架（前端三栏布局 + 路由）**
1. 重构 `layouts/default.vue` 为三栏布局
2. 新建各页面路由框架（占位内容）
3. 实现 `useUser.ts` auth guard
4. 验证：访问需登录页面正确重定向到 /login

**Phase 2：资源中心（复用现有后端 API）**
1. 实现 `/resources` 页面，对接现有 `/portal/resource/skill/list` 等接口
2. 实现资源详情页，调用 `/portal/resource/{type}/{id}`
3. 实现收藏功能（前端 + 后端 FavoriteController）
4. 验证：资源列表/详情/收藏 正常

**Phase 3：技术社区后端（新增表 + API）**
1. 新增数据库表（article/question/answer/comment/tag 等）
2. 实现 ArticleController、QuestionController、AnswerController、CommentController
3. 实现 FavoriteController 扩展支持 article/question
4. 验证：CRUD + 评论 + 点赞 API 正常

**Phase 4：技术社区前端**
1. 实现 `/community` 文章列表 + `/community/article/[id]` 详情页
2. 实现 `/community/question/[id]` 问题详情页
3. 实现 `/community/ask` 和 `/community/write` 发布页
4. 验证：发文章/提问 → 列表 → 详情 → 评论/点赞 完整流程

**Phase 5：个人中心**
1. 实现 `/profile` 页面（Tab 切换：我的资源/收藏/浏览记录/消息）
2. 实现收藏夹分类展示
3. 实现 `/profile/settings`
4. 验证：登录用户查看个人中心正常

**Rollback**：Phase 1-2 不涉及数据变更，可随时回滚。Phase 3+ 涉及 DB schema，需准备 Flyway 回滚脚本。

## Open Questions

~~1. **文章/问题是否需要审核？**~~ ✅ 已确认：发布即上线，管理员可下架（SUPER_ADMIN 可通过 `/api/admin/article/[id]/takedown` 和 `/api/admin/question/[id]/takedown` 下架内容）；article.status: 0草稿/1已发布/2已下架；question.taken_down: 0正常/1已下架。

~~2. **MCP/安装包/视频 后端接口是否存在？**~~ ✅ 已确认：本次实现范围仅包含 Skill 和 Plugin，其余类型（MCP/教程/安装包/视频）不在本次范围内。

~~3. **浏览记录如何实现？**~~ ✅ 已确认：在本次范围内实现，`browse_history` 表已列入 tasks.md。

~~4. **站内消息通知的触发条件？**~~ ✅ 已确认：在本次范围内实现，触发条件已在 `portal-comment/spec.md` 中定义（评论/回复/采纳 → notification 表）。
