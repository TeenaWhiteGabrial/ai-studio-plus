import request from '@/utils/request'

export const tutorialApi = {
  // 查询
  list: (params: any) => request.get('/tutorial/list', { params }),
  detail: (id: number) => request.get(`/tutorial/${id}`),
  versions: (id: number) => request.get(`/tutorial/${id}/versions`),

  // 管理
  create: (data: any) => request.post('/tutorial', data),
  update: (id: number, data: any) => request.post(`/tutorial/${id}`, data),
  delete: (id: number) => request.post(`/tutorial/${id}/delete`),
  publishVersion: (id: number, data: any) => request.post(`/tutorial/${id}/versions`, data),

  // 上传文件到 OSS
  uploadVideo: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/tutorial/upload/video', formData)
  },

  uploadZip: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/tutorial/upload/zip', formData)
  },
}
