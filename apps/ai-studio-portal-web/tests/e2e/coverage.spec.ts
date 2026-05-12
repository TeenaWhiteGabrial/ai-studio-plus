import { test } from '@playwright/test'
import { addLocalPortalLogin, expectPortalRoute } from './helpers'

const routes: Array<{ path: string; text: RegExp }> = [
  { path: '/', text: /AI Studio|搜索知识库、文章、资源/ },
  { path: '/discover', text: /推荐文章|资源管理/ },
  { path: '/resources', text: /资源管理|Skill|Plugin/ },
  { path: '/knowledge', text: /知识库|检索|搜索/ },
  { path: '/profile', text: /个人中心|我的资源|收藏/ }
]

test.describe('Portal Route Coverage', () => {
  test.beforeEach(async ({ page }) => {
    await addLocalPortalLogin(page)
  })

  for (const route of routes) {
    test(`${route.path} is reachable`, async ({ page }) => {
      await expectPortalRoute(page, route.path, route.text)
    })
  }
})
