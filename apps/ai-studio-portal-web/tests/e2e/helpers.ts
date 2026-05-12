import { expect, type Page } from '@playwright/test'

export async function addLocalPortalLogin(page: Page) {
  await page.context().addCookies([
    {
      name: 'Admin-Token',
      value: 'local-smoke-token',
      url: process.env.PORTAL_COOKIE_URL || 'http://localhost:3000'
    }
  ])
  await page.addInitScript(() => {
    localStorage.setItem('token', 'local-smoke-token')
    localStorage.setItem('userInfo', JSON.stringify({
      token: 'local-smoke-token',
      username: 'local-smoke-user',
      userName: 'local-smoke-user',
      real_name: '本地烟测用户',
      roles: ['SUPER_ADMIN']
    }))
  })
}

export async function expectPortalRoute(page: Page, path: string, text: RegExp) {
  await page.goto(path)
  await expect(page).not.toHaveURL(/\/login/)
  await expect(page.locator('body')).toContainText(text)
}
