## ADDED Requirements

### Requirement: 评论数据模型

评论 SHALL 存储于 `comment` 表，包含字段：id、comment_type（varchar 20：'article'/'question'/'answer'）、target_id（被评论对象 ID）、parent_id（父评论 ID，可空，用于二级嵌套回复）、root_id（根评论 ID，可空）、author_id、author_name、author_avatar、content（varchar 2000）、likes_count（int 默认 0）、created_at、is_deleted。

### Requirement: 评论列表 API

`GET /api/comment/list` 接口 SHALL 支持参数：comment_type、target_id、page、size。返回该对象下的所有评论，按 created_at 升序排列。二级回复 SHALL 显示在父评论下方并缩进（通过 parent_id 关系组织）。

### Requirement: 评论创建 API

`POST /api/comment` 接口 SHALL 仅对登录用户开放。请求体包含 comment_type、target_id、parent_id（可选）、content。创建后 SHALL 更新对应 target 的评论计数：article.comments_count + 1 或 question.answers_count + 1（若是回答下的评论则更新 answer.likes_count？实际上评论不影响点赞数）。

parent_id 为空时为一级评论；parent_id 不为空时为二级回复，root_id = 父评论的 root_id（若父评论 root_id 为空则 root_id = 父评论 id）。

### Requirement: 评论删除 API

`DELETE /api/comment/[id]` 接口 SHALL 仅对评论作者或 SUPER_ADMIN 开放。逻辑删除后，若评论为一级评论 SHALL 同时删除其所有二级子评论。

### Requirement: 评论点赞 API

`POST /api/comment/[id]/like` 接口 SHALL 仅对登录用户开放。记录到 `comment_like` 表（user_id, comment_id 联合唯一），更新 comment.likes_count。

### Requirement: 评论内容限制

评论 content 最大长度为 2000 字符，HTML 特殊字符 SHALL 被转义存储，防止 XSS。前端评论输入框 SHALL 支持 Markdown 简化语法（粗体/斜体/代码块/链接）。

### Requirement: 评论通知触发

当评论被创建时：
- 若评论是对文章/问题的评论：通知文章/问题作者有一条新评论
- 若评论是对回答的评论：通知回答作者有一条新回复
- 若评论是二级回复：通知被回复者的上一级评论作者
通知内容存储于 `notification` 表，trigger_count + 1。
