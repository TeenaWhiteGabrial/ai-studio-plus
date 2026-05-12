const fs = require('node:fs')
const path = require('node:path')

const STATUS_LABELS = {
  passed: '通过',
  failed: '失败',
  timedOut: '超时',
  skipped: '跳过',
  interrupted: '中断'
}

const STATUS_CLASS = {
  passed: 'status-passed',
  failed: 'status-failed',
  timedOut: 'status-failed',
  skipped: 'status-skipped',
  interrupted: 'status-failed'
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;')
}

function formatDuration(ms) {
  if (!Number.isFinite(ms)) return '0 秒'
  if (ms < 1000) return `${Math.round(ms)} 毫秒`
  return `${(ms / 1000).toFixed(1)} 秒`
}

function formatDate(date) {
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  }).format(date)
}

class ChineseHtmlReporter {
  constructor(options = {}) {
    this.outputFolder = options.outputFolder || 'playwright-report-zh'
    this.tests = []
    this.startedAt = new Date()
  }

  onBegin(config) {
    this.config = config
    this.rootDir = config.rootDir || process.cwd()
  }

  onTestEnd(test, result) {
    const titlePath = test.titlePath().filter(Boolean)
    const project = titlePath[1] || ''
    const suite = titlePath.slice(2, -1).join(' / ')
    const title = titlePath.at(-1) || test.title
    const location = test.location || {}
    const error = result.error || result.errors?.[0]

    this.tests.push({
      project,
      suite,
      title,
      status: result.status,
      duration: result.duration,
      file: location.file ? path.relative(this.rootDir, location.file) : '',
      line: location.line,
      error: error?.message || '',
      attachments: (result.attachments || [])
        .filter((attachment) => attachment.path)
        .map((attachment) => ({
          name: attachment.name,
          contentType: attachment.contentType,
          path: attachment.path
        }))
    })
  }

  async onEnd(result) {
    const outputDir = path.resolve(process.cwd(), this.outputFolder)
    fs.mkdirSync(outputDir, { recursive: true })
    fs.writeFileSync(path.join(outputDir, 'index.html'), this.render(result, outputDir), 'utf8')
  }

  render(result, outputDir) {
    const total = this.tests.length
    const passed = this.tests.filter((test) => test.status === 'passed').length
    const failed = this.tests.filter((test) => ['failed', 'timedOut', 'interrupted'].includes(test.status)).length
    const skipped = this.tests.filter((test) => test.status === 'skipped').length
    const duration = this.tests.reduce((sum, test) => sum + (test.duration || 0), 0)
    const nativeReport = path.relative(outputDir, path.resolve(process.cwd(), 'playwright-report/index.html'))

    return `<!doctype html>
<html lang="zh-CN">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>AI Studio 自动化测试报告</title>
  <style>
    :root {
      color-scheme: light;
      --bg: #f6f2ea;
      --panel: #fffaf0;
      --ink: #1f2933;
      --muted: #6b7280;
      --line: #e5dccb;
      --passed: #157f3b;
      --failed: #b42318;
      --skipped: #8a5a00;
      --chip: #f1e7d4;
    }
    * { box-sizing: border-box; }
    body {
      margin: 0;
      background:
        radial-gradient(circle at 15% 12%, rgba(255, 198, 109, 0.28), transparent 28rem),
        linear-gradient(135deg, #fffaf0 0%, var(--bg) 48%, #eef4ef 100%);
      color: var(--ink);
      font-family: "Noto Serif SC", "Songti SC", "PingFang SC", serif;
      min-height: 100vh;
    }
    main {
      max-width: 1180px;
      margin: 0 auto;
      padding: 48px 24px 72px;
    }
    header {
      display: flex;
      justify-content: space-between;
      gap: 24px;
      align-items: flex-end;
      margin-bottom: 28px;
    }
    h1 {
      margin: 0 0 10px;
      font-size: clamp(32px, 5vw, 56px);
      line-height: 1.05;
      letter-spacing: -0.04em;
    }
    .subtitle {
      color: var(--muted);
      margin: 0;
      font-size: 15px;
    }
    .native-link {
      color: var(--ink);
      background: var(--panel);
      border: 1px solid var(--line);
      border-radius: 999px;
      padding: 10px 14px;
      text-decoration: none;
      white-space: nowrap;
      box-shadow: 0 8px 24px rgba(31, 41, 51, 0.06);
    }
    .cards {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: 14px;
      margin: 26px 0;
    }
    .card {
      background: rgba(255, 250, 240, 0.84);
      border: 1px solid var(--line);
      border-radius: 22px;
      padding: 18px;
      box-shadow: 0 18px 50px rgba(31, 41, 51, 0.07);
      backdrop-filter: blur(12px);
    }
    .card strong {
      display: block;
      font-size: 32px;
      line-height: 1;
      margin-bottom: 8px;
    }
    .card span {
      color: var(--muted);
      font-size: 14px;
    }
    .toolbar {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 26px 0 14px;
      gap: 16px;
      color: var(--muted);
      font-size: 14px;
    }
    .result {
      background: rgba(255, 250, 240, 0.88);
      border: 1px solid var(--line);
      border-radius: 20px;
      margin-bottom: 12px;
      overflow: hidden;
      box-shadow: 0 12px 34px rgba(31, 41, 51, 0.05);
    }
    .result summary {
      cursor: pointer;
      list-style: none;
      display: grid;
      grid-template-columns: 88px 1fr auto;
      gap: 16px;
      align-items: center;
      padding: 16px 18px;
    }
    .result summary::-webkit-details-marker { display: none; }
    .status {
      border-radius: 999px;
      padding: 7px 12px;
      text-align: center;
      font-size: 13px;
      font-weight: 700;
      background: var(--chip);
    }
    .status-passed { color: var(--passed); }
    .status-failed { color: var(--failed); }
    .status-skipped { color: var(--skipped); }
    .title {
      min-width: 0;
      font-weight: 700;
    }
    .meta {
      margin-top: 5px;
      color: var(--muted);
      font-size: 13px;
      overflow-wrap: anywhere;
    }
    .duration {
      color: var(--muted);
      font-variant-numeric: tabular-nums;
    }
    .details {
      border-top: 1px solid var(--line);
      padding: 16px 18px 18px 122px;
    }
    pre {
      white-space: pre-wrap;
      overflow-wrap: anywhere;
      background: #1f2933;
      color: #fffaf0;
      border-radius: 14px;
      padding: 14px;
      line-height: 1.5;
      font-size: 13px;
    }
    .attachments {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      margin-top: 12px;
    }
    .attachments a {
      color: var(--ink);
      background: var(--chip);
      border-radius: 999px;
      padding: 7px 10px;
      text-decoration: none;
      font-size: 13px;
    }
    @media (max-width: 760px) {
      header { display: block; }
      .native-link { display: inline-block; margin-top: 18px; }
      .cards { grid-template-columns: repeat(2, minmax(0, 1fr)); }
      .result summary { grid-template-columns: 1fr; }
      .details { padding-left: 18px; }
    }
  </style>
</head>
<body>
  <main>
    <header>
      <div>
        <h1>自动化测试报告</h1>
        <p class="subtitle">生成时间：${escapeHtml(formatDate(new Date()))}，整体状态：${escapeHtml(STATUS_LABELS[result.status] || result.status)}</p>
      </div>
      <a class="native-link" href="${escapeHtml(nativeReport)}">打开 Playwright 原生报告</a>
    </header>

    <section class="cards" aria-label="测试概览">
      <div class="card"><strong>${total}</strong><span>用例总数</span></div>
      <div class="card"><strong>${passed}</strong><span>通过</span></div>
      <div class="card"><strong>${failed}</strong><span>失败 / 超时</span></div>
      <div class="card"><strong>${formatDuration(duration)}</strong><span>总耗时</span></div>
    </section>

    <div class="toolbar">
      <span>跳过：${skipped}</span>
      <span>点击单条用例可查看错误、截图、视频和 trace 附件</span>
    </div>

    <section aria-label="测试明细">
      ${this.tests.map((test) => this.renderTest(test, outputDir)).join('\n')}
    </section>
  </main>
</body>
</html>`
  }

  renderTest(test, outputDir) {
    const statusLabel = STATUS_LABELS[test.status] || test.status
    const statusClass = STATUS_CLASS[test.status] || ''
    const location = [test.file, test.line].filter(Boolean).join(':')
    const details = [
      test.error ? `<pre>${escapeHtml(test.error)}</pre>` : '',
      this.renderAttachments(test.attachments, outputDir)
    ].filter(Boolean).join('\n')

    return `<details class="result"${test.status !== 'passed' ? ' open' : ''}>
  <summary>
    <span class="status ${statusClass}">${escapeHtml(statusLabel)}</span>
    <span class="title">
      ${escapeHtml(test.title)}
      <span class="meta">${escapeHtml([test.project, test.suite, location].filter(Boolean).join(' / '))}</span>
    </span>
    <span class="duration">${escapeHtml(formatDuration(test.duration))}</span>
  </summary>
  ${details ? `<div class="details">${details}</div>` : ''}
</details>`
  }

  renderAttachments(attachments, outputDir) {
    if (!attachments.length) return ''

    return `<div class="attachments">
      ${attachments.map((attachment) => {
        const href = path.relative(outputDir, attachment.path)
        return `<a href="${escapeHtml(href)}">${escapeHtml(this.translateAttachment(attachment.name))}</a>`
      }).join('\n')}
    </div>`
  }

  translateAttachment(name) {
    if (name === 'screenshot') return '截图'
    if (name === 'video') return '视频'
    if (name === 'trace') return 'Trace 调试包'
    if (name === 'error-context') return '错误上下文'
    return name
  }
}

module.exports = ChineseHtmlReporter
