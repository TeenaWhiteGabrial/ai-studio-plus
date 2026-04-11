import request from '@/utils/request'

export const skillApi = {
  list: (params: any) => request.get('/skill/list', { params }),
  detail: (id: number) => request.get(`/skill/${id}`),
  // 下载 Skill ZIP 包（包含 SKILL.md 和辅助文件）
  download: (id: number) => request.get(`/skill/${id}/download`, { responseType: 'blob' }),
  // 获取 Raw 内容（供 Claude Code 使用）- 返回纯文本，不经过响应拦截器处理
  raw: (id: number) => request.get(`/skill/${id}/raw`, { responseType: 'text', transformResponse: [(data) => data] }),
  // 手动触发同步（仅管理员）
  sync: () => request.post('/skill/sync'),
  // 查询同步历史
  syncLog: (limit: number = 20) => request.get('/skill/sync-log', { params: { limit } }),
}

export const mcpApi = {
  list: (params: any) => request.get('/mcp/list', { params }),
  create: (data: any) => request.post('/mcp', data),
  update: (id: number, data: any) => request.put(`/mcp/${id}`, data),
  delete: (id: number) => request.delete(`/mcp/${id}`),
  test: (id: number) => request.post(`/mcp/${id}/test`),
}

export const pluginApi = {
  list: (params: any) => request.get('/plugin/list', { params }),
  create: (data: any) => request.post('/plugin', data),
  update: (id: number, data: any) => request.put(`/plugin/${id}`, data),
  delete: (id: number) => request.delete(`/plugin/${id}`),
  download: (id: number) => request.get(`/plugin/${id}/download`),
}

export const tutorialApi = {
  list: (params: any) => request.get('/tutorial/list', { params }),
  create: (data: any) => request.post('/tutorial', data),
  update: (id: number, data: any) => request.put(`/tutorial/${id}`, data),
  delete: (id: number) => request.delete(`/tutorial/${id}`),
  detail: (id: number) => request.get(`/tutorial/${id}`),
}

export const outputApi = {
  today: () => request.get('/output/today'),
  submit: (data: any) => request.post('/output', data),
  history: (params: any) => request.get('/output/history', { params }),
  adminList: (params: any) => request.get('/output/admin/list', { params }),
  stats: (params: any) => request.get('/output/stats', { params }),
  // 开放 API
  openSubmit: (data: any) => request.post('/api/open/output/submit', data),
  openToday: (username: string) => request.get('/api/open/output/today', { params: { username } }),
  // 部门统计
  deptSummary: (params: any) => request.get('/output/stats/department/summary', { params }),
  deptMembers: (params: any) => request.get('/output/stats/department/members', { params }),
  deptRanking: (params: any) => request.get('/output/stats/department/ranking', { params }),
  // 项目统计
  projectSummary: (params: any) => request.get('/output/stats/project/summary', { params }),
  projectMembers: (params: any) => request.get('/output/stats/project/members', { params }),
  userProjectDistribution: (params: any) => request.get('/output/stats/project/user-distribution', { params }),
  // 导出
  export: (params: any) => request.get('/output/export', { params, responseType: 'blob' }),
}

export const departmentApi = {
  list: () => request.get('/department/list'),
  active: () => request.get('/department/active'),
  create: (data: any) => request.post('/department', data),
  update: (id: number, data: any) => request.put(`/department/${id}`, data),
  delete: (id: number) => request.delete(`/department/${id}`),
}

export const dashboardApi = {
  overview: () => request.get('/dashboard/overview'),
  trend: (params: any) => request.get('/dashboard/trend', { params }),
  ranking: (params: any) => request.get('/dashboard/ranking', { params }),
  detail: (params: any) => request.get('/dashboard/detail', { params }),
}

export const roleApi = {
  list: () => request.get('/role/list'),
  menus: (id: number) => request.get(`/role/${id}/menus`),
  updateMenus: (id: number, menuIds: number[]) => request.put(`/role/${id}/menus`, menuIds),
}

export const userApi = {
  list: (params: any) => request.get('/user/list', { params }),
  create: (data: any) => request.post('/user', data),
  update: (id: number, data: any) => request.put(`/user/${id}`, data),
  delete: (id: number) => request.delete(`/user/${id}`),
  assignRoles: (id: number, roleIds: number[]) => request.put(`/user/${id}/roles`, roleIds),
  roles: () => request.get('/user/roles'),
  // 批量导入
  batchImport: (data: any[]) => request.post('/user/batch', data),
  downloadTemplate: () => request.get('/user/template', { responseType: 'blob' }),
  // 更新用户状态
  updateStatus: (id: number, status: number) => request.put(`/user/${id}/status`, null, { params: { status } }),
}
