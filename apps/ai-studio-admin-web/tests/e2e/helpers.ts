import { expect, type Page, test } from '@playwright/test'

export async function loginAdmin(page: Page) {
  const username = process.env.ADMIN_USERNAME
  const password = process.env.ADMIN_PASSWORD
  test.skip(!username || !password, 'Set ADMIN_USERNAME and ADMIN_PASSWORD to run authenticated admin coverage.')

  await page.goto('/#/admin/login')
  await page.getByPlaceholder('请输入用户名').fill(username!)
  await page.getByPlaceholder('请输入密码').fill(password!)
  await page.getByRole('button', { name: /立即登录/ }).click()
  await expect(page).toHaveURL(/\/admin\/dashboard/)
  await expect.poll(async () => page.evaluate(() => localStorage.getItem('token'))).not.toBeNull()
}

export async function expectAdminRoute(page: Page, path: string, title: RegExp) {
  await page.goto(`/#${path}`)
  await expect(page).not.toHaveURL(/\/admin\/login/)
  await expect(page.locator('.page-title').filter({ hasText: title }).first()).toBeVisible()
}
