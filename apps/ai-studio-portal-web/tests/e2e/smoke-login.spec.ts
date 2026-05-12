import { expect, test } from '@playwright/test'
import { addLocalPortalLogin } from './helpers'

test.describe('Portal Smoke', () => {
  test('public landing exposes login, article, and resource entry points', async ({ page }) => {
    await page.goto('/')

    await expect(page.getByRole('button', { name: /登录/ })).toBeVisible()
    await expect(page.getByRole('button', { name: /资源中心/ })).toBeVisible()
    await expect(page.getByRole('searchbox', { name: /搜索知识库、文章、资源/ })).toBeVisible()
  })

  test('discover page exposes article and resource sections with local login state', async ({ page }) => {
    await addLocalPortalLogin(page)

    await page.goto('/discover')

    await expect(page.getByText('推荐文章')).toBeVisible()
    await expect(page.getByText('资源管理')).toBeVisible()
    await expect(page.getByRole('button', { name: /^Skill$/ }).first()).toBeVisible()
    await expect(page.getByRole('button', { name: /^Plugin$/ }).first()).toBeVisible()
  })

  test('admin login and homepage core APIs should be 200 when credentials are configured', async ({ page }) => {
    const username = process.env.PORTAL_USERNAME
    const password = process.env.PORTAL_PASSWORD
    test.skip(!username || !password, 'Set PORTAL_USERNAME and PORTAL_PASSWORD to run authenticated portal smoke.')

    const tracked: Record<string, number> = {}
    const targets = [
      '/auth/public-key',
      '/auth/token',
      '/auth/user-info',
      '/portal/article/list',
      '/portal/open/resource/skill/list'
    ]

    page.on('response', (resp) => {
      const full = resp.url()
      const url = full.replace(/^https?:\/\/[^/]+/, '')
      if (targets.some(t => url.startsWith(t))) {
        tracked[url] = resp.status()
      }
    })

    await page.goto('/login')

    const inputs = page.locator('input')
    await inputs.nth(0).fill(username!)
    await inputs.nth(1).fill(password!)
    await page.getByRole('button', { name: /登录|立即登录/ }).click()

    await expect(page).not.toHaveURL(/\/login/)
    await expect.poll(async () => {
      return await page.evaluate(() => localStorage.getItem('token'))
    }).not.toBeNull()

    await page.waitForTimeout(4_000)

    const status = (prefix: string) => {
      const hit = Object.entries(tracked).find(([k]) => k.startsWith(prefix))
      return hit?.[1]
    }

    expect(status('/auth/public-key')).toBe(200)
    expect(status('/auth/token')).toBe(200)
    expect(status('/auth/user-info')).toBe(200)
    expect(status('/portal/article/list')).toBe(200)
    expect(status('/portal/open/resource/skill/list')).toBe(200)
  })
})
