## ADDED Requirements

### Requirement: 记住密码
系统 SHALL 允许用户选择是否记住登录凭据，勾选后下次打开登录页自动填充用户名和密码。

#### Scenario: 用户勾选记住密码并登录成功
- **WHEN** 用户勾选"记住密码"复选框并成功登录
- **THEN** 系统将用户名和密码存储到 localStorage，下次打开登录页自动填充

#### Scenario: 用户未勾选记住密码
- **WHEN** 用户未勾选"记住密码"复选框并成功登录
- **THEN** 系统不存储凭据，下次打开登录页不自动填充

#### Scenario: 用户清除记住的密码
- **WHEN** 用户 logout 或者手动清除浏览器数据
- **THEN** localStorage 中存储的凭据被清除
