## 1. 前端记住密码功能

- [x] 1.1 在登录页添加"记住密码"复选框
- [x] 1.2 在 userStore 中添加保存和读取记住的密码方法
- [x] 1.3 修改登录逻辑，勾选时保存到 localStorage
- [x] 1.4 页面加载时读取记住的密码并自动填充
- [x] 1.5 logout 时清除记住的密码

## 2. 后端产出提交 API 变更

- [x] 2.1 在 OpenOutputSubmitRequest 中添加 project_root_name 字段
- [x] 2.2 在 MemberOutput 实体中添加 project_root_name 字段（如需要持久化）
- [x] 2.3 创建数据库迁移脚本添加 project_root_name 字段
- [x] 2.4 在 MemberOutputServiceImpl 中保存 project_root_name

## 3. 联调测试

- [x] 3.1 测试记住密码功能（勾选/不勾选/清除）
- [x] 3.2 测试产出提交 API 传入 project_root_name
- [x] 3.3 测试产出提交 API 不传 project_root_name（向后兼容）
