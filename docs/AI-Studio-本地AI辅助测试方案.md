# AI Studio 本地 AI 辅助测试方案

## 目标

本方案先建立本地可重复运行的传统自动化测试能力，再把 AI 放到更适合的位置：帮助编造测试数据、生成自动化脚本草稿、扩展边界用例、分析失败日志和截图。

最终通过与否由确定性脚本判断，例如 Playwright、JUnit、Spring MVC/Jackson/Bean Validation 测试。AI 不直接作为回归测试的裁判。

## 本地前置条件

- Node.js 版本需满足现有前端依赖要求。
- 使用 `corepack pnpm install` 安装依赖。
- 后端使用 Java 17 和 Maven。
- 首次运行浏览器测试前，在对应前端目录运行 Playwright 浏览器安装命令，或使用包脚本安装 Chromium。
- 需要真实后端集成时，本地后端默认地址为 `http://localhost:8080/ai-studio/v1`。
- 需要登录烟测时，使用环境变量提供测试账号，不要把真实密码写入测试代码。

## 本地命令

```bash
pnpm test:backend
pnpm test:portal:smoke
pnpm test:admin:smoke
pnpm test:console:smoke
pnpm test:local:smoke
```

前端 smoke 运行前需要对应 dev server 已启动：

```bash
pnpm dev:portal
pnpm dev:admin
pnpm dev:console
```

认证后的浏览器 smoke 需要显式配置账号：

```bash
PORTAL_USERNAME=xxx PORTAL_PASSWORD=xxx pnpm test:portal:smoke
ADMIN_USERNAME=xxx ADMIN_PASSWORD=xxx pnpm test:admin:smoke
CONSOLE_USERNAME=xxx CONSOLE_PASSWORD=xxx pnpm test:console:smoke
```

也可以使用本地私有配置文件，避免每次手动 export：

```bash
cp apps/ai-studio-portal-web/tests/e2e/.env.example apps/ai-studio-portal-web/tests/e2e/.env.local
cp apps/ai-studio-admin-web/tests/e2e/.env.example apps/ai-studio-admin-web/tests/e2e/.env.local
cp apps/ai-studio-console-web/tests/e2e/.env.example apps/ai-studio-console-web/tests/e2e/.env.local
```

也支持直接使用 `tests/e2e/.env`。`.env` 和 `.env.local` 都已被根 `.gitignore` 忽略，可以填写真实测试账号。命令行环境变量优先级更高，适合临时覆盖。Portal 如果改了本地 host 或端口，同步调整 `PORTAL_BASE_URL` 和 `PORTAL_COOKIE_URL`。

没有配置账号时，认证后的 smoke 会跳过；登录页、公共入口、局部登录态 smoke 仍会运行。

扩展路由覆盖命令：

```bash
pnpm test:portal:coverage
pnpm test:admin:coverage
pnpm test:console:coverage
pnpm test:local:coverage
```

覆盖原则：

- 覆盖当前有效功能页面是否可达、是否保持登录态、是否显示稳定页面标题或核心区块。
- 不覆盖已经移出 PRD 的问答社区作为必过目标。
- 不在第一版广覆盖里执行破坏性新增、删除、审核提交等数据变更动作。

OpenSpec 状态命令：

```bash
pnpm openspec:status:testing
pnpm openspec:apply:testing
```

## 测试分层

### 后端契约测试

优先验证不依赖数据库的接口边界：

- 请求 DTO 的字段命名和兼容性。
- 必填字段校验。
- 响应包裹结构。
- `snake_case` JSON 合约。

第一批测试不直接验证数据库业务流，避免测试用例撞上本地或远程数据库状态。

### Playwright 烟测

三端都使用 Playwright 做最小浏览器烟测：

- portal：公共页面、文章/资源入口、登录入口。
- admin：登录页可打开；配置账号后验证至少一个后台管理页面可进入。
- console：登录页可打开；配置账号后验证至少一个工作台页面可进入。

未配置账号时，登录后的烟测必须清晰跳过或给出明确提示，不能伪装成业务失败。

## 测试数据策略

### 前端纯页面烟测

如果测试目标只是验证页面结构、路由和基础交互，可以使用 Playwright route mock 或静态 fixture，避免依赖后端数据库。

### 真实集成烟测

如果测试目标是验证前后端真实联通，必须使用以下任一方式：

- 文档化的测试账号。
- 测试前显式创建的数据。
- 可回填的本地种子数据。

不要让 smoke test 依赖“某个数据库里刚好存在的数据”。

## AI 怎么参与

AI 可以做：

- 根据 PRD 和代码生成测试用例草稿。
- 编造边界测试数据，例如空字符串、超长标题、缺失字段、类型漂移。
- 生成 Playwright/JUnit/MockMvc 脚本初稿。
- 根据失败日志、trace、截图总结可能原因。
- 帮助发现传统自动化不容易主动想到的路径，例如异常空态、权限错配、字段命名漂移。

AI 不应该做：

- 代替断言决定回归测试通过与否。
- 直接把一次浏览器观察当成稳定测试结论。
- 生成后无人审查、无人运行、无人维护的测试代码。

## AI 输出验收标准

AI 产出的测试资产只有满足以下条件，才算进入项目测试能力：

- 已经变成仓库里的代码、fixture 或文档。
- 有明确预期结果。
- 可以本地重复运行。
- 失败时能解释是环境问题、数据问题还是业务问题。

## AI 误判记录模板

```md
日期：
场景：
AI 建议：
实际原因：
修正后的判断：
后续改进：
```

## OpenSpec 变更

当前测试建设通过 OpenSpec change `add-local-ai-testing` 管理：

```bash
pnpm openspec:status:testing
```

后续如果继续实现或归档测试方案，应优先更新对应 OpenSpec artifacts，而不是散落修改。

## 当前验证记录

- `mvn -f ai-studio-service/pom.xml test`：通过，8 个测试全部成功。
- `pnpm --filter ai-studio-portal-web test:e2e:smoke`：通过，2 个 smoke 成功，1 个认证 smoke 因未配置账号跳过。
- `pnpm --filter ai-studio-web test:e2e:smoke`：通过，1 个登录页 smoke 成功，1 个认证 smoke 因未配置账号跳过。
- `pnpm --filter ai-studio-console-web test:e2e:smoke`：通过，1 个登录页 smoke 成功，1 个认证 smoke 因未配置账号跳过。
