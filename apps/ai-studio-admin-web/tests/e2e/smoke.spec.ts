import { expect, test } from '@playwright/test'

const username = process.env.ADMIN_USERNAME
const password = process.env.ADMIN_PASSWORD

test.describe('Admin Smoke', () => {
  test('login page is reachable', async ({ page }) => {
    await page.goto('/#/admin/login')

    await expect(page.getByRole('heading', { name: /登录到 AI Studio/ })).toBeVisible()
    await expect(page.getByPlaceholder('请输入用户名')).toBeVisible()
    await expect(page.getByPlaceholder('请输入密码')).toBeVisible()
    await expect(page.getByRole('button', { name: /立即登录/ })).toBeVisible()
  })

  test('authenticated dashboard is reachable when credentials are configured', async ({ page }) => {
    test.skip(!username || !password, 'Set ADMIN_USERNAME and ADMIN_PASSWORD to run authenticated admin smoke.')

    await page.goto('/#/admin/login')
    await page.getByPlaceholder('请输入用户名').fill(username!)
    await page.getByPlaceholder('请输入密码').fill(password!)
    await page.getByRole('button', { name: /立即登录/ }).click()

    await expect(page).toHaveURL(/\/admin\/dashboard/)
    await expect.poll(async () => page.evaluate(() => localStorage.getItem('token'))).not.toBeNull()
    await expect(page.locator('.main-content, body')).toContainText(/数据看板|产出|AI Studio/)
  })
})
