# AI-Studio 测试用例文档

## 1. 文档说明

本文档依据 [AI-Studio-业务功能清单](./AI-Studio-业务功能清单.md) 生成，覆盖认证授权、后台管理、资源管理、社区模块、开放接口与公共信息等核心能力，适用于接口测试、联调测试与回归测试。

- 文档版本：v1.0
- 生成日期：2026-04-24
- 覆盖对象：`/auth`、`/admin/*`、`/console/*`、`/portal/*`、`/portal/open/*`、`/open/*`、`/oss/*`

## 2. 测试范围

包含：
- 功能正确性（正向流程）
- 权限控制（角色与鉴权）
- 参数校验（必填、格式、边界）
- 状态流转（草稿/发布/下架、审核通过/拒绝）
- 幂等与一致性（收藏、点赞、已读标记）

不包含：
- 压测与性能基准（单独性能测试计划）
- 安全渗透专项（单独安全测试计划）
- 前端像素级视觉验收（UI专项）

## 3. 角色与前置数据

测试角色：
- `SUPER_ADMIN`
- `OP_ADMIN`
- `DEPT_ADMIN`
- `USER`
- `Guest`（未登录）

建议准备基础数据：
- 用户：至少 1 个各角色账号
- 部门与团队：至少 2 个部门、每部门 2 个团队
- 资源：Skill/Plugin/Tutorial 各 3 条（含待审核、已通过、已拒绝）
- 社区：文章 5 篇、问题 5 条、回答 10 条、评论 10 条

## 4. 测试用例

## 4.1 认证授权（AUTH）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| AUTH-001 | `GET /auth/public-key` | 获取公钥成功 | 无 | 调用接口 | 返回 `code=200`，`data` 为有效公钥字符串 | P0 |
| AUTH-002 | `POST /auth/token` | 用户名+RSA加密密码登录成功 | 存在有效用户 | 先取公钥，加密密码后登录 | 返回 token，状态成功 | P0 |
| AUTH-003 | `POST /auth/token` | 错误密码登录失败 | 存在有效用户 | 用错误密码登录 | 返回业务错误码与错误信息 | P0 |
| AUTH-004 | `GET /auth/user-info` | 获取当前用户信息成功 | 已登录并携带 token | 调用接口 | 返回用户信息（`user_id/username/roles` 等） | P0 |
| AUTH-005 | `GET /auth/user-info` | 未登录获取用户信息失败 | 未登录 | 调用接口 | 返回 401 或未授权业务码 | P0 |
| AUTH-006 | `POST /auth/logout` | 登出成功 | 已登录 | 调用登出接口 | 返回成功，后续受保护接口不可访问 | P1 |
| AUTH-007 | `POST /auth/change-password` | 修改密码成功 | 已登录 | 提交旧密码+新密码 | 返回成功，使用新密码可登录 | P1 |
| AUTH-008 | `GET /auth/gen-hash?pwd=xxx` | 调试哈希生成 | 无 | 调用接口 | 返回合法 bcrypt 哈希 | P2 |

## 4.2 用户与权限管理（ADMIN-ACL）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| ADMIN-ACL-001 | `GET /admin/user/list` | 用户列表查询 | `SUPER_ADMIN` 登录 | 调用接口 | 返回分页列表 | P0 |
| ADMIN-ACL-002 | `POST /admin/user` | 创建用户成功 | `SUPER_ADMIN` 登录 | 提交完整参数 | 用户创建成功，默认或指定角色生效 | P0 |
| ADMIN-ACL-003 | `POST /admin/user/{id}/status` | 禁用自己失败 | 任意管理员登录 | 传自身 id 禁用 | 返回失败并提示不能禁用自己 | P0 |
| ADMIN-ACL-004 | `POST /admin/user/batch` | 批量导入结果校验 | `SUPER_ADMIN` 登录 | 上传混合有效/无效数据 | 返回逐条导入结果 | P1 |
| ADMIN-ACL-005 | `GET /admin/role/list` | 角色列表查询 | `SUPER_ADMIN` 登录 | 调用接口 | 返回角色列表 | P1 |
| ADMIN-ACL-006 | `POST /admin/role/{id}/menus` | 配置角色菜单 | `SUPER_ADMIN` 登录 | 提交菜单 ID 集合 | 配置成功，权限即时生效 | P1 |
| ADMIN-ACL-007 | `GET /admin/department/list` | 部门列表查询 | 管理员登录 | 调用接口 | 返回部门列表 | P1 |
| ADMIN-ACL-008 | `POST /admin/team/{id}/members` | 团队新增成员 | 管理员登录 | 为团队添加成员 | 添加成功且成员可查询 | P1 |
| ADMIN-ACL-009 | `GET /admin/menu/tree` | 菜单树查询 | 管理员登录 | 调用接口 | 返回完整菜单树 | P1 |
| ADMIN-ACL-010 | `POST /admin/menu/{id}/delete` | 删除被引用菜单失败 | `SUPER_ADMIN` 登录 | 删除被角色引用菜单 | 返回失败并给出原因 | P0 |

## 4.3 数据看板与统计导出（ADMIN-STAT）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| ADMIN-STAT-001 | `GET /admin/dashboard/overview` | 看板概览成功 | 管理员登录 | 调用接口 | 返回汇总指标 | P0 |
| ADMIN-STAT-002 | `GET /admin/dashboard/trend` | 趋势按粒度查询 | 管理员登录 | 分别传 day/week/month | 返回对应粒度趋势数据 | P1 |
| ADMIN-STAT-003 | `GET /admin/dashboard/ranking` | 排行查询 | 管理员登录 | 传 week/month/all | 返回排序结果 | P1 |
| ADMIN-STAT-004 | `GET /admin/stats/project/summary` | 项目汇总统计 | `OP_ADMIN` 登录 | 指定时间范围查询 | 返回项目汇总统计 | P1 |
| ADMIN-STAT-005 | `GET /admin/stats/department/ranking` | 部门排行权限校验 | `DEPT_ADMIN` 登录 | 查询部门排行 | 仅返回本部门可见范围 | P0 |
| ADMIN-STAT-006 | `GET /admin/export` | 导出文件成功 | `OP_ADMIN` 登录 | 传类型+时间范围 | 返回可下载 Excel 文件流 | P1 |

## 4.4 产出管理（OUTPUT）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| OUTPUT-001 | `GET /console/output/today` | 个人今日产出查询 | `USER` 登录 | 调用接口 | 返回当前用户今日产出 | P1 |
| OUTPUT-002 | `GET /console/output/history` | 个人历史产出查询 | `USER` 登录 | 指定时间范围 | 返回当前用户历史产出 | P1 |
| OUTPUT-003 | `GET /admin/output/list` | 管理端多维产出明细 | 管理员登录 | 传 `userIds/deptIds/projectNames` | 返回过滤后的明细列表 | P1 |
| OUTPUT-004 | `GET /admin/output/stats` | 管理端统计汇总 | 管理员登录 | 调用接口 | 返回统计指标 | P1 |
| OUTPUT-005 | `POST /open/output/submit` | 开放提交成功 | 无需登录 | 提交有效 username 与产出数据 | 返回成功 | P0 |
| OUTPUT-006 | `POST /open/output/submit` | 开放提交参数错误 | 无需登录 | 缺少必填字段提交 | 返回参数校验错误 | P0 |

## 4.5 Skill/Plugin/Tutorial（RESOURCE）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| RES-001 | `POST /console/skill` | 创建 Skill | `USER` 登录 | 提交 Skill 信息 | 创建成功，状态为待审核 | P1 |
| RES-002 | `POST /admin/skill/{id}/audit` | 审核 Skill 通过 | `OP_ADMIN` 登录 | 审核状态传通过 | 状态变为已发布 | P0 |
| RES-003 | `GET /portal/open/resource/skill/list` | 门户公开 Skill 列表 | Guest | 查询分页列表 | 仅返回审核通过数据 | P0 |
| RES-004 | `GET /portal/open/resource/skill/{id}/download` | Skill 下载链接 | Guest | 下载已发布 Skill | 可成功下载 ZIP | P1 |
| RES-005 | `POST /console/plugin` | 创建 Plugin | `USER` 登录 | 提交 Plugin 信息 | 创建成功待审核 | P1 |
| RES-006 | `POST /admin/plugin/{id}/audit` | 审核 Plugin 拒绝 | `OP_ADMIN` 登录 | 审核状态传拒绝 | 状态变更为拒绝并记录意见 | P1 |
| RES-007 | `GET /portal/open/resource/plugin/list` | 门户公开 Plugin 列表 | Guest | 查询列表 | 仅展示已审核通过 | P0 |
| RES-008 | `POST /console/tutorial` | 创建 Tutorial | `USER` 登录 | 提交内容与附件信息 | 创建成功待审核 | P1 |
| RES-009 | `GET /portal/open/resource/tutorial/{id}/video` | 获取教程视频地址 | Guest | 查询视频地址 | 返回可播放 URL | P1 |
| RES-010 | `GET /portal/open/resource/tutorial/{id}/zip` | 下载教程附件 ZIP | Guest | 下载 ZIP | 返回可下载文件 | P1 |

## 4.6 MCP 服务管理（MCP）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| MCP-001 | `POST /admin/mcp` | 新建 MCP 服务 | 管理员登录 | 提交服务配置 | 创建成功 | P1 |
| MCP-002 | `POST /admin/mcp/{id}/test` | 管理端测试连接 | 管理员登录 | 测试连接 | 返回连接成功/失败原因 | P1 |
| MCP-003 | `GET /console/mcp/list` | 控制台只读列表 | `USER` 登录 | 查询列表 | 可查看可见范围内服务 | P1 |
| MCP-004 | `POST /console/mcp/{id}/test` | 控制台测试权限 | `USER` 登录 | 测试连接 | 权限符合角色定义 | P1 |

## 4.7 社区：文章（COMM-ARTICLE）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| COMM-ARTICLE-001 | `POST /console/article` | 创建文章草稿 | `USER` 登录 | `publishType=0` 提交 | 状态为草稿 | P0 |
| COMM-ARTICLE-002 | `POST /console/article/{id}/publish` | 立即发布文章 | 作者登录 | 发布草稿 | 状态变为已发布 | P0 |
| COMM-ARTICLE-003 | `POST /console/article/{id}/schedule` | 定时发布文章 | 作者登录 | 设置未来时间发布 | 定时任务建立成功 | P1 |
| COMM-ARTICLE-004 | `GET /portal/article/list` | 门户文章列表 | 登录用户 | 查询列表 | 仅返回已发布文章 | P0 |
| COMM-ARTICLE-005 | `POST /portal/article/{id}/like` | 点赞文章幂等 | 登录用户 | 连续点赞/取消点赞 | 状态切换正确，无脏数据 | P1 |
| COMM-ARTICLE-006 | `POST /admin/article/{id}/takedown` | 管理员下架文章 | `OP_ADMIN` 登录 | 下架目标文章 | 门户不再可见该文章 | P0 |

## 4.8 社区：问答（COMM-QA）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| COMM-QA-001 | `POST /portal/question` | 发布问题成功 | 登录用户 | 提交标题与内容 | 问题创建成功 | P0 |
| COMM-QA-002 | `POST /portal/question/{id}` | 非作者编辑失败 | 用户A创建问题，用户B登录 | 用户B编辑 | 返回无权限 | P0 |
| COMM-QA-003 | `POST /portal/question/{questionId}/answer` | 发布回答成功 | 登录用户 | 提交回答内容 | 回答创建成功 | P0 |
| COMM-QA-004 | `POST /portal/answer/{id}/accept` | 采纳回答成功 | 问题作者登录 | 采纳某回答 | 回答标记已采纳，问题状态更新 | P0 |
| COMM-QA-005 | `POST /portal/answer/{id}/accept` | 非问题作者采纳失败 | 非问题作者登录 | 采纳回答 | 返回无权限 | P0 |
| COMM-QA-006 | `POST /portal/answer/{id}/like` | 回答点赞 | 登录用户 | 点赞回答 | 点赞计数更新 | P1 |

## 4.9 社区：评论/收藏/历史/通知/标签（COMM-EXT）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| COMM-EXT-001 | `POST /portal/comment` | 发表评论成功 | 登录用户 | 对文章/问题提交评论 | 评论创建成功 | P1 |
| COMM-EXT-002 | `POST /portal/comment/{id}/delete` | 非作者删评论失败 | 用户A评论，用户B登录 | 用户B删除评论 | 返回无权限 | P0 |
| COMM-EXT-003 | `POST /portal/favorite` | 添加收藏幂等 | 登录用户 | 同对象重复收藏 | 结果幂等，不重复记录 | P1 |
| COMM-EXT-004 | `POST /portal/favorite/remove` | 取消收藏幂等 | 登录用户 | 对未收藏对象取消收藏 | 返回成功或幂等成功 | P1 |
| COMM-EXT-005 | `POST /portal/browse-history` | 添加浏览历史 | 登录用户 | 上报浏览记录 | 记录写入成功 | P1 |
| COMM-EXT-006 | `POST /portal/browse-history/clear` | 清空浏览历史 | 登录用户 | 清空后查询列表 | 列表为空 | P1 |
| COMM-EXT-007 | `POST /portal/notification/{id}/read` | 通知已读 | 登录用户 | 标记已读 | 状态更新，未读数减少 | P1 |
| COMM-EXT-008 | `GET /portal/tag/list` | 标签列表查询 | 登录用户 | 查询标签列表 | 返回标签集合 | P1 |

## 4.10 OSS 与公共信息（COMMON）

| 用例ID | 接口 | 标题 | 前置条件 | 步骤 | 预期结果 | 优先级 |
|---|---|---|---|---|---|---|
| COMMON-001 | `GET /oss/token?keyPrefix=xxx` | 获取上传凭证成功 | 已登录 | 调用接口 | 返回 token/domain/keyPrefix | P0 |
| COMMON-002 | `GET /oss/token?keyPrefix=xxx` | 未登录获取凭证失败 | 未登录 | 调用接口 | 返回未授权 | P0 |
| COMMON-003 | `GET /portal/open/public/announcement` | 公告公开查询 | Guest | 查询接口 | 返回公告数据或 TODO 约定值 | P2 |
| COMMON-004 | `GET /portal/open/public/site-info` | 站点信息公开查询 | Guest | 查询接口 | 返回站点信息或 TODO 约定值 | P2 |

## 5. 回归建议

每次发版至少执行：
- 所有 P0 用例
- 认证相关 `AUTH-001` 到 `AUTH-006`
- 资源公开浏览链路 `RES-003`、`RES-007`、`RES-009`
- 社区主链路 `COMM-ARTICLE-001/002/004`、`COMM-QA-001/003/004`

## 6. 缺陷分级建议

- S1：核心流程不可用（登录失败、主列表不可用、权限绕过）
- S2：重要功能异常（状态流转错误、数据不一致）
- S3：一般缺陷（提示文案、边界校验）
- S4：优化项（兼容性、体验细节）

---

如需，我可以继续基于本文件再生成：
- 禅道/Jira 可导入的 CSV 用例模板
- Postman/Apifox 场景化集合（按用例ID命名）
- 冒烟测试清单（15分钟快速回归版）
