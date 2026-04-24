import { chromium } from 'playwright'

const baseURL = process.env.PORTAL_BASE_URL || 'http://localhost:3000'
const username = process.env.PORTAL_USERNAME || 'admin'
const password = process.env.PORTAL_PASSWORD || 'admin123'
const chromePath = process.env.PORTAL_CHROME_PATH || 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe'

const tracked = [
  '/auth/public-key',
  '/auth/token',
  '/auth/user-info',
  '/portal/article/list',
  '/portal/question/list',
  '/portal/tag/list'
]

const summary = {
  baseURL,
  currentUrl: '',
  hasToken: false,
  api: {},
  pageErrors: [],
  consoleErrors: []
}

const browser = await chromium.launch({
  headless: true,
  executablePath: chromePath
})

try {
  const page = await browser.newPage()

  page.on('response', (resp) => {
    const raw = resp.url()
    const url = raw.replace(/^https?:\/\/[^/]+/, '')
    if (tracked.some(item => url.startsWith(item))) {
      summary.api[url] = resp.status()
    }
  })

  page.on('pageerror', (err) => {
    summary.pageErrors.push(err.message)
  })

  page.on('console', (msg) => {
    if (msg.type() === 'error') {
      summary.consoleErrors.push(msg.text())
    }
  })

  await page.goto(`${baseURL}/login`, { waitUntil: 'domcontentloaded' })
  await page.waitForLoadState('networkidle')
  const formInputs = page.locator('.login-form input')
  summary.formInputCount = await formInputs.count()
  const placeholders = []
  for (let i = 0; i < summary.formInputCount; i += 1) {
    placeholders.push(await formInputs.nth(i).getAttribute('placeholder'))
  }
  summary.formInputPlaceholders = placeholders

  const usernameInput = page.locator('.login-form input[type="text"]').first()
  const passwordInput = page.locator('.login-form input[type="password"]').first()
  await usernameInput.fill(username)
  await passwordInput.fill(password)
  summary.filled = {
    username: await usernameInput.inputValue(),
    passwordLength: (await passwordInput.inputValue()).length
  }

  await page.locator('.login-form button.login-btn').click()
  await page.waitForTimeout(300)
  if (page.url().includes('/login')) {
    await passwordInput.press('Enter')
  }
  await page.waitForTimeout(6000)

  summary.currentUrl = page.url()
  summary.hasToken = Boolean(await page.evaluate(() => localStorage.getItem('token')))
  summary.loginError = await page.locator('.login-error-alert').first().textContent().catch(() => '')

  console.log(JSON.stringify(summary, null, 2))
} finally {
  await browser.close()
}
