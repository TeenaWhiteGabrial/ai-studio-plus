import { expect, type Page, test } from '@playwright/test'

const username = process.env.PORTAL_USERNAME || 'admin'
const password = process.env.PORTAL_PASSWORD || 'admin123'

function normalizeUrl(raw: string): string {
  return raw.replace(/^https?:\/\/[^/]+/, '')
}

function trackApiStatus(page: Page, prefix: string) {
  const statuses: number[] = []

  page.on('response', (resp) => {
    const url = normalizeUrl(resp.url())
    if (url.startsWith(prefix)) {
      statuses.push(resp.status())
    }
  })

  return () => statuses.at(-1)
}

async function loginAsAdmin(page: Page) {
  await page.goto('/login', { waitUntil: 'domcontentloaded' })
  await page.waitForLoadState('networkidle')

  const usernameInput = page.locator('.login-form input[type="text"]').first()
  const passwordInput = page.locator('.login-form input[type="password"]').first()
  await usernameInput.fill(username)
  await passwordInput.fill(password)
  await page.locator('.login-form button.login-btn').click()
  await page.waitForTimeout(300)
  if (page.url().includes('/login')) {
    await passwordInput.press('Enter')
  }

  await expect(page).not.toHaveURL(/\/login/)
  await expect.poll(async () => {
    return await page.evaluate(() => localStorage.getItem('token'))
  }).not.toBeNull()
}

test.describe('Portal Home Core APIs', () => {
  test('article list should be 200 after login', async ({ page }) => {
    const getAuthTokenStatus = trackApiStatus(page, '/auth/token')
    const getUserInfoStatus = trackApiStatus(page, '/auth/user-info')
    const getStatus = trackApiStatus(page, '/portal/article/list')
    await loginAsAdmin(page)

    await expect.poll(() => getAuthTokenStatus(), {
      timeout: 15_000
    }).toBe(200)
    await expect.poll(() => getUserInfoStatus(), {
      timeout: 15_000
    }).toBe(200)
    await expect.poll(() => getStatus(), {
      timeout: 15_000
    }).toBe(200)
  })

  test('resource list should be 200 after login', async ({ page }) => {
    const getAuthTokenStatus = trackApiStatus(page, '/auth/token')
    const getUserInfoStatus = trackApiStatus(page, '/auth/user-info')
    const getStatus = trackApiStatus(page, '/portal/open/resource/skill/list')
    await loginAsAdmin(page)

    await expect.poll(() => getAuthTokenStatus(), {
      timeout: 15_000
    }).toBe(200)
    await expect.poll(() => getUserInfoStatus(), {
      timeout: 15_000
    }).toBe(200)
    await expect.poll(() => getStatus(), {
      timeout: 15_000
    }).toBe(200)
  })
})
