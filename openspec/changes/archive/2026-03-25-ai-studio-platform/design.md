## Context

AI Studio 平台从零构建，包含前端（Vue 3）和后端（Spring Boot 3）两个独立项目。平台面向团队内部使用，需管理 Skills / MCP / Plugin / 教程四类资源，提供成员产出录入与数据看板，并集成七牛云对象存储实现前端直传。

核心约束：
- 三级 RBAC 角色（SUPER_ADMIN / ADMIN / USER），USER 只能管理自己创建的资源
- 所有文件资源存储于七牛云 OSS，后端不做文件中转
- 成员产出仅允许录入当天数据，联合唯一键防重

## Goals / Non-Goals

**Goals:**
- 搭建前后端完整项目骨架（目录结构、依赖、配置）
- 实现全部 8 个模块的 REST API（后端）及对应页面（前端）
- JWT + Spring Security 实现无状态认证与 RBAC 权限控制
- 七牛云 UpToken 前端直传方案
- MySQL 建表脚本（含索引、唯一键）
- Nginx 反向代理配置示例

**Non-Goals:**
- 不做 Git API 自动统计代码产出（PRD 明确为后期扩展）
- 不做按钮级权限（预留扩展，当前仅菜单级）
- 不做多租户、不做 SaaS 化
- 不做移动端适配
- 不做自动化部署 / CI-CD 流水线

## Decisions

### D1：前后端分离，独立部署

**选择**：前端 Vue 3 打包静态文件由 Nginx 托管，后端 Spring Boot JAR 独立运行，Nginx 反向代理 `/api` 到后端 8080 端口。

**理由**：前后端独立迭代，职责清晰；Nginx 直接服务静态文件性能更好；符合 PRD 部署要求。

**替代方案**：Spring Boot 直接托管前端静态资源（打包进 JAR）—— 放弃，耦合度高，构建流程复杂。

---

### D2：JWT 无状态认证，不使用 Session

**选择**：Spring Security + JJWT，Token 有效期 24h，前端 localStorage 存储，每次请求 `Authorization: Bearer <token>` 头携带。

**理由**：无状态易于水平扩展；与 PRD 要求一致。

**替代方案**：Session + Redis —— 放弃，增加 Redis 依赖，对当前规模过重。

---

### D3：RBAC 基于菜单，后端 @PreAuthorize 注解控制

**选择**：
- 数据库存储 sys_menu、sys_role_menu 关联
- 后端接口用 `@PreAuthorize("hasRole('ADMIN')")` 或自定义注解控制
- 前端根据 `/api/menu/tree` 返回结果动态生成路由和菜单

**理由**：菜单权限可在管理界面配置，无需改代码；动态路由防止前端直接访问无权页面。

**替代方案**：硬编码权限列表 —— 放弃，不灵活。

---

### D4：七牛云前端直传（UpToken 方案）

**选择**：
1. 前端请求后端 `/api/oss/token` 获取 UpToken（后端用七牛 SDK 生成）
2. 前端使用 UpToken 直接 PUT 文件到七牛云
3. 七牛返回 key，前端拼接 `domain + key` 得到访问 URL
4. 前端将 `oss_key` 和 `url` 一起提交到后端业务接口保存

**理由**：后端不做文件中转，节省带宽；符合 PRD 明确要求；七牛官方推荐方案。

**替代方案**：后端中转上传 —— 放弃，带宽压力大。

---

### D5：MyBatis Plus 作为 ORM，不用 JPA

**选择**：MyBatis Plus 3.x，BaseMapper 提供基础 CRUD，复杂查询写 XML Mapper。

**理由**：SQL 可控，便于调优；团队对 MyBatis 更熟悉；PRD 指定。

**替代方案**：Spring Data JPA —— 放弃，PRD 已明确技术栈。

---

### D6：后端项目分层结构

```
ai-studio-backend/
├── controller/       # REST 接口层，@PreAuthorize 注解
├── service/          # 业务逻辑层
├── mapper/           # MyBatis Plus Mapper
├── entity/           # 数据库实体（对应表）
├── dto/              # 请求/响应 DTO
├── common/           # 统一响应、异常、工具类
├── config/           # Spring Security、JWT、Qiniu、Swagger 配置
└── resources/
    └── mapper/       # XML Mapper 文件
```

---

### D7：前端项目分层结构

```
ai-studio-frontend/
├── src/
│   ├── api/          # Axios 封装 + 各模块接口
│   ├── views/        # 页面组件（按模块分目录）
│   ├── components/   # 通用组件
│   ├── stores/       # Pinia 状态管理（user、menu）
│   ├── router/       # Vue Router（动态路由）
│   ├── utils/        # 工具函数（request、oss 上传等）
│   └── layout/       # 布局组件（侧边栏、Header）
```

---

### D8：统一响应体格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

错误时：`code` 为业务错误码（401/403/404/500），`message` 为可读描述，`data` 为 null。

---

### D9：成员产出"仅录入当天"在后端强制校验

**选择**：Service 层校验 `stat_date == LocalDate.now()`，不通过返回业务错误。

**理由**：前端校验可被绕过，必须后端兜底；联合唯一键 `(user_id, stat_date)` 同时防重。

## Risks / Trade-offs

| 风险 | 缓解措施 |
|------|---------|
| 七牛云 UpToken 泄露 → 非授权上传 | Token 有效期设短（1h），设置 Bucket 回调策略限制上传内容类型 |
| JWT Token 无法主动吊销 | Token 有效期 24h 可接受；后期可加 Redis 黑名单 |
| 菜单权限粒度仅到菜单级，按钮级暂缺 | PRD 明确后期扩展，sys_menu 已预留 `permission` 字段 |
| 批量 ZIP 导入 Skill 时内存占用 | 限制 ZIP 大小（100MB），流式解析，单个文件处理后立即释放 |
| download_count / view_count 并发写竞争 | 使用 SQL `UPDATE ... SET count = count + 1`，避免先查后写 |

## Migration Plan

1. **初始化数据库**：执行建表 DDL 脚本，插入初始角色数据（SUPER_ADMIN/ADMIN/USER）和菜单数据
2. **后端启动**：配置 `application.yml`（DB、JWT Secret、七牛云 AK/SK），`java -jar ai-studio-backend.jar`
3. **前端构建部署**：`npm run build` → 产物放 Nginx `html/` 目录，配置反向代理
4. **创建超级管理员账号**：通过初始化 SQL 或启动脚本插入第一个 SUPER_ADMIN 用户
5. **回滚**：停服务即可，数据库可通过备份恢复

## Open Questions

- 七牛云 Bucket 域名是否已申请？需配置到 `dev.config` 中（参见 PRD §6）
- 操作日志（`sys_oper_log`）的记录范围：仅写操作还是包含查询？建议仅写操作（create/update/delete/download）
- 前端 Token 过期后是否需要无感刷新（refresh token）？当前方案为过期跳登录页，如需无感刷新需增加 refresh token 接口
