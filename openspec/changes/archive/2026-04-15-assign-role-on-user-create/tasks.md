## 1. 后端实现

- [x] 1.1 在 `UserCreateRequest.java` 中增加 `List<Long> roleIds` 字段（可选）
- [x] 1.2 在 `UserServiceImpl.createUser()` 方法中，插入用户后根据 `roleIds` 插入 `sys_user_role` 记录
- [x] 1.3 若 `roleIds` 为 null 或空，自动设置为 `[4]`（普通用户 role_id=4）
- [x] 1.4 更新 `AdminUserController.java` 的接口注释，说明 `roleIds` 参数

## 2. 前端实现

- [x] 2.1 在 `form` 对象中增加 `roleIds` 字段，默认值为 `[4]`
- [x] 2.2 在新建用户弹窗的表单中增加角色选择器（el-select），默认选中"普通用户"
- [x] 2.3 在编辑用户时，从 `row.role_ids` 回填角色选择器
- [x] 2.4 过滤掉"超级管理员"角色选项（不能通过界面创建超级管理员用户）
