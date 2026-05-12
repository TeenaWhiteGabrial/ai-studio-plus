import { expect, type Page, test } from '@playwright/test'

export async function loginConsole(page: Page) {
  const username = process.env.CONSOLE_USERNAME
  const password = process.env.CONSOLE_PASSWORD
  test.skip(!username || !password, 'Set CONSOLE_USERNAME and CONSOLE_PASSWORD to run authenticated console coverage.')

  await page.goto('/#/console/login')
  await page.getByPlaceholder('请输入用户名').fill(username!)
  await page.getByPlaceholder('请输入密码').fill(password!)
  await page.getByRole('button', { name: /立即登录/ }).click()
  await expect(page).toHaveURL(/\/console\/dashboard/)
  await expect.poll(async () => page.evaluate(() => localStorage.getItem('token'))).not.toBeNull()
}

export async function expectConsoleRoute(page: Page, path: string, title: RegExp) {
  await page.goto(`/#${path}`)
  await expect(page).not.toHaveURL(/\/console\/login/)
  await expect(page.locator('.page-title').filter({ hasText: title }).first()).toBeVisible()
}
