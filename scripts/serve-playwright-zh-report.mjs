import http from 'node:http'
import fs from 'node:fs/promises'
import path from 'node:path'

const reportDir = path.resolve(process.cwd(), 'playwright-report-zh')
const preferredPort = Number.parseInt(process.env.REPORT_PORT || '9334', 10)

async function exists(filePath) {
  try {
    await fs.access(filePath)
    return true
  } catch {
    return false
  }
}

function contentType(filePath) {
  if (filePath.endsWith('.html')) return 'text/html; charset=utf-8'
  if (filePath.endsWith('.css')) return 'text/css; charset=utf-8'
  if (filePath.endsWith('.js')) return 'text/javascript; charset=utf-8'
  if (filePath.endsWith('.png')) return 'image/png'
  if (filePath.endsWith('.jpg') || filePath.endsWith('.jpeg')) return 'image/jpeg'
  if (filePath.endsWith('.webm')) return 'video/webm'
  if (filePath.endsWith('.zip')) return 'application/zip'
  if (filePath.endsWith('.md')) return 'text/markdown; charset=utf-8'
  return 'application/octet-stream'
}

async function listen(server, port) {
  return new Promise((resolve, reject) => {
    function cleanup() {
      server.off('error', onError)
      server.off('listening', onListening)
    }

    function onError(error) {
      cleanup()
      reject(error)
    }

    function onListening() {
      cleanup()
      resolve(port)
    }

    server.once('error', onError)
    server.once('listening', onListening)
    server.listen(port, '127.0.0.1')
  })
}

async function findPort(server, startPort) {
  for (let port = startPort; port < startPort + 20; port += 1) {
    try {
      return await listen(server, port)
    } catch (error) {
      try {
        server.close()
      } catch {
        // The server may not have started listening yet.
      }
      if (error.code !== 'EADDRINUSE' && error.code !== 'EPERM') throw error
    }
  }
  throw new Error(`No available report port from ${startPort} to ${startPort + 19}`)
}

if (!await exists(path.join(reportDir, 'index.html'))) {
  console.error(`未找到中文测试报告：${path.join(reportDir, 'index.html')}`)
  console.error('请先运行对应的 coverage 测试，例如 pnpm test:console:coverage')
  process.exit(1)
}

const server = http.createServer(async (request, response) => {
  const url = new URL(request.url || '/', 'http://127.0.0.1')
  const safePath = path.normalize(decodeURIComponent(url.pathname)).replace(/^(\.\.[/\\])+/, '')
  const requestedPath = safePath === '/' ? '/index.html' : safePath
  const filePath = path.join(reportDir, requestedPath)

  if (!filePath.startsWith(reportDir)) {
    response.writeHead(403)
    response.end('Forbidden')
    return
  }

  try {
    const body = await fs.readFile(filePath)
    response.writeHead(200, { 'Content-Type': contentType(filePath) })
    response.end(body)
  } catch {
    response.writeHead(404)
    response.end('Not found')
  }
})

const port = await findPort(server, preferredPort)
console.log(`中文测试报告：http://127.0.0.1:${port}`)
console.log('按 Ctrl+C 停止报告服务。')
