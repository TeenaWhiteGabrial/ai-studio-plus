## ADDED Requirements

### Requirement: 用户登录（JWT 认证）
系统 SHALL 通过用户名密码验证用户身份，成功后返回 JWT Token 和用户基本信息。

#### Scenario: 登录成功
- **WHEN** 用户提交正确的用户名和密码
- **THEN** 系统返回 JWT Token（有效期 24h）、用户 ID、用户名、角色列表

#### Scenario: 密码错误
- **WHEN** 用户提交错误的密码
- **THEN** 系统返回 401，提示"用户名或密码错误"

#### Scenario: 账号已禁用
- **WHEN** 用户账号 `status=0`
- **THEN** 系统返回 403，提示"用户已被禁用"

#### Scenario: Token 过期
- **WHEN** 请求携带已过期的 JWT Token
- **THEN** 系统返回 401，前端跳转登录页

---

### Requirement: 用户列表查询（仅管理员）
系统 SHALL 允许 SUPER_ADMIN 分页查询用户列表，支持按用户名和部门筛选。

#### Scenario: 超级管理员查询用户列表
- **WHEN** SUPER_ADMIN 请求用户列表
- **THEN** 系统返回分页用户数据及总条数

#### Scenario: 普通用户访问用户列表
- **WHEN** USER 角色访问用户列表接口
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 创建用户
系统 SHALL 允许 SUPER_ADMIN 创建新用户，密码 BCrypt 加密存储，用户名全局唯一。

#### Scenario: 成功创建用户
- **WHEN** SUPER_ADMIN 提交合法的用户表单（用户名、密码、真实姓名、部门）
- **THEN** 系统以 BCrypt 加密密码并创建用户记录，返回新用户 ID

#### Scenario: 用户名重复
- **WHEN** 提交的用户名已存在
- **THEN** 系统返回错误提示"用户名已存在"

#### Scenario: 密码复杂度校验失败
- **WHEN** 管理员创建用户并输入密码"123456"（不满足复杂度要求）
- **THEN** 系统显示错误提示"密码至少8位，需包含大小写字母、数字和特殊字符"
- **AND** 阻止表单提交

#### Scenario: 密码符合复杂度要求
- **WHEN** 管理员创建用户并输入密码"Admin@2026"
- **THEN** 系统通过验证，允许创建用户

---

### Requirement: 更新用户信息
系统 SHALL 允许 SUPER_ADMIN 修改用户基本信息和账号状态（启用/禁用）。

#### Scenario: 成功更新用户信息
- **WHEN** SUPER_ADMIN 提交更新请求（含 real_name、department、email、status）
- **THEN** 系统更新用户记录并返回成功

#### Scenario: 禁用用户
- **WHEN** SUPER_ADMIN 将用户 `status` 设为 0
- **THEN** 系统更新状态，该用户后续登录返回 403

---

### Requirement: 删除用户
系统 SHALL 允许 SUPER_ADMIN 删除用户，同时级联删除 `sys_user_role` 关联记录。

#### Scenario: 成功删除用户
- **WHEN** SUPER_ADMIN 发起删除用户请求
- **THEN** 系统删除用户记录及其所有角色关联记录，返回成功

---

### Requirement: 分配用户角色
系统 SHALL 允许 SUPER_ADMIN 为用户分配角色，替换原有角色关联。

#### Scenario: 成功分配角色
- **WHEN** SUPER_ADMIN 提交用户 ID 和角色 ID 列表
- **THEN** 系统清除该用户原有角色关联，重新插入新的关联，返回成功

---

### Requirement: 获取菜单树（按权限）
系统 SHALL 根据当前登录用户角色返回有权限的菜单树结构，用于前端动态路由。

#### Scenario: SUPER_ADMIN 获取菜单树
- **WHEN** SUPER_ADMIN 请求菜单树
- **THEN** 系统返回全部菜单的树形结构

#### Scenario: USER 获取菜单树
- **WHEN** USER 请求菜单树
- **THEN** 系统返回资源浏览、资源新增、产出录入相关菜单（不含用户管理、其他成员资源管理入口）

---

### Requirement: 角色列表查询
系统 SHALL 允许管理员查询全部角色，用于分配权限时选择。

#### Scenario: 管理员查询角色列表
- **WHEN** ADMIN 或 SUPER_ADMIN 请求角色列表
- **THEN** 系统返回所有角色（id、role_code、role_name）

#### Scenario: 普通用户查询角色列表
- **WHEN** USER 请求角色列表
- **THEN** 系统返回 403 Forbidden

---

### Requirement: 管理员启用/禁用用户
系统 SHALL 允许管理员通过开关控件启用或禁用用户账号。禁用用户无法登录系统。

#### Scenario: 管理员禁用用户
- **WHEN** 管理员点击用户行的禁用开关
- **THEN** 该用户状态变为禁用
- **AND** 该用户无法登录系统

#### Scenario: 管理员启用用户
- **WHEN** 管理员点击用户行的启用开关
- **THEN** 该用户状态变为正常
- **AND** 该用户可以正常登录

#### Scenario: 禁用用户尝试登录
- **GIVEN** 用户状态为禁用
- **WHEN** 该用户尝试登录
- **THEN** 系统返回错误提示"账号已被禁用，请联系管理员"

#### Scenario: 普通用户无权限切换状态
- **GIVEN** 当前登录用户为 USER 角色
- **WHEN** 访问用户管理页面
- **THEN** 不显示状态切换开关

---

### Requirement: 超级管理员配置角色菜单
系统 SHALL 允许超级管理员为 ADMIN 和 USER 角色配置菜单权限。SUPER_ADMIN 菜单不可编辑。

#### Scenario: 超级管理员查看角色菜单配置
- **GIVEN** 当前用户为 SUPER_ADMIN
- **WHEN** 访问角色管理页面并点击"配置菜单"
- **THEN** 显示该角色当前拥有的菜单（树形结构）

#### Scenario: 超级管理员修改角色菜单
- **GIVEN** 当前用户为 SUPER_ADMIN
- **WHEN** 勾选/取消某些菜单并保存
- **THEN** 系统更新该角色的菜单权限
- **AND** 该角色用户重新登录后生效

#### Scenario: 不允许编辑 SUPER_ADMIN 菜单
- **GIVEN** 当前用户为 SUPER_ADMIN
- **WHEN** 查看 SUPER_ADMIN 角色的配置菜单按钮
- **THEN** 按钮禁用或隐藏
- **AND** 提示"超级管理员菜单不可编辑"

#### Scenario: 普通管理员无权限配置菜单
- **GIVEN** 当前登录用户为 ADMIN
- **WHEN** 访问角色管理页面
- **THEN** 不显示"配置菜单"按钮

#### Scenario: 配置后用户权限生效
- **GIVEN** 管理员移除了 USER 角色的"全员产出"菜单
- **WHEN** USER 角色用户重新登录
- **THEN** 该用户菜单栏不显示"全员产出"
