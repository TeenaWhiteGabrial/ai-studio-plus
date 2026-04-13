import request from '@/utils/request'

export const skillApi = {
  // 查询
  list: (params: any) => request.get('/skill/list', { params }),
  detail: (id: number) => request.get(`/skill/${id}`),
  versions: (id: number) => request.get(`/skill/${id}/versions`),

  // 管理
  create: (data: any) => request.post('/skill', data),
  update: (id: number, data: any) => request.put(`/skill/${id}`, data),
  delete: (id: number) => request.delete(`/skill/${id}`),

  // 版本管理
  publishVersion: (id: number, data: any) => request.post(`/skill/${id}/versions`, data),
  deleteVersion: (id: number, version: string) => request.delete(`/skill/${id}/versions/${version}`),

  // 下载（返回 302 重定向）
  download: (id: number, version?: string) => request.get(`/skill/${id}/download`, {
    params: { version },
    maxRedirects: 0
  }),
  getDownloadUrl: (id: number, version?: string) => {
    const params = version ? `?version=${version}` : ''
    // 通过 Vite proxy 代理到后端，baseURL /ai-studio/v1/admin 会自动添加
    return `/skill/${id}/download${params}`
  },

  // 上传文件到 OSS（后端代理上传）
  upload: (file: File, skillName: string, version: string) => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('skillName', skillName)
    formData.append('version', version)
    return request.post('/skill/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 以下方法已废弃（保留用于兼容性）
  /** @deprecated */
  raw: (id: number) => request.get(`/skill/${id}/raw`, { responseType: 'text', transformResponse: [(data) => data] }),
  /** @deprecated */
  sync: () => request.post('/skill/sync'),
  /** @deprecated */
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
  // 开放 API（无需认证，通过 window.location 跳转或直接 fetch 调用）
  openSubmit: (data: any) => {
    return fetch('/ai-studio/v1/open/output/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
  },
  openToday: (username: string) => {
    return fetch(`/ai-studio/v1/open/output/today?username=${username}`)
      .then(res => res.json())
  },
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
