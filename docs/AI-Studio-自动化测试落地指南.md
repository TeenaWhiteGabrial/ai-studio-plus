# AI-Studio 自动化测试落地指南

## 1. 目标

基于现有《AI-Studio-测试用例文档》，建立可持续执行的自动化测试体系，优先保障 P0 主链路：
- 登录鉴权
- 首页核心数据加载
- 社区文章/问答主流程
- 资源公开浏览主流程

## 2. 分层策略（推荐顺序）

1. 接口自动化（后端主力）：覆盖广、定位快  
2. E2E 冒烟（前端主链路）：模拟真实用户点击  
3. CI 持续集成：每次提交自动执行并留痕

## 3. 项目目录建议

```text
ai-studio/
├─ ai-studio-service/
│  └─ src/test/java/                  # 后端接口自动化测试（JUnit）
├─ apps/ai-studio-portal-web/
│  ├─ playwright.config.ts            # 已新增
│  ├─ tests/e2e/                      # 已新增
│  │  └─ smoke-login.spec.ts          # 已新增
│  └─ scripts/
│     └─ e2e-login.mjs                # 已有脚本，可保留
└─ docs/
   ├─ AI-Studio-测试用例文档.md
   ├─ AI-Studio-自动化测试落地指南.md  # 本文
   └─ templates/
      └─ github-actions-automation-template.yml
```

## 4. 首批 10 条自动化用例（建议第一阶段）

| 编号 | 用例ID | 层级 | 场景 |
|---|---|---|---|
| 1 | AUTH-001 | API | `GET /auth/public-key` 返回成功 |
| 2 | AUTH-002 | API | `POST /auth/token` 登录成功 |
| 3 | AUTH-004 | API | `GET /auth/user-info` 返回用户信息 |
| 4 | AUTH-005 | API | 未登录访问 `/auth/user-info` 返回未授权 |
| 5 | COMM-ARTICLE-004 | API | `GET /portal/article/list` 已登录可访问 |
| 6 | COMM-QA-001 | API | `POST /portal/question` 发布问题成功 |
| 7 | COMM-QA-003 | API | `POST /portal/question/{id}/answer` 回答成功 |
| 8 | RES-003 | API | `GET /portal/open/resource/skill/list` 公开可访问 |
| 9 | RES-007 | API | `GET /portal/open/resource/plugin/list` 公开可访问 |
| 10 | E2E-SMOKE-LOGIN | E2E | 浏览器输入账号密码并点击登录，首页关键接口均 200 |

## 5. 已落地的前端 E2E 命令

位置：`apps/ai-studio-portal-web/package.json`

```bash
pnpm -C apps/ai-studio-portal-web run test:e2e:install
pnpm -C apps/ai-studio-portal-web run test:e2e:smoke
pnpm -C apps/ai-studio-portal-web run test:e2e
```

环境变量（可选）：

```bash
PORTAL_BASE_URL=http://localhost:3000
PORTAL_USERNAME=admin
PORTAL_PASSWORD=admin123
```

## 6. 后端接口自动化建议（Java）

建议在 `ai-studio-service/src/test/java` 中按模块建立测试类：
- `auth/AuthApiTest`
- `portal/ArticleApiTest`
- `portal/QuestionApiTest`
- `resource/OpenResourceApiTest`

基础技术栈：
- JUnit5（已随 `spring-boot-starter-test` 引入）
- MockMvc 或 RestAssured（二选一）
- 可选 Testcontainers（后续阶段再加）

执行命令：

```bash
mvn -f ai-studio-service/pom.xml test
```

## 7. 执行节奏建议

1. 每次开发自测：`test:e2e:smoke` + 相关 API 用例  
2. 每次 PR：执行首批 10 条  
3. 每日晚间：全量 API + E2E 冒烟回归

## 8. 报告与失败留痕

前端 E2E 已配置：
- 失败截图
- 失败视频
- 重试时 trace

建议 CI 保留：
- `test-results/`
- Playwright 报告目录
- Maven surefire 报告

