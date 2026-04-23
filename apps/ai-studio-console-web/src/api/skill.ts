import request from '@/utils/request'

export const skillApi = {
  // 查询
  list: (params: any) => request.get('/skill/list', { params }),
  detail: (id: number) => request.get(`/skill/${id}`),
  versions: (id: number) => request.get(`/skill/${id}/versions`),

  // 管理
  create: (data: any) => request.post('/skill', data),
  update: (id: number, data: any) => request.post(`/skill/${id}`, data),
  delete: (id: number) => request.post(`/skill/${id}/delete`),

  // 版本管理
  publishVersion: (id: number, data: any) => request.post(`/skill/${id}/versions`, data),

  // 下载（返回 302 重定向）
  download: (id: number, version?: string) => request.get(`/skill/${id}/download`, {
    params: { version },
    maxRedirects: 0
  }),
  getDownloadUrl: (id: number, version?: string) => {
    const params = version ? `?version=${version}` : ''
    return `/skill/${id}/download${params}`
  },

  // 上传文件到 OSS
  upload: (file: File, skillName: string, version: string) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('skillName', skillName)
    formData.append('version', version)
    return request.post('/skill/upload', formData)
  },
}
