## ADDED Requirements

### Requirement: 收藏数据模型

收藏 SHALL 存储于 `favorite` 表，包含字段：id、user_id、target_type（varchar 20：'skill'/'plugin'/'tutorial'/'article'/'question'）、target_id、created_at。user_id + target_type + target_id 联合唯一索引，防止重复收藏。

### Requirement: 收藏创建 API

`POST /api/favorite` 接口 SHALL 仅对登录用户开放。请求体包含 target_type、target_id。创建前检查是否已收藏（重复收藏返回 409 Conflict）。创建成功后返回 {id, target_type, target_id}。

### Requirement: 收藏取消 API

`DELETE /api/favorite` 接口 SHALL 仅对登录用户开放。请求参数或请求体包含 target_type、target_id。删除对应收藏记录。

### Requirement: 收藏列表 API

`GET /api/favorite/list` 接口 SHALL 仅对登录用户开放（查看自己的收藏）。支持参数：target_type（可选，不传则返回全部）、page、size。按 created_at 倒序返回收藏列表，每条包含收藏时间 + 目标对象的完整信息（通过 target_type 和 target_id JOIN 查询）。

### Requirement: 收藏状态查询

`GET /api/favorite/check` 接口 SHALL 仅对登录用户开放。参数 target_type、target_id。返回 { favorited: true/false } 用于页面渲染"收藏"或"已收藏"按钮状态。

### Requirement: 收藏计数接口

各资源/内容 SHALL 通过现有字段记录收藏总数（skill.favorite_count/plugin.favorite_count/article.favorite_count/question.favorite_count），在收藏创建/删除时通过 Service 层同步更新。

### Requirement: 收藏唯一性约束

同一用户对同一资源/内容只能收藏一次。后端 SHALL 使用唯一索引 `uk_user_target` (user_id, target_type, target_id) 保证，重复插入抛出 DuplicateKeyException。
