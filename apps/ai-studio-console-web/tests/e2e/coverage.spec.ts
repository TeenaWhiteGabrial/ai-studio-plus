import { test } from '@playwright/test'
import { expectConsoleRoute, loginConsole } from './helpers'

const routes: Array<{ path: string; title: RegExp }> = [
  { path: '/console/dashboard', title: /工作台/ },
  { path: '/console/apikey', title: /API Key 管理/ },
  { path: '/console/task', title: /每日任务/ },
  { path: '/console/article', title: /文章管理/ },
  { path: '/console/project', title: /项目管理|工作台/ },
  { path: '/console/resource', title: /资源中心/ },
  { path: '/console/stats', title: /产出统计/ },
  { path: '/console/settings', title: /个人设置/ },
  { path: '/console/messages', title: /消息中心/ }
]

test.describe('Console Route Coverage', () => {
  test.beforeEach(async ({ page }) => {
    await loginConsole(page)
  })

  for (const route of routes) {
    test(`${route.path} is reachable`, async ({ page }) => {
      await expectConsoleRoute(page, route.path, route.title)
    })
  }
})
