# 登录逻辑重构总结

## 修改时间
2026-04-20

## 修改内容

### 1. 后端修改

#### 1.1 新增DTO类
- `TokenResponse.java` - 获取Token接口响应
- `UserInfoResponse.java` - 获取用户信息接口响应（包含头像、部门、团队、邮箱等信息）

#### 1.2 修改AuthService接口
- 新增 `getToken()` 方法：仅返回Token
- 新增 `getUserInfo(Long userId)` 方法：返回完整的用户信息
- 保留原 `login()` 方法并标记为@Deprecated（向后兼容）

#### 1.3 修改AuthServiceImpl实现
- 实现 `getToken()` 方法：验证用户名密码并返回Token
- 实现 `getUserInfo()` 方法：根据userId返回用户详细信息，包括：
  - userId, username, realName, roles
  - avatar（头像）
  - deptId, deptName（部门信息）
  - teamId, teamName（团队信息）
  - email（邮箱，为空时返回空字符串）

#### 1.4 修改AuthController控制器
- 新增 `POST /auth/token` 接口：获取Token
- 新增 `GET /auth/user-info` 接口：获取当前登录用户的信息
- 保留原 `POST /auth/login` 接口（向后兼容）
- 新增 `POST /auth/avatar` 接口：上传用户头像

#### 1.5 实体和请求DTO修改
- `SysUser` 实体：新增 `avatar` 字段
- `UserCreateRequest`：新增 `avatar` 字段
- `UserUpdateRequest`：新增 `avatar` 字段

#### 1.6 数据库变更
- 创建SQL脚本 `add_avatar_field.sql` 用于添加avatar字段
- 需要在sys_user表中添加avatar字段

### 2. 前端修改

#### 2.1 admin-web
- 修改 `stores/user.ts`：
  - 更新 `UserInfo` 接口，新增 avatar、deptId、deptName、teamId、teamName、email 字段
  - 修改 `login()` 方法：先调用 `/auth/token` 获取Token，再调用 `/auth/user-info` 获取用户信息

#### 2.2 console-web
- 修改 `stores/user.ts`：
  - 更新 `UserInfo` 接口，新增 avatar、deptId、deptName、teamId、teamName、email 字段
  - 修改 `login()` 方法：先调用 `/auth/token` 获取Token，再调用 `/auth/user-info` 获取用户信息

#### 2.3 portal-web
- 修改 `shared/types/auth.ts`：
  - 更新 `UserInfo` 接口，新增 deptId、deptName、teamId、teamName 字段
- 修改 `stores/auth.ts`：
  - 修改 `ownLogin()` 方法：先调用 `/auth/token` 获取Token，再调用 `/auth/user-info` 获取用户信息

## 接口变更说明

### 新增接口

#### POST /auth/token
**功能**：获取登录Token
**请求体**：
```json
{
  "username": "用户名",
  "password": "RSA加密后的密码"
}
```
**响应**：
```json
{
  "code": 200,
  "data": {
    "token": "JWT Token字符串"
  }
}
```

#### GET /auth/user-info
**功能**：获取当前登录用户的详细信息
**请求头**：需要携带有效的Token
**响应**：
```json
{
  "code": 200,
  "data": {
    "userId": 1,
    "username": "admin",
    "realName": "管理员",
    "roles": ["SUPER_ADMIN"],
    "avatar": "头像URL或空字符串",
    "deptId": 1,
    "deptName": "技术部",
    "teamId": 1,
    "teamName": "开发团队",
    "email": "admin@example.com"
  }
}
```

#### POST /auth/avatar
**功能**：上传用户头像
**请求类型**：multipart/form-data
**请求参数**：file（头像文件）
**响应**：
```json
{
  "code": 200,
  "data": "头像URL"
}
```

### 保留接口（向后兼容）

#### POST /auth/login
**说明**：保留原接口以确保向后兼容，建议前端逐步迁移到新接口

## 部署步骤

### 1. 数据库变更
```sql
-- 执行SQL脚本添加avatar字段
source docs/add_avatar_field.sql;
```

### 2. 后端部署
1. 编译后端代码
2. 部署到服务器
3. 重启应用

### 3. 前端部署
1. 编译三个前端项目
   - admin-web
   - console-web
   - portal-web
2. 部署到服务器

## 注意事项

1. **向后兼容**：原 `/auth/login` 接口已标记为@Deprecated，但仍可正常使用
2. **头像上传**：当前上传接口返回的是文件名，实际生产环境需实现对象存储（OSS、MinIO等）
3. **空值处理**：用户信息中的邮箱、部门、团队等字段，如果没有值则返回null或空字符串
4. **安全性**：所有新接口都继承了原有的认证机制，需要有效的Token才能访问
5. **测试建议**：
   - 测试登录流程（获取Token → 获取用户信息）
   - 测试用户信息接口返回的完整性
   - 测试头像上传功能
   - 测试向后兼容性（原login接口仍可用）

## 后续优化建议

1. 实现完整的头像上传和存储功能
2. 添加头像删除接口
3. 考虑添加头像尺寸限制和格式验证
4. 可以考虑添加用户信息缓存机制
5. 原login接口可以在确认所有前端都已迁移后删除
