import { defineConfig, devices } from '@playwright/test'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import '../../scripts/load-playwright-env.mjs'

const __dirname = path.dirname(fileURLToPath(import.meta.url))
const zhReporter: [string, { outputFolder: string }] = [
  path.resolve(__dirname, '../../scripts/playwright-zh-reporter.cjs'),
  { outputFolder: 'playwright-report-zh' }
]

export default defineConfig({
  testDir: './tests/e2e',
  timeout: 60_000,
  expect: {
    timeout: 10_000
  },
  fullyParallel: false,
  retries: process.env.CI ? 1 : 0,
  reporter: process.env.CI
    ? [['list'], ['junit', { outputFile: 'test-results/e2e-junit.xml' }], zhReporter]
    : [['list'], ['html', { outputFolder: 'playwright-report', open: 'never' }], zhReporter],
  use: {
    baseURL: process.env.CONSOLE_BASE_URL || 'http://localhost:5174/ai-studio-plus-console-web',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure'
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] }
    }
  ]
})
