## ADDED Requirements

### Requirement: 文章数据模型

文章 SHALL 存储于 `article` 表，包含字段：id（自增主键）、title（varchar 200）、content（longtext 富文本 HTML）、summary（varchar 500 可选）、cover_image（varchar 500 可选，OSS key）、author_id（外键 sys_user.id）、author_name（冗余存储）、views_count（int 默认 0）、likes_count（int 默认 0）、comments_count（int 默认 0）、status（tinyint 0草稿/1已发布/2已下架）、created_at、updated_at、is_deleted（tinyint 逻辑删除）。

### Requirement: 文章标签关联

文章与标签为多对多关系，通过 `article_tag` 关联表实现：article_id + tag_id 联合主键。标签由用户在发布时创建或选择，支持中英文标签名。

### Requirement: 文章列表 API

`GET /api/article/list` 接口 SHALL 支持参数：keyword（LIKE title/content）、tag（tag_id）、sort（latest/hot）、page、size。返回分页结果，每条包含文章基本信息（不含完整 content）。

### Requirement: 文章详情 API

`GET /api/article/[id]` 接口 SHALL 返回文章完整信息，同时自动将 views_count + 1。该接口无需登录。

### Requirement: 文章发布 API

`POST /api/article` 接口 SHALL 仅对登录用户开放，请求体包含 title、content、summary（可选）、coverImage（可选）、tagIds（数组）。发布成功后创建 article_tag 关联记录。

### Requirement: 文章更新 API

`PUT /api/article/[id]` 接口 SHALL 仅对文章作者开放。更新 title/content/summary/coverImage/tagIds，updated_at 自动更新为当前时间。

### Requirement: 文章删除 API

`DELETE /api/article/[id]` 接口 SHALL 仅对文章作者开放，执行 is_deleted=1 逻辑删除。

### Requirement: 文章点赞 API

`POST /api/article/[id]/like` 接口 SHALL 仅对登录用户开放。同一用户重复点赞 SHALL 切换状态（点赞→取消点赞）。该接口使用 `article_like` 表（user_id, article_id 联合唯一键）记录点赞状态，同时更新 article.likes_count。

### Requirement: 文章评论数更新

每当有评论创建或删除时，系统 SHALL 自动更新 article.comments_count 字段（通过触发器或 Service 层同步）。

### Requirement: 文章下架 API

`PUT /api/admin/article/[id]/takedown` 接口 SHALL 仅对 SUPER_ADMIN 开放。将 article.status 更新为 2（已下架）。下架后文章不再出现在列表中，但数据保留。

### Requirement: 文章 SEO

文章详情页 SHALL 通过 Nuxt SSR 渲染，每个页面包含唯一的 `<title>` 和 `<meta name="description">`，内容取自文章 title 和 summary。
