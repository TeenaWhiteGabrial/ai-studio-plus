## ADDED Requirements

### Requirement: 技术社区页面

技术社区路由为 `/community`，SHALL 显示文章列表和问答列表，通过顶部 Tab 切换。默认显示"文章" Tab。

### Requirement: 社区 Tab 切换

顶部 SHALL 显示内容类型 Tab：文章 | 问答。切换 Tab 时更新 URL 为 `/community`（文章，默认）和 `/community?type=question`（问答）。

### Requirement: 文章列表

文章列表 SHALL 支持以下查询：keyword（标题/内容关键词）、tag（标签筛选）、sort（排序：最新/最热/最多评论）、page、size。每篇文章卡片 SHALL 显示：封面图（可选）、标题（截断 1 行）、摘要（截断 2 行）、作者头像+名称、发布时间、阅读数、评论数、点赞数。列表以单列或双列展示，间距清晰。

### Requirement: 文章详情页

文章详情路由为 `/community/article/[id]`，SHALL 支持 SSR 渲染（SEO 友好）。页面 SHALL 显示：文章标题、作者信息（头像/名称/粉丝数/关注按钮）、发布时间/阅读数/点赞数、标签、完整文章内容（富文本/HTML 渲染）、点赞按钮、收藏按钮、相关文章推荐（同标签 3-5 篇）、评论区。

### Requirement: 文章点赞

已登录用户点击点赞按钮 SHALL 调用 `/api/article/[id]/like` POST 接口。点赞成功后点赞数 +1，按钮变为已点赞状态（高亮）。再次点击取消点赞。游客点击重定向到登录页。

### Requirement: 文章收藏

已登录用户点击收藏按钮 SHALL 调用 `/api/favorite` POST 接口（target_type='article'），收藏成功后显示提示。游客点击重定向到登录页。

### Requirement: 文章发布页

文章发布路由为 `/community/write`，SHALL 仅对登录用户开放，未登录访问时重定向到登录页。页面 SHALL 包含：标题输入框（必填，最多 100 字）、标签选择器（支持多选，最多 5 个标签，标签可新建）、封面图上传（可选）、富文本内容编辑器（支持图片上传）、发布按钮（发布/存草稿）。

### Requirement: 文章编辑

已登录用户访问 `/community/write/[id]` SHALL 进入文章编辑页，内容预填充为该文章的草稿或已发布内容。编辑后保存为新版本或覆盖原内容（根据 article_version 策略）。

### Requirement: 文章删除

文章作者点击文章详情页的"删除"按钮 SHALL 调用 `/api/article/[id]` DELETE 接口。删除前 SHALL 弹出确认对话框"确定删除这篇文章吗？"。删除成功后跳转到 `/community`。

### Requirement: 问答列表

问答列表 SHALL 支持 keyword（搜索）、tag（标签筛选）、sort（最新/最热/待回答）、page、size。问题卡片 SHALL 显示：问题标题（截断 1 行）、问题摘要（截断 2 行）、标签（最多 3 个）、回答数、浏览数、发布时间。

### Requirement: 问题详情页

问题详情路由为 `/community/question/[id]`，SHALL 显示：问题标题、问题详情（富文本）、标签、作者信息、发布时间、回答数。页面下方 SHALL 展示回答列表，每个回答 SHALL 支持：内容、作者信息、点赞数、评论数、最佳答案标记（采纳后显示）。

### Requirement: 回答功能

已登录用户可在问题详情页底部填写回答（富文本编辑器），点击"提交回答"调用 `/api/question/[id]/answer` POST 接口。回答提交后显示在回答列表中，无需刷新页面（AJAX）。

### Requirement: 回答采纳

问题作者 SHALL 看到每个回答旁的"采纳"按钮。点击后调用 `/api/answer/[id]/accept` PUT 接口，回答标记为最佳答案，显示✅标签。已采纳问题不可更改。

### Requirement: 问题删除

问题作者点击问题详情页的"删除"按钮 SHALL 调用 `/api/question/[id]` DELETE 接口。删除成功后跳转到 `/community?type=question`。

### Requirement: 社区内容发布权限

文章发布 SHALL 仅对登录用户开放，未登录访问 `/community/write` 重定向到 /login。问答发布 SHALL 仅对登录用户开放，未登录访问 `/community/ask` 重定向到 /login。所有访客均可浏览文章/问答列表和详情。

### Requirement: 社区内容排序规则

"最新"：按 created_at 倒序；"最热"：按 views_count 倒序；"最多评论"（仅文章）：按评论数倒序；"待回答"（仅问答）：answers_count=0 且按 created_at 倒序。
