## ADDED Requirements

### Requirement: 问题数据模型

问题 SHALL 存储于 `question` 表，包含字段：id、title（varchar 200）、content（longtext 富文本 HTML）、author_id、author_name、tags（varchar 500 逗号分隔标签名）、answers_count（int 默认 0）、views_count（int 默认 0）、has_best_answer（tinyint 0无最佳答案/1已有最佳答案）、best_answer_id（外键 answer.id 可空）、taken_down（tinyint 0正常/1已下架）、created_at、updated_at、is_deleted。

### Requirement: 回答数据模型

回答 SHALL 存储于 `answer` 表，包含字段：id、question_id（外键）、author_id、author_name、content（longtext）、likes_count（int 默认 0）、is_best（tinyint 默认 0）、created_at、updated_at、is_deleted。

### Requirement: 问题列表 API

`GET /api/question/list` 接口 SHALL 支持参数：keyword（LIKE title/content）、tag（LIKE %tag%）、sort（latest/hot/unanswered）、page、size。返回分页结果，不含 content 完整内容。

### Requirement: 问题详情 API

`GET /api/question/[id]` 接口 SHALL 返回问题完整信息（含回答列表分页，answer 每页 10 条），同时将 views_count + 1。已采纳回答的 answer.is_best=1 且排在列表最前。

### Requirement: 问题发布 API

`POST /api/question` 接口 SHALL 仅对登录用户开放，请求体包含 title、content、tags（数组）。创建后初始化 answers_count=0、status=0。

### Requirement: 问题更新 API

`PUT /api/question/[id]` 接口 SHALL 仅对问题作者开放。更新 title/content/tags。

### Requirement: 问题删除 API

`DELETE /api/question/[id]` 接口 SHALL 仅对问题作者或 SUPER_ADMIN 开放。级联删除所有关联的 answer 和 comment。

### Requirement: 回答发布 API

`POST /api/question/[id]/answer` 接口 SHALL 仅对登录用户开放。创建 answer 记录后，question.answers_count + 1。同一问题同一用户只能提交一次回答（避免重复）。

### Requirement: 回答更新 API

`PUT /api/answer/[id]` 接口 SHALL 仅对回答作者开放，更新 content。

### Requirement: 回答删除 API

`DELETE /api/answer/[id]` 接口 SHALL 仅对回答作者或 SUPER_ADMIN 开放。删除后 question.answers_count - 1。

### Requirement: 回答采纳 API

`PUT /api/answer/[id]/accept` 接口 SHALL 仅对问题作者开放。将该 answer 标记为 is_best=1，同时更新 question.best_answer_id 和 question.has_best_answer=1。已采纳问题不可再更改。

### Requirement: 回答点赞 API

`POST /api/answer/[id]/like` 接口 SHALL 仅对登录用户开放。记录到 `answer_like` 表（user_id, answer_id 联合唯一），更新 answer.likes_count。

### Requirement: 问题下架 API

`PUT /api/admin/question/[id]/takedown` 接口 SHALL 仅对 SUPER_ADMIN 开放。将 question.taken_down 更新为 1。已下架问题不再出现在列表中，但数据保留。
