import * as qiniu from 'qiniu-js'
import request from './request'

interface UploadResult {
  ossKey: string
  url: string
}

export async function uploadToOss(file: File, keyPrefix = ''): Promise<UploadResult> {
  // 1. 获取 UpToken
  const res = await request.get('/oss/token', { params: { keyPrefix } }) as any
  const { token, domain } = res.data

  // 2. 生成文件 key
  const ext = file.name.split('.').pop()
  const key = `${keyPrefix}${Date.now()}_${Math.random().toString(36).slice(2)}.${ext}`

  // 3. 直传七牛云
  return new Promise((resolve, reject) => {
    const observable = qiniu.upload(file, key, token, {}, { useCdnDomain: true })
    observable.subscribe({
      next: () => {},
      error: (err) => reject(err),
      complete: () => {
        const url = `${domain}/${key}`
        resolve({ ossKey: key, url })
      },
    })
  })
}
