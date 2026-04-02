## ADDED Requirements

### Requirement: 获取七牛云上传凭证（UpToken）
系统 SHALL 允许已登录用户获取七牛云上传凭证（UpToken），用于前端直传文件。

#### Scenario: 成功获取 UpToken
- **WHEN** 已登录用户请求 `/api/oss/token`
- **THEN** 系统使用七牛 SDK 生成 UpToken，返回 `{token, domain, keyPrefix}`，Token 有效期 1 小时

#### Scenario: 未登录获取 UpToken
- **WHEN** 未携带有效 JWT Token 的请求访问 UpToken 接口
- **THEN** 系统返回 401 Unauthorized

---

### Requirement: 前端直传七牛云
系统 SHALL 支持前端使用 UpToken 直接上传文件到七牛云，后端不做文件中转。

#### Scenario: 前端直传成功
- **WHEN** 前端使用有效 UpToken 将文件 PUT 到七牛云
- **THEN** 七牛云返回文件 key，前端拼接 `domain + key` 得到访问 URL，再提交到后端业务接口

#### Scenario: UpToken 过期
- **WHEN** 前端使用已过期的 UpToken 上传
- **THEN** 七牛云返回 401，前端重新请求 UpToken 后重试

---

### Requirement: OSS 文件删除
系统 SHALL 在删除资源记录时联动删除七牛云 OSS 中对应的文件，节省存储空间。

#### Scenario: 删除资源时联动删除 OSS 文件
- **WHEN** 管理员删除 Skill / Plugin / Tutorial 记录
- **THEN** 系统调用七牛 SDK 删除对应 `oss_key` 的文件，再删除数据库记录

#### Scenario: OSS 文件已不存在
- **WHEN** 删除时 OSS 中对应文件已不存在
- **THEN** 系统忽略 OSS 删除错误，继续删除数据库记录，返回成功

#### Scenario: 非管理员触发删除
- **WHEN** USER 删除自己创建的资源
- **THEN** 系统同样联动删除 OSS 文件（权限校验通过后执行）
