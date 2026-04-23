# AI-Studio 后端业务功能清单

## 概述

本文档以业务功能为核心，梳理 AI-Studio 后端服务中各个功能模块的接口分布情况。每个功能可能涉及多个端（Admin/Console/Portal/Open），本文档清晰地展示每个功能在各个端的接口、职责和业务规则。

---

## 一、认证授权模块

### 1.1 用户登录

| 属性 | 说明 |
|------|------|
| 功能描述 | 用户通过用户名密码登录系统，支持 RSA 加密传输 |
| 涉及端 | 通用(/auth) |

**接口：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| 通用 | GET | `/auth/public-key` | 获取 RSA 公钥 |
| 通用 | POST | `auth/token` | 登录获取token |
| 通用 | GET | `/auth/user-info` | 获取用户信息 |

**业务规则：**
- 密码传输使用 RSA 加密
- 登录成功后返回 JWT Token
- 根据Token获取用户信息
- 所有端（Admin/Console/Portal）使用同一套登录接口

---

### 1.2 密码哈希生成（调试用）

| 属性 | 说明 |
|------|------|
| 功能描述 | 生成 BCrypt 密码哈希（仅调试用） |
| 涉及端 | 通用(/auth) |

**接口：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| 通用 | GET | `/auth/gen-hash?pwd=xxx` | 生成密码哈希 |

---

## 二、用户与权限管理模块

### 2.1 用户管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 系统用户的 CRUD 操作、角色分配、状态管理、批量导入 |
| 涉及端 | Admin(/admin/user) |

**接口权限：**
- **SUPER_ADMIN**：可执行所有操作
- **DEPT_ADMIN**：可管理本部门用户（查看、创建、更新、删除、状态管理）
- **OP_ADMIN**：可查看用户列表、管理用户状态

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/user/list` | 用户列表 |
| Admin | POST | `/admin/user` | 创建用户 |
| Admin | POST | `/admin/user/{id}` | 更新用户 |
| Admin | POST | `/admin/user/{id}/delete` | 删除用户 |
| Admin | POST | `/admin/user/{id}/roles` | 分配角色 |
| Admin | GET | `/admin/user/roles` | 角色列表 |
| Admin | POST | `/admin/user/{id}/status` | 更新用户状态 |
| Admin | POST | `/admin/user/batch` | 批量导入用户 |

**业务规则：**
- 创建用户时可指定 roleIds，不指定默认分配"普通用户"角色(roleId=4)
- 禁用用户时不能禁用自己
- 批量导入返回每个用户的导入结果
- **关闭门户注册，用户只能通过管理端添加**

---

### 2.2 角色管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 系统角色的列表查询、菜单权限配置 |
| 涉及端 | Admin(/admin/role) |

**接口权限：**
- **SUPER_ADMIN**：可获取和更新角色菜单

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/role/list` | 角色列表 |
| Admin | GET | `/admin/role/{id}/menus` | 获取角色菜单树 |
| Admin | POST | `/admin/role/{id}/menus` | 更新角色菜单 |

**业务规则：**
- SUPER_ADMIN 角色的菜单不可编辑
- 角色 ID=1 为超级管理员角色，不可配置

---

### 2.3 部门管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 部门的 CRUD 操作 |
| 涉及端 | Admin(/admin/department) |

**接口权限：**
- **SUPER_ADMIN**：可创建、删除部门
- **DEPT_ADMIN**：可查看本部门信息
- **认证用户**：可查看启用中的部门

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/department/list` | 部门列表 |
| Admin | GET | `/admin/department/active` | 启用中部门 |
| Admin | POST | `/admin/department` | 创建部门 |
| Admin | POST | `/admin/department/{id}` | 更新部门 |
| Admin | POST | `/admin/department/{id}/delete` | 删除部门 |

---

### 2.4 团队管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 团队的 CRUD 及团队成员管理 |
| 涉及端 | Admin(/admin/team) |

**接口权限：**
- **SUPER_ADMIN**：可管理所有团队
- **DEPT_ADMIN**：可管理本部门团队

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/team/list` | 团队列表 |
| Admin | POST | `/admin/team` | 创建团队 |
| Admin | POST | `/admin/team/{id}` | 更新团队 |
| Admin | POST | `/admin/team/{id}/delete` | 删除团队 |
| Admin | GET | `/admin/team/{id}/members` | 查看团队成员 |
| Admin | POST | `/admin/team/{id}/members` | 添加成员到团队 |
| Admin | POST | `/admin/team/{id}/members/{userId}/delete` | 移出团队成员 |

---

### 2.5 菜单管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 菜单的查询、创建、更新、删除及角色菜单分配 |
| 涉及端 | Admin(/admin/menu) |

**接口权限：**
- **SUPER_ADMIN**：可执行所有操作

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/menu/tree` | 获取当前用户菜单树 |
| Admin | GET | `/admin/menu/list` | 菜单列表 |
| Admin | GET | `/admin/menu/{id}` | 菜单详情 |
| Admin | POST | `/admin/menu` | 创建菜单 |
| Admin | POST | `/admin/menu/{id}` | 更新菜单 |
| Admin | POST | `/admin/menu/{id}/delete` | 删除菜单 |

**业务规则：**
- 删除菜单前需先删除子菜单
- 被角色引用的菜单无法删除

---

## 三、数据看板与统计模块

### 3.1 数据看板

| 属性 | 说明 |
|------|------|
| 功能描述 | 系统概览数据、产出趋势、排行榜展示 |
| 涉及端 | Admin(/admin/dashboard) |

**接口权限：**
- **所有管理员角色**：概览、趋势、排行
- **SUPER_ADMIN/OP_ADMIN**：详细统计

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/dashboard/overview` | 概览数据 |
| Admin | GET | `/admin/dashboard/trend` | 产出趋势 |
| Admin | GET | `/admin/dashboard/ranking` | 成员排行榜 |
| Admin | GET | `/admin/dashboard/detail` | 详细统计 |

**业务规则：**
- trend 支持 day/week/month 粒度
- ranking 支持 week/month/all 时间段
- detail 按 user/project/dept 维度分组

---

### 3.2 项目统计

| 属性 | 说明 |
|------|------|
| 功能描述 | 项目维度的产出数据统计 |
| 涉及端 | Admin(/admin/stats) |

**接口权限：**
- **SUPER_ADMIN/OP_ADMIN**：可查看所有项目统计

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/stats/project/summary` | 项目整体产出汇总 |
| Admin | GET | `/admin/stats/project/members` | 项目内成员产出明细 |
| Admin | GET | `/admin/stats/project/user-distribution` | 个人项目产出分布 |

**业务规则：**
- 支持按项目名称筛选
- 支持日期范围筛选

---

### 3.3 部门统计

| 属性 | 说明 |
|------|------|
| 功能描述 | 部门维度的产出数据统计和排行 |
| 涉及端 | Admin(/admin/stats) |

**接口权限：**
- **SUPER_ADMIN/OP_ADMIN**：可查看所有部门统计
- **DEPT_ADMIN**：只能看本部门统计

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/stats/department/summary` | 部门整体产出汇总 |
| Admin | GET | `/admin/stats/department/members` | 部门内成员产出明细 |
| Admin | GET | `/admin/stats/department/ranking` | 部门产出排行 |

**业务规则：**
- ranking 支持按 total_code_lines/code_reviews 等指标排序

---

### 3.4 数据导出

| 属性 | 说明 |
|------|------|
| 功能描述 | 将产出统计数据导出为 Excel |
| 涉及端 | Admin(/admin/export) |

**接口权限：**
- **SUPER_ADMIN/OP_ADMIN**

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/export` | 导出产出数据 |

**业务规则：**
- 支持导出明细(detail)和汇总(summary)两种类型
- 文件名格式: 产出统计_{type}_{startDate}_{endDate}.xlsx

---

## 四、产出管理模块

### 4.1 个人产出管理（Console端 - 仅查询）

| 属性 | 说明 |
|------|------|
| 功能描述 | 成员查询个人日常产出数据 |
| 涉及端 | Console(/console/output) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Console | GET | `/console/output/today` | 查询今日产出 |
| Console | GET | `/console/output/history` | 查询历史产出 |

**业务规则：**
- 仅允许查询个人产出数据
- 不允许在 Console 端提交产出（提交请使用 Open 接口）

---

### 4.2 管理员产出管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 管理员多维度查询全员产出数据和汇总统计 |
| 涉及端 | Admin(/admin/output) |

**接口权限：**
- **SUPER_ADMIN/OP_ADMIN/DEPT_ADMIN**

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/output/list` | 查询产出明细（多维度筛选） |
| Admin | GET | `/admin/output/by-users` | 按人员查询产出汇总 |
| Admin | GET | `/admin/output/stats` | 产出汇总统计 |
| Admin | GET | `/admin/output/by-department` | 按部门汇总产出 |
| Admin | GET | `/admin/output/by-project` | 按项目汇总产出 |
| Admin | GET | `/admin/output/project-members` | 按项目查看成员产出 |

**多维度查询条件：**
- `date`: 统计日期
- `userIds`: 用户ID列表（为空则查全员）
- `deptIds`: 部门ID列表
- `projectNames`: 项目名称列表
- `startDate` / `endDate`: 时间范围

**业务规则：**
- DEPT_ADMIN 只能查看本部门数据
- stats 根据 isAdmin 标志决定数据范围

---

### 4.3 开放产出接口（仅提交）

| 属性 | 说明 |
|------|------|
| 功能描述 | 外部系统无需认证即可提交产出数据 |
| 涉及端 | Open(/open/output) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Open | POST | `/open/output/submit` | 提交产出数据 |

**业务规则：**
- 无需认证即可访问
- 通过 username 定位用户
- 不提供查询接口（查询请使用 Admin 接口）

---

## 五、Skill 管理模块

### 模块说明

| 属性 | 说明 |
|------|------|
| 功能描述 | Skill 是一种可复用的技能包，包含基础信息和上传的 ZIP 包 |
| 业务流程 | Console 上传/管理 → Admin 审核 → Portal 展示 |

### 数据结构

**Skill 实体：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 名称 |
| description | String | 描述 |
| category | String | 分类 |
| version | String | 当前版本号 |
| fileUrl | String | ZIP 包 OSS 地址 |
| fileName | String | 文件名 |
| status | Integer | 状态（0待审核 1通过 2拒绝） |
| creatorId | Long | 创建人ID |
| reviewTime | LocalDateTime | 审核时间 |
| reviewComment | String | 审核备注 |

### Console 端 - 内容上传/管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Console | GET | `/console/skill/list` | Skill 列表(带权限过滤) |
| Console | GET | `/console/skill/{id}` | Skill 详情 |
| Console | GET | `/console/skill/{id}/versions` | 版本列表 |
| Console | GET | `/console/skill/{id}/download` | 下载(重定向到预签名URL) |
| Console | POST | `/console/skill` | 创建 Skill |
| Console | POST | `/console/skill/{id}` | 更新 Skill |
| Console | POST | `/console/skill/{id}/delete` | 删除 Skill |
| Console | POST | `/console/skill/{id}/versions` | 发布新版本 |
| Console | POST | `/console/skill/upload` | 上传 ZIP 文件到 OSS |

**业务规则：**
- 提交后状态变为"待审核"
- 非管理员只能操作自己的资源
- 文件上传路径: `skills/{skillName}/v{version}/{randomFileName}`

### Admin 端 - 审核管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/skill/list` | Skill 列表(可筛选状态) |
| Admin | GET | `/admin/skill/{id}` | Skill 详情 |
| Admin | GET | `/admin/skill/{id}/versions` | 版本列表 |
| Admin | POST | `/admin/skill/{id}/audit` | 审核 Skill |
| Admin | POST | `/admin/skill/{id}/versions/{version}/delete` | 删除版本(仅SUPER_ADMIN) |
| Admin | GET | `/admin/skill/{id}/download` | 下载 |

**审核请求体：**
```json
{
  "status": 1,
  "reviewComment": "审核通过"
}
```

**业务规则：**
- 只有 SUPER_ADMIN 和 OP_ADMIN 有审核权限
- 审核通过后状态变为"已发布"，可在 Portal 展示
- 删除版本仅 SUPER_ADMIN 可操作

### Portal 端 - 公开浏览（无需认证）

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | GET | `/portal/open/resource/skill/list` | Skill 列表(分页) |
| Portal | GET | `/portal/open/resource/skill/{id}` | Skill 详情 |
| Portal | GET | `/portal/open/resource/skill/{id}/download` | 下载 ZIP |

**业务规则：**
- 只显示已审核通过(status=1)的 Skill
- 分页参数: page(默认1), size(默认10)
- 支持 keyword 关键词搜索

---

## 六、Plugin 管理模块

### 模块说明

| 属性 | 说明 |
|------|------|
| 功能描述 | Plugin 是扩展插件，包含基础信息和上传的 ZIP 包 |
| 业务流程 | Console 上传/管理 → Admin 审核 → Portal 展示 |

### 数据结构

**Plugin 实体：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| name | String | 名称 |
| description | String | 描述 |
| category | String | 分类 |
| icon | String | 图标URL |
| version | String | 当前版本号 |
| fileUrl | String | ZIP 包 OSS 地址 |
| fileName | String | 文件名 |
| status | Integer | 状态（0待审核 1通过 2拒绝） |
| creatorId | Long | 创建人ID |
| reviewTime | LocalDateTime | 审核时间 |
| reviewComment | String | 审核备注 |

### Console 端 - 内容上传/管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Console | GET | `/console/plugin/list` | Plugin 列表 |
| Console | GET | `/console/plugin/{id}` | Plugin 详情 |
| Console | GET | `/console/plugin/{id}/download` | 下载 |
| Console | POST | `/console/plugin` | 创建 Plugin |
| Console | POST | `/console/plugin/{id}` | 更新 Plugin |
| Console | POST | `/console/plugin/{id}/delete` | 删除 Plugin |
| Console | POST | `/console/plugin/upload` | 上传 ZIP 文件到 OSS |

**业务规则：**
- 提交后状态变为"待审核"
- 非管理员只能操作自己的资源

### Admin 端 - 审核管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/plugin/list` | Plugin 列表(可筛选状态) |
| Admin | GET | `/admin/plugin/{id}` | Plugin 详情 |
| Admin | POST | `/admin/plugin/{id}/audit` | 审核 Plugin |
| Admin | GET | `/admin/plugin/{id}/download` | 下载 |

**审核请求体：**
```json
{
  "status": 1,
  "reviewComment": "审核通过"
}
```

**业务规则：**
- 只有 SUPER_ADMIN 和 OP_ADMIN 有审核权限
- 审核通过后状态变为"已发布"，可在 Portal 展示

### Portal 端 - 公开浏览（无需认证）

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | GET | `/portal/open/resource/plugin/list` | Plugin 列表(分页) |
| Portal | GET | `/portal/open/resource/plugin/{id}` | Plugin 详情 |
| Portal | GET | `/portal/open/resource/plugin/{id}/download` | 下载 |

**业务规则：**
- 只显示已审核通过(status=1)的 Plugin
- 分页参数: page(默认1), size(默认10)
- 支持 keyword 关键词搜索

---

## 七、教程管理模块

### 模块说明

| 属性 | 说明 |
|------|------|
| 功能描述 | Tutorial 是学习教程，支持富文本、Markdown、视频、ZIP 附件 |
| 业务流程 | Console 上传/管理 → Admin 审核 → Portal 展示 |

### 数据结构

**Tutorial 实体：**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 主键 |
| title | String | 标题 |
| description | String | 简介 |
| category | String | 分类 |
| coverImage | String | 封面图URL |
| contentType | String | 内容类型（richText/markdown） |
| content | Text | 内容（富文本或Markdown） |
| videoUrl | String | 视频 OSS 地址 |
| zipFileUrl | String | 附件 ZIP OSS 地址 |
| zipFileName | String | 附件文件名 |
| status | Integer | 状态（0待审核 1通过 2拒绝） |
| creatorId | Long | 创建人ID |
| reviewTime | LocalDateTime | 审核时间 |
| reviewComment | String | 审核备注 |

**内容类型说明：**

| 类型 | contentType | 说明 |
|------|-------------|------|
| 富文本 | richText | HTML 格式的富文本内容 |
| Markdown | markdown | Markdown 格式的文本内容 |

### Console 端 - 内容上传/管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Console | GET | `/console/tutorial/list` | 教程列表 |
| Console | GET | `/console/tutorial/{id}` | 教程详情 |
| Console | POST | `/console/tutorial` | 创建教程 |
| Console | POST | `/console/tutorial/{id}` | 更新教程 |
| Console | POST | `/console/tutorial/{id}/delete` | 删除教程 |
| Console | POST | `/console/tutorial/upload/video` | 上传视频到 OSS |
| Console | POST | `/console/tutorial/upload/zip` | 上传 ZIP 附件到 OSS |

**创建/更新请求体：**
```json
{
  "title": "教程标题",
  "description": "教程简介",
  "category": "分类",
  "coverImage": "封面图URL",
  "contentType": "richText",
  "content": "<p>富文本内容</p>",
  "videoUrl": "视频OSS地址",
  "zipFileUrl": "ZIP OSS地址",
  "zipFileName": "附件文件名.zip"
}
```

**业务规则：**
- 提交后状态变为"待审核"
- 非管理员只能操作自己的资源
- 视频支持格式: mp4, mov, avi
- ZIP 附件用于提供课程相关资料下载

### Admin 端 - 审核管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/tutorial/list` | 教程列表(可筛选状态) |
| Admin | GET | `/admin/tutorial/{id}` | 教程详情 |
| Admin | POST | `/admin/tutorial/{id}/audit` | 审核教程 |
| Admin | GET | `/admin/tutorial/{id}/video` | 获取视频播放凭证/URL |

**审核请求体：**
```json
{
  "status": 1,
  "reviewComment": "审核通过"
}
```

**业务规则：**
- 只有 SUPER_ADMIN 和 OP_ADMIN 有审核权限
- 审核通过后状态变为"已发布"，可在 Portal 展示

### Portal 端 - 公开浏览（无需认证）

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | GET | `/portal/open/resource/tutorial/list` | 教程列表(分页) |
| Portal | GET | `/portal/open/resource/tutorial/{id}` | 教程详情 |
| Portal | GET | `/portal/open/resource/tutorial/{id}/video` | 获取视频播放地址 |
| Portal | GET | `/portal/open/resource/tutorial/{id}/zip` | 下载 ZIP 附件 |

**Portal 端内容渲染说明：**

| contentType | 渲染方式 |
|-------------|----------|
| richText | 直接渲染 HTML（需 XSS 过滤） |
| markdown | 解析后渲染为 HTML |

**视频播放流程：**
1. 调用 `/portal/open/resource/tutorial/{id}/video` 获取播放地址
2. 前端使用 video 标签播放

**业务规则：**
- 只显示已审核通过(status=1)的教程
- 分页参数: page(默认1), size(默认10)
- 支持 keyword 关键词搜索

---

## 八、MCP 服务器管理模块

### 8.1 MCP 服务器基础信息

| 属性 | 说明 |
|------|------|
| 功能描述 | MCP (Model Context Protocol) 服务器，提供 AI 交互能力 |
| 涉及端 | Admin、Console |

**Console 只读浏览：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Console | GET | `/console/mcp/list` | MCP 服务器列表 |
| Console | GET | `/console/mcp/{id}` | MCP 服务器详情 |
| Console | POST | `/console/mcp/{id}/test` | 测试连接 |

**Admin 完整管理：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/mcp/list` | MCP 服务器列表 |
| Admin | GET | `/admin/mcp/{id}` | MCP 服务器详情 |
| Admin | POST | `/admin/mcp` | 创建 MCP |
| Admin | POST | `/admin/mcp/{id}` | 更新 MCP |
| Admin | POST | `/admin/mcp/{id}/delete` | 删除 MCP |
| Admin | POST | `/admin/mcp/{id}/test` | 测试连接 |

**业务规则：**
- 非管理员只能操作自己的资源
- 测试连接需要 USER 及以上角色

---

## 九、社区模块

### 9.1 文章管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 文章的创建、编辑、发布、下架及互动 |
| 业务流程 | Console 创建/编辑 → Admin 下架 → Portal 展示/互动 |

#### 文章状态说明

| 状态 | 值 | 说明 |
|------|-----|------|
| 草稿 | 0 | 未发布状态，作者可继续编辑 |
| 已发布 | 1 | 已发布状态，在 Portal 端可见 |
| 已下架 | 2 | 管理员下架，不再在 Portal 端显示 |

#### Console 端 - 文章创建与管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Console | GET | `/console/article/list` | 我的文章列表(所有状态) |
| Console | GET | `/console/article/{id}` | 文章详情(编辑使用) |
| Console | POST | `/console/article` | 创建文章 |
| Console | POST | `/console/article/{id}` | 更新文章 |
| Console | POST | `/console/article/{id}/delete` | 删除文章 |
| Console | POST | `/console/article/{id}/publish` | 立即发布文章 |
| Console | POST | `/console/article/{id}/schedule` | 定时发布文章 |
| Console | POST | `/console/article/{id}/cancel-schedule` | 取消定时发布 |

**创建/更新请求体：**
```json
{
  "title": "文章标题",
  "content": "文章内容",
  "summary": "文章摘要",
  "coverImage": "封面图URL",
  "tagIds": [1, 2, 3],
  "publishType": 0,
  "scheduledPublishTime": "2026-05-01T10:00:00"
}
```

**publishType 说明：**
- `0`: 保存为草稿
- `1`: 立即发布
- `2`: 定时发布（需配合 scheduledPublishTime）

**业务规则：**
- 只有作者可以查看、更新、删除自己的文章
- 定时发布时间不能早于当前时间
- 已下架的文章不能发布

#### Admin 端 - 文章下架管理

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Admin | GET | `/admin/article/list` | 文章列表(所有状态) |
| Admin | GET | `/admin/article/{id}` | 文章详情 |
| Admin | POST | `/admin/article/{id}/takedown` | 下架文章 |

**下架请求体：**
```json
{
  "reason": "下架原因"
}
```

**业务规则：**
- 只有 SUPER_ADMIN 和 OP_ADMIN 可以下架文章
- 下架后文章状态变为"已下架"，不再在 Portal 端显示

#### Portal 端 - 文章展示与互动

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | GET | `/portal/article/list` | 文章列表(仅已发布) |
| Portal | GET | `/portal/article/{id}` | 文章详情 |
| Portal | POST | `/portal/article/{id}/like` | 点赞/取消点赞 |
| Portal | GET | `/portal/article/{id}/is-liked` | 检查是否已点赞 |

**业务规则：**
- 列表只显示 status=1(已发布) 的文章
- 支持按关键字、标签筛选和排序
- 排序规则: latest(最新)、hot(热门)
- 查看文章详情时阅读数自动 +1

---

### 9.2 问题管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 问题的 CRUD |
| 涉及端 | Portal(/portal/question) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 | 业务规则 |
|----|------|------|------|---------|
| Portal | GET | `/portal/question/list` | 问题列表 | - |
| Portal | GET | `/portal/question/{id}` | 问题详情 | - |
| Portal | POST | `/portal/question` | 创建问题 | - |
| Portal | POST | `/portal/question/{id}` | 更新问题 | 必须是作者 |
| Portal | POST | `/portal/question/{id}/delete` | 删除问题 | 必须是作者 |

**业务规则：**
- 只有作者可以更新和删除自己的问题

---

### 9.3 回答管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 回答的 CRUD、采纳、点赞 |
| 涉及端 | Portal(/portal) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 | 业务规则 |
|----|------|------|------|---------|
| Portal | GET | `/portal/question/{questionId}/answers` | 回答列表 | - |
| Portal | POST | `/portal/question/{questionId}/answer` | 创建回答 | - |
| Portal | POST | `/portal/answer/{id}` | 更新回答 | 必须是作者 |
| Portal | POST | `/portal/answer/{id}/delete` | 删除回答 | 必须是作者 |
| Portal | POST | `/portal/answer/{id}/accept` | 采纳回答 | 必须是问题作者 |
| Portal | POST | `/portal/answer/{id}/like` | 点赞回答 | 空实现 |

**业务规则：**
- 只有问题的作者可以采纳回答
- 采纳后问题状态变为已解决

---

### 9.4 评论管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 评论的 CRUD 和点赞（支持对文章、问题、回答评论） |
| 涉及端 | Portal(/portal/comment) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 | 业务规则 |
|----|------|------|------|---------|
| Portal | GET | `/portal/comment/list` | 评论列表 | - |
| Portal | POST | `/portal/comment` | 创建评论 | - |
| Portal | POST | `/portal/comment/{id}/delete` | 删除评论 | 必须是作者 |
| Portal | POST | `/portal/comment/{id}/like` | 点赞评论 | 空实现 |

**业务规则：**
- 支持二级评论(parentId)
- targetType 支持: article/question/answer

---

### 9.5 收藏管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 用户收藏和取消收藏内容 |
| 涉及端 | Portal(/portal/favorite) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 | 业务规则 |
|----|------|------|------|---------|
| Portal | POST | `/portal/favorite` | 添加收藏 | 幂等操作 |
| Portal | POST | `/portal/favorite/remove` | 取消收藏 | 幂等操作 |
| Portal | GET | `/portal/favorite/list` | 收藏列表 | - |
| Portal | GET | `/portal/favorite/check` | 检查是否已收藏 | - |

**业务规则：**
- targetType 支持: article/question/answer
- 可按类型筛选收藏列表

---

### 9.6 浏览历史

| 属性 | 说明 |
|------|------|
| 功能描述 | 记录和查询用户浏览历史 |
| 涉及端 | Portal(/portal/browse-history) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | POST | `/portal/browse-history` | 添加浏览记录 |
| Portal | GET | `/portal/browse-history/list` | 浏览历史列表 |
| Portal | POST | `/portal/browse-history/clear` | 清除浏览历史 |

---

### 9.7 通知管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 用户通知消息查询和已读标记 |
| 涉及端 | Portal(/portal/notification) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | GET | `/portal/notification/list` | 通知列表 |
| Portal | POST | `/portal/notification/{id}/read` | 标记已读 |
| Portal | GET | `/portal/notification/unread-count` | 未读数量 |

---

### 9.8 标签管理

| 属性 | 说明 |
|------|------|
| 功能描述 | 标签的创建和查询 |
| 涉及端 | Portal(/portal/tag) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | GET | `/portal/tag/list` | 标签列表 |
| Portal | POST | `/portal/tag` | 创建标签 |

---

## 十、文件存储模块

### 10.1 OSS 文件存储

| 属性 | 说明 |
|------|------|
| 功能描述 | 获取七牛云上传凭证 |
| 涉及端 | 通用(/oss) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| 通用 | GET | `/oss/token?keyPrefix=xxx` | 获取上传凭证 |

**业务规则：**
- 返回 token、domain 和 keyPrefix

---

## 十一、公开信息模块

### 11.1 公告与站点信息

| 属性 | 说明 |
|------|------|
| 功能描述 | 公告和站点信息公开查询 |
| 涉及端 | Portal(/portal/open/public) |

**接口列表：**

| 端 | 方法 | 路径 | 说明 |
|----|------|------|------|
| Portal | GET | `/portal/open/public/announcement` | 公告列表 (TODO) |
| Portal | GET | `/portal/open/public/site-info` | 站点信息 (TODO) |

**业务规则：**
- 当前为 TODO 实现，返回 null

---

## 附录

### A. 角色说明

| 角色代码 | 角色名称 | 权限说明 |
|----------|----------|----------|
| SUPER_ADMIN | 超级管理员 | 拥有系统所有权限 |
| OP_ADMIN | 运营管理员 | 运营管理权限，可查看统计数据 |
| DEPT_ADMIN | 部门管理员 | 部门管理权限，只能查看本部门数据 |
| USER | 普通用户 | 基本用户权限，可使用社区功能 |
| Guest | 访客 | 未登录用户，仅可访问公开接口 |

**默认角色**：新建用户时如未指定角色，默认为 USER（普通用户）。

---

### B. 端说明

| 端 | 路径前缀 | 认证要求 | 说明 |
|----|----------|---------|------|
| Admin | `/admin/*` | 需认证 | 管理端，供管理员使用 |
| Console | `/console/*` | 需认证 | 控制台端，供普通用户日常使用 |
| Portal | `/portal/*` | 需认证 | 门户端，供社区用户使用 |
| Portal Open | `/portal/open/*` | 无需认证 | 公开接口，访客可访问 |
| Open | `/open/*` | 无需认证 | 开放接口，无需认证 |
| Common | `/auth/*`, `/oss/*` | 部分需认证 | 通用功能，多端共用 |

---

### C. Controller 文件位置索引

```
ai-studio-service/src/main/java/com/aistudio/service/controller/
├── common/
│   ├── AuthController.java                    # 统一认证(/auth)
│   └── OssController.java                     # 文件上传(/oss)
├── portal/
│   ├── PortalPublicInfoController.java        # 公开信息
│   ├── PortalResourceBrowseController.java    # 资源公开浏览
│   ├── ArticleController.java                # 文章管理
│   ├── QuestionController.java               # 问题管理
│   ├── AnswerController.java                 # 回答管理
│   ├── CommentController.java                # 评论管理
│   ├── FavoriteController.java               # 收藏管理
│   ├── BrowseHistoryController.java          # 浏览历史
│   ├── NotificationController.java            # 通知管理
│   └── TagController.java                    # 标签管理
├── console/
│   ├── ConsoleSkillController.java           # Skill 浏览
│   ├── ConsolePluginController.java          # Plugin 浏览
│   ├── ConsoleTutorialController.java         # 教程浏览
│   ├── ConsoleMcpController.java             # MCP 浏览
│   ├── ConsoleOutputController.java          # 个人产出
│   └── ConsoleArticleController.java        # 文章管理
├── admin/
│   ├── AdminDashboardController.java         # 数据看板
│   ├── AdminStatsController.java             # 产出统计
│   ├── AdminUserController.java              # 用户管理
│   ├── AdminRoleController.java             # 角色管理
│   ├── AdminMenuController.java             # 菜单管理
│   ├── AdminDepartmentController.java        # 部门管理
│   ├── AdminTeamController.java             # 团队管理
│   ├── AdminSkillController.java            # Skill 管理
│   ├── AdminPluginController.java           # Plugin 管理
│   ├── AdminTutorialController.java         # 教程管理
│   ├── AdminMcpController.java               # MCP 管理
│   ├── AdminOutputController.java           # 产出管理
│   ├── AdminExportController.java           # 数据导出
│   └── AdminArticleController.java          # 文章管理
└── open/
    └── OpenOutputController.java             # 开放产出接口
```
