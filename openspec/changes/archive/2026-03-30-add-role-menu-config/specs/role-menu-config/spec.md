## ADDED Requirements

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
- **GIVEN** 当前用户为 ADMIN
- **WHEN** 访问角色管理页面
- **THEN** 不显示"配置菜单"按钮

#### Scenario: 配置后用户权限生效
- **GIVEN** 管理员移除了 USER 角色的"全员产出"菜单
- **WHEN** USER 角色用户重新登录
- **THEN** 该用户菜单栏不显示"全员产出"
