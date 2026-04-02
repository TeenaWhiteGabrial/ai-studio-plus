## 1. 项目初始化

- [x] 1.1 创建后端项目骨架：Spring Boot 3.x + MyBatis Plus + Spring Security，配置 pom.xml 依赖（JJWT、七牛云 SDK、Lombok、Validation）
- [x] 1.2 创建前端项目骨架：Vite + Vue 3 + TypeScript + Element Plus + Pinia + Vue Router
- [x] 1.3 配置后端 application.yml（数据库连接、JWT Secret、七牛云 AK/SK/Bucket/Domain）
- [x] 1.4 配置前端 .env 文件（API 基础路径、七牛云 Domain）
- [x] 1.5 配置 Nginx 反向代理示例（`/api` 转发到后端 8080）

## 2. 数据库初始化

- [x] 2.1 编写建表 DDL：sys_user、sys_role、sys_user_role、sys_menu、sys_role_menu
- [x] 2.2 编写建表 DDL：skill、mcp_server、plugin、tutorial、member_output、sys_oper_log
- [x] 2.3 插入初始数据：三个角色（SUPER_ADMIN/ADMIN/USER）、菜单树数据、初始超级管理员账号

## 3. 用户权限模块（M05）

- [x] 3.1 实现 JWT 工具类（生成、解析、校验 Token）
- [x] 3.2 配置 Spring Security（白名单、JWT Filter、UserDetailsService）
- [x] 3.3 实现登录接口 `POST /api/auth/login`，返回 Token 和用户信息
- [x] 3.4 实现获取菜单树接口 `GET /api/menu/tree`，按角色过滤
- [x] 3.5 实现用户 CRUD 接口（列表/创建/更新/删除），仅 SUPER_ADMIN 可访问
- [x] 3.6 实现分配角色接口 `PUT /api/user/{id}/roles`
- [x] 3.7 实现角色列表查询接口 `GET /api/role/list`
- [x] 3.8 前端：实现登录页（用户名/密码表单，调用登录接口，存储 Token）
- [x] 3.9 前端：实现动态路由（根据菜单树生成路由，路由守卫校验 Token）
- [x] 3.10 前端：实现用户管理页面（列表、新增、编辑、删除、分配角色）

## 4. OSS 文件存储集成（M08）

- [x] 4.1 后端封装七牛云 Service（生成 UpToken、删除文件）
- [x] 4.2 实现获取 UpToken 接口 `GET /api/oss/token`，需登录
- [x] 4.3 前端封装 OSS 上传工具函数（使用 UpToken 直传七牛云，返回访问 URL）

## 5. Skill 技能管理（M01）

- [x] 5.1 后端：实现 Skill 实体、Mapper、Service、Controller
- [x] 5.2 后端：实现列表分页查询 `GET /api/skill/list`（支持 keyword/category/status 筛选）
- [x] 5.3 后端：实现创建 Skill `POST /api/skill`（名称唯一校验）
- [x] 5.4 后端：实现编辑 Skill `PUT /api/skill/{id}`（权限校验：管理员任意/USER 仅自己）
- [x] 5.5 后端：实现删除 Skill `DELETE /api/skill/{id}` 和批量删除（权限校验 + 联动删除 OSS）
- [x] 5.6 后端：实现 Skill 详情 `GET /api/skill/{id}`
- [x] 5.7 后端：实现 Skill 下载 `GET /api/skill/{id}/download`（download_count +1）
- [x] 5.8 后端：实现批量 ZIP 导入 `POST /api/skill/import`（解析 ZIP，返回成功/失败数量）
- [x] 5.9 前端：实现 Skill 列表页（分页、搜索、筛选、下载、删除）
- [x] 5.10 前端：实现 Skill 新增/编辑表单（含 OSS 文件上传）

## 6. MCP 服务器管理（M02）

- [x] 6.1 后端：实现 MCP 实体、Mapper、Service、Controller
- [x] 6.2 后端：实现列表分页查询 `GET /api/mcp/list`（支持 keyword/status 筛选）
- [x] 6.3 后端：实现创建 MCP `POST /api/mcp`
- [x] 6.4 后端：实现更新 MCP `PUT /api/mcp/{id}`（权限校验）
- [x] 6.5 后端：实现删除 MCP `DELETE /api/mcp/{id}`（权限校验）
- [x] 6.6 后端：实现连接测试 `POST /api/mcp/{id}/test`（仅管理员，超时 5s）
- [x] 6.7 前端：实现 MCP 列表页（分页、搜索、筛选、连接测试、删除）
- [x] 6.8 前端：实现 MCP 新增/编辑表单

## 7. Plugin 插件管理（M03）

- [x] 7.1 后端：实现 Plugin 实体、Mapper、Service、Controller
- [x] 7.2 后端：实现列表分页查询 `GET /api/plugin/list`（支持 keyword/type/status 筛选）
- [x] 7.3 后端：实现上传 Plugin `POST /api/plugin`（file_size 校验 ≤100MB）
- [x] 7.4 后端：实现更新 Plugin `PUT /api/plugin/{id}`（权限校验）
- [x] 7.5 后端：实现删除 Plugin `DELETE /api/plugin/{id}`（权限校验 + 联动删除 OSS）
- [x] 7.6 后端：实现 Plugin 下载 `GET /api/plugin/{id}/download`（download_count +1）
- [x] 7.7 前端：实现 Plugin 列表页（分页、搜索、筛选、下载、删除）
- [x] 7.8 前端：实现 Plugin 上传/编辑表单（含 OSS 文件上传）

## 8. 教程管理（M04）

- [x] 8.1 后端：实现 Tutorial 实体、Mapper、Service、Controller
- [x] 8.2 后端：实现列表分页查询 `GET /api/tutorial/list`（支持 keyword/category/tag/status 筛选）
- [x] 8.3 后端：实现创建教程 `POST /api/tutorial`
- [x] 8.4 后端：实现更新教程 `PUT /api/tutorial/{id}`（权限校验）
- [x] 8.5 后端：实现删除教程 `DELETE /api/tutorial/{id}`（权限校验 + 联动删除 OSS）
- [x] 8.6 后端：实现教程详情 `GET /api/tutorial/{id}`（view_count +1）
- [x] 8.7 前端：实现教程列表页（分页、搜索、筛选、在线阅读入口）
- [x] 8.8 前端：实现教程新增/编辑表单（含 Markdown 文件上传）
- [x] 8.9 前端：实现教程在线阅读页（渲染 Markdown 内容）

## 9. 成员产出统计（M06）

- [x] 9.1 后端：实现 MemberOutput 实体、Mapper、Service、Controller
- [x] 9.2 后端：实现查询今日产出 `GET /api/output/today`
- [x] 9.3 后端：实现提交今日产出 `POST /api/output`（stat_date 校验为今天，UPSERT 逻辑）
- [x] 9.4 后端：实现查询个人历史 `GET /api/output/history`（按日期范围）
- [x] 9.5 后端：实现管理员全员产出列表 `GET /api/output/admin/list`（按日期，仅管理员）
- [x] 9.6 后端：实现产出汇总统计 `GET /api/output/stats`（按日期范围，USER 仅自己）
- [x] 9.7 前端：实现"我的产出"页面（今日产出表单，回填已有数据，提交）
- [x] 9.8 前端：实现产出历史页面（日期范围查询，列表展示）
- [x] 9.9 前端：实现管理员产出列表页（按日期查看全员产出）

## 10. 数据看板（M07）

- [x] 10.1 后端：实现概览数据接口 `GET /api/dashboard/overview`（各资源总数和下载量）
- [x] 10.2 后端：实现产出趋势接口 `GET /api/dashboard/trend`（支持 day/week/month 粒度）
- [x] 10.3 后端：实现成员排行榜接口 `GET /api/dashboard/ranking`（支持 week/month 周期，topN）
- [x] 10.4 后端：实现详细统计接口 `GET /api/dashboard/detail`（支持 groupBy=department/user，仅管理员）
- [x] 10.5 前端：实现数据看板页面（概览卡片、趋势折线图、排行榜、详细统计表格）
- [x] 10.6 前端：集成图表库（ECharts 或 Chart.js）渲染趋势图和排行榜

## 11. 公共基础设施

- [x] 11.1 后端：实现统一响应体 `Result<T>`（code/message/data）
- [x] 11.2 后端：实现全局异常处理器（@ControllerAdvice，处理 403/404/400/500）
- [x] 11.3 后端：实现操作日志切面（AOP，记录写操作到 sys_oper_log）
- [x] 11.4 后端：实现通用分页请求/响应 DTO
- [x] 11.5 前端：封装 Axios 请求拦截器（自动携带 Token，401 跳登录页）
- [x] 11.6 前端：实现主布局组件（侧边栏菜单、顶部 Header、内容区域）
