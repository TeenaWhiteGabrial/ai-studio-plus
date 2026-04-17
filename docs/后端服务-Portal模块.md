# AI-Studio 后端服务 - Portal 模块技术文档

## 1. 项目概述

### 1.1 项目架构

```
ai-studio/
├── apps/                              # 前端应用
│   ├── ai-studio-portal-web/         # 门户前端 (Nuxt 3)
│   ├── ai-studio-console-web/        # 控制台前端
│   └── ai-studio-admin-web/          # 管理端前端
├── ai-studio-service/                # 后端服务 (Spring Boot)
└── openspec/changes/                 # 设计变更文档
```

### 1.2 后端服务位置

```
ai-studio-service/src/main/java/com/aistudio/service/
├── controller/
│   └── portal/                       # Portal 控制器层
├── service/                         # 服务层接口
├── service/impl/                    # 服务层实现
├── mapper/                          # 数据访问层
├── entity/                         # 实体类
└── dto/                             # 数据传输对象
```

---

## 2. 数据库结构

### 2.1 数据库迁移文件

| 文件 | 说明 |
|------|------|
| `V4__init_portal_tables.sql` | 初始 Portal 表：banner、announcement、favorite、like_record、comment |
| `V11__portal_community_tables.sql` | 社区表：article、question、answer、community_comment、tag、browse_history、notification、user_follow |
| `V12__add_portal_columns.sql` | 扩展字段：sys_user.avatar/bio、skill.plugin.favorite_count/followers_count |

### 2.2 核心实体

#### 社区相关实体

| 实体类 | 说明 |
|--------|------|
| `Article.java` | 文章 |
| `Question.java` | 问题 |
| `Answer.java` | 回答 |
| `AnswerLike.java` | 回答点赞 |
| `ArticleLike.java` | 文章点赞 |
| `CommunityComment.java` | 社区评论 |
| `CommunityCommentLike.java` | 评论点赞 |
| `Favorite.java` | 收藏 |
| `BrowseHistory.java` | 浏览历史 |
| `Notification.java` | 通知 |
| `Tag.java` | 标签 |
| `UserFollow.java` | 用户关注 |

---

## 3. 控制器层 (Controller)

位置：`controller/portal/`

### 3.1 控制器列表

| 控制器 | 路由前缀 | 功能说明 |
|--------|---------|---------|
| `ArticleController.java` | `/api/article` | 文章管理（CRUD、点赞） |
| `QuestionController.java` | `/api/question` | 问答管理（CRUD） |
| `AnswerController.java` | `/api/answer` | 回答管理（CRUD、采纳、点赞） |
| `CommentController.java` | `/api/comment` | 评论管理（列表、创建、删除、点赞） |
| `FavoriteController.java` | `/api/favorite` | 收藏管理（添加、删除、列表、检查） |
| `BrowseHistoryController.java` | `/api/browse-history` | 浏览历史（添加、列表、清空） |
| `NotificationController.java` | `/api/notification` | 通知管理（列表、已读、未读数） |
| `TagController.java` | `/api/tag` | 标签管理（列表、创建） |
| `PortalPublicInfoController.java` | `/portal/public` | 公开信息（Banner、公告、站点信息） |
| `PortalResourceBrowseController.java` | `/portal/resource` | 资源浏览公开接口 |

---

## 4. 服务层 (Service)

位置：`service/` 和 `service/impl/`

### 4.1 服务接口列表

| 接口 | 实现类 | 功能说明 |
|------|--------|---------|
| `ArticleService` | `ArticleServiceImpl.java` | 文章服务 |
| `QuestionService` | `QuestionServiceImpl.java` | 问答服务 |
| `AnswerService` | `AnswerServiceImpl.java` | 回答服务 |
| `CommentService` | `CommentServiceImpl.java` | 评论服务 |
| `FavoriteService` | `FavoriteServiceImpl.java` | 收藏服务 |
| `BrowseHistoryService` | `BrowseHistoryServiceImpl.java` | 浏览历史服务 |
| `NotificationService` | `NotificationServiceImpl.java` | 通知服务 |

---

## 5. API 接口规格

### 5.1 公开接口（无需认证）

#### Banner 与公告

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/portal/public/banner` | 获取 Banner 列表 |
| GET | `/portal/public/announcement` | 获取公告列表 |
| GET | `/portal/public/site-info` | 获取站点信息 |

#### 资源浏览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/portal/resource/skill/list` | Skill 列表 |
| GET | `/portal/resource/skill/{id}` | Skill 详情 |
| GET | `/portal/resource/plugin/list` | Plugin 列表 |
| GET | `/portal/resource/plugin/{id}` | Plugin 详情 |
| GET | `/portal/resource/tutorial/list` | 教程列表 |
| GET | `/portal/resource/tutorial/{id}` | 教程详情 |

#### 文章（公开）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/article/list` | 文章列表 |
| GET | `/api/article/{id}` | 文章详情 |
| GET | `/api/tag/list` | 标签列表 |

#### 问答（公开）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/question/list` | 问题列表 |
| GET | `/api/question/{id}` | 问题详情 |

---

### 5.2 认证接口（需登录）

#### 文章操作

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/article` | 创建文章 |
| PUT | `/api/article/{id}` | 更新文章 |
| DELETE | `/api/article/{id}` | 删除文章 |
| POST | `/api/article/{id}/like` | 点赞文章 |

#### 问答操作

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/question` | 创建问题 |
| PUT | `/api/question/{id}` | 更新问题 |
| DELETE | `/api/question/{id}` | 删除问题 |
| POST | `/api/question/{questionId}/answer` | 创建回答 |
| PUT | `/api/answer/{id}` | 更新回答 |
| DELETE | `/api/answer/{id}` | 删除回答 |
| POST | `/api/answer/{id}/accept` | 采纳回答 |
| POST | `/api/answer/{id}/like` | 点赞回答 |

#### 评论操作

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/comment` | 创建评论 |
| DELETE | `/api/comment/{id}` | 删除评论 |
| POST | `/api/comment/{id}/like` | 点赞评论 |

#### 收藏操作

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/favorite` | 添加收藏 |
| DELETE | `/api/favorite` | 移除收藏 |
| GET | `/api/favorite/list` | 收藏列表 |
| GET | `/api/favorite/check` | 检查是否已收藏 |

#### 浏览历史

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/browse-history` | 添加浏览记录 |
| GET | `/api/browse-history/list` | 浏览历史列表 |
| DELETE | `/api/browse-history/clear` | 清空浏览历史 |

#### 通知

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/notification/list` | 通知列表 |
| PUT | `/api/notification/{id}/read` | 标记已读 |
| GET | `/api/notification/unread-count` | 未读数量 |

#### 标签

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/tag` | 创建标签 |

---

## 6. 安全配置 (SecurityConfig)

### 6.1 公开接口（无需认证）

```
/portal/resource/**                  # 资源浏览
/api/article/list                   # 文章列表
/api/article/{id}                   # 文章详情
/api/article/{id}/like              # 文章点赞
/api/question/list                  # 问题列表
/api/question/{id}                  # 问题详情
/api/tag/list                       # 标签列表
```

### 6.2 需认证接口

```
/api/article                         # 文章操作
/api/article/{id}
/api/question                        # 问题操作
/api/question/{id}
/api/answer/**                       # 回答操作
/api/comment/**                     # 评论操作
/api/favorite/**                    # 收藏操作
/api/browse-history/**              # 浏览历史
/api/notification/**                # 通知
```

---

## 7. 前端 Portal-Web 结构

### 7.1 页面结构

```
apps/ai-studio-portal-web/app/pages/
├── index.vue                    # 首页
├── login/                       # 登录页
├── community/                   # 社区模块
│   ├── index.vue               # 社区首页
│   ├── ask.vue                 # 提问页
│   ├── write.vue               # 写文章页
│   ├── article/[id].vue        # 文章详情页
│   └── question/[id].vue       # 问题详情页
├── resources/                   # 资源中心
│   ├── index.vue               # 资源列表页
│   └── [type]/[id].vue         # 资源详情页
└── profile/                    # 个人中心
    ├── index.vue               # 个人主页
    └── settings.vue            # 设置页
```

### 7.2 API 调用封装 (Composables)

| 文件 | 功能 |
|------|------|
| `useArticle.ts` | 文章相关 API |
| `useQuestion.ts` | 问答相关 API |
| `useCommunity.ts` | 社区通用 API（评论、收藏、通知、标签、浏览历史） |
| `useResource.ts` | 资源相关 API |
| `useSite.ts` | 网站配置 |

### 7.3 共享类型 (shared/types)

| 文件 | 类型定义 |
|------|---------|
| `article.ts` | Article, ArticleListQuery, ArticleFormData |
| `question.ts` | Question, Answer, QuestionListQuery, QuestionFormData |
| `community.ts` | Comment, Favorite, Notification, BrowseHistory, Tag |
| `resource.ts` | Resource, ResourceType |

---

## 8. 功能模块总结

| 功能模块 | 说明 | 状态 |
|---------|------|------|
| 首页 | 资源精选 + 社区最新文章混排 | 已实现 |
| 资源中心 | Skill/Plugin 列表、详情、收藏 | 已实现 |
| 技术博客 | 文章发布/编辑/阅读/评论/点赞 | 已实现 |
| 问答系统 | 提问/回答/采纳/评论/点赞 | 已实现 |
| 标签系统 | 文章/问题标签筛选 | 已实现 |
| 收藏功能 | 资源+社区内容统一收藏 | 已实现 |
| 个人中心 | 我的资源/收藏/浏览记录/通知 | 已实现 |
| 认证系统 | 与 Admin/Console 共用用户体系 | 已实现 |
| Banner/公告 | 公开信息展示 | 后端待实现 |

---

## 9. 关键文件路径

### 后端核心

| 类别 | 路径 |
|------|------|
| 控制器 | `ai-studio-service/src/main/java/com/aistudio/service/controller/portal/` |
| 服务接口 | `ai-studio-service/src/main/java/com/aistudio/service/service/` |
| 服务实现 | `ai-studio-service/src/main/java/com/aistudio/service/service/impl/` |
| 实体类 | `ai-studio-service/src/main/java/com/aistudio/service/entity/` |
| 数据库迁移 | `ai-studio-service/src/main/resources/db/migration/` |

### 前端核心

| 类别 | 路径 |
|------|------|
| 页面组件 | `apps/ai-studio-portal-web/app/pages/` |
| API 调用 | `apps/ai-studio-portal-web/app/composables/` |
| 类型定义 | `apps/ai-studio-portal-web/shared/types/` |

### 设计文档

| 文档 | 路径 |
|------|------|
| Portal PRD | `docs/PRD-Portal.md` |
| 变更设计 | `openspec/changes/portal-redesign/` |
