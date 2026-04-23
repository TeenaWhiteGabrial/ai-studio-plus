import request from '@/utils/request'

export const pluginApi = {
  // 查询
  list: (params: any) => request.get('/plugin/list', { params }),
  detail: (id: number) => request.get(`/plugin/${id}`),
  versions: (id: number) => request.get(`/plugin/${id}/versions`),

  // 管理
  create: (data: any) => request.post('/plugin', data),
  update: (id: number, data: any) => request.post(`/plugin/${id}`, data),
  delete: (id: number) => request.post(`/plugin/${id}/delete`),
  publishVersion: (id: number, data: any) => request.post(`/plugin/${id}/versions`, data),

  // 下载
  download: (id: number) => request.get(`/plugin/${id}/download`),

  // 上传文件到 OSS
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/plugin/upload', formData)
  },
}
