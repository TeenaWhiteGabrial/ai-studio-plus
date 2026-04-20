import request from '@/utils/request'

export const articleApi = {
  // 我的文章列表（所有状态）
  list: (params: { status?: number; keyword?: string; page?: number; size?: number }) => request.get('/article/list', { params }),
  // 文章详情（编辑使用）
  detail: (id: number) => request.get(`/article/${id}`),
  // 创建文章
  create: (data: any) => request.post('/article', data),
  // 更新文章
  update: (id: number, data: any) => request.post(`/article/${id}`, data),
  // 删除文章
  delete: (id: number) => request.post(`/article/${id}/delete`),
  // 立即发布文章
  publish: (id: number) => request.post(`/article/${id}/publish`),
  // 定时发布文章
  schedule: (id: number, scheduledPublishTime: string) => request.post(`/article/${id}/schedule`, { scheduledPublishTime }),
  // 取消定时发布
  cancelSchedule: (id: number) => request.post(`/article/${id}/cancel-schedule`),
}
