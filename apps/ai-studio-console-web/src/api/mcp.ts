import request from '@/utils/request'

export const mcpApi = {
  // 查询
  list: (params: any) => request.get('/mcp/list', { params }),
  detail: (id: number) => request.get(`/mcp/${id}`),

  // 测试连接
  test: (id: number) => request.post(`/mcp/${id}/test`),
}
