import { expect, test } from '@playwright/test'

test.describe('Portal Smoke', () => {
  test('admin login and homepage core APIs should be 200', async ({ page }) => {
    const username = process.env.PORTAL_USERNAME || 'admin'
    const password = process.env.PORTAL_PASSWORD || 'admin123'

    const tracked: Record<string, number> = {}
    const targets = [
      '/auth/public-key',
      '/auth/token',
      '/auth/user-info',
      '/portal/article/list',
      '/portal/question/list',
      '/portal/tag/list'
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
    await inputs.nth(0).fill(username)
    await inputs.nth(1).fill(password)
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
    expect(status('/portal/question/list')).toBe(200)
    expect(status('/portal/tag/list')).toBe(200)
  })
})
