import axios from 'axios'
import request from '@/utils/request'

export interface SiteFooterLink {
  name: string
  url: string
}

export interface SiteConfig {
  siteName: string
  siteDescription: string
  logoUrl: string
  iconUrl: string
  footerText: string
  footerCopyright: string
  footerRecord: string
  footerLinks: SiteFooterLink[]
  contacts: string
}

function parseFooterLinks(value?: string): SiteFooterLink[] {
  if (!value) return []
  try {
    const parsed = JSON.parse(value)
    return Array.isArray(parsed) ? parsed : []
  } catch {
    return []
  }
}

function mapSiteConfigResponse(item: any): SiteConfig {
  return {
    siteName: item.site_name ?? item.siteName ?? 'AI Studio',
    siteDescription: item.site_description ?? item.siteDescription ?? '',
    logoUrl: item.logo_url ?? item.logoUrl ?? '/ai-studio-logo.svg',
    iconUrl: item.icon_url ?? item.iconUrl ?? '/favicon.png',
    footerText: item.footer_text ?? item.footerText ?? '',
    footerCopyright: item.footer_copyright ?? item.footerCopyright ?? '',
    footerRecord: item.footer_record ?? item.footerRecord ?? '',
    footerLinks: parseFooterLinks(item.footer_links ?? item.footerLinks),
    contacts: item.contacts ?? '',
  }
}

function mapSiteConfigPayload(data: SiteConfig) {
  return {
    site_name: data.siteName,
    site_description: data.siteDescription,
    logo_url: data.logoUrl,
    icon_url: data.iconUrl,
    footer_text: data.footerText,
    footer_copyright: data.footerCopyright,
    footer_record: data.footerRecord,
    footer_links: JSON.stringify(data.footerLinks || []),
    contacts: data.contacts,
  }
}

export const siteConfigApi = {
  get: async () => {
    const res = await request.get('/site/config') as any
    return mapSiteConfigResponse(res.data)
  },
  update: async (data: SiteConfig) => {
    const res = await request.post('/site/config', mapSiteConfigPayload(data)) as any
    return mapSiteConfigResponse(res.data)
  },
}

interface KnowledgeApiResponse<T> {
  success: boolean
  data?: T
  error?: string
  message?: string
}

export interface KnowledgeDocument {
  id: string
  fileName: string
  filePath: string
  fileType: string
  fileSize: number
  status: string
  chunkCount: number
  indexedAt?: string
  errorMessage?: string
}

export interface KnowledgeStats {
  kbId: string
  kbName: string
  status: string
  fileCount: number
  chunkCount: number
  vectorCount: number
  vectorDimension: number
  queueStatus?: {
    queueSize: number
    running: number
    concurrency: number
  }
}

const knowledgeRequest = axios.create({
  baseURL: import.meta.env.VITE_KNOWLEDGE_API_BASE || '/knowledge-api',
  timeout: 60000,
})

async function knowledgeApiRequest<T>(url: string, options: Record<string, any> = {}) {
  const response = await knowledgeRequest.request<KnowledgeApiResponse<T>>({
    url,
    ...options,
  })
  const payload = response.data
  if (!payload.success) {
    throw new Error(payload.error || payload.message || '知识库服务请求失败')
  }
  return payload.data as T
}

export const knowledgeAdminApi = {
  health: async () => {
    const response = await knowledgeRequest.get<{ status: string; timestamp: string; uptime: number }>('/health')
    return response.data
  },
  stats: () => knowledgeApiRequest<KnowledgeStats>('/stats'),
  documents: () => knowledgeApiRequest<KnowledgeDocument[]>('/documents'),
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return knowledgeApiRequest<{ id: string; filename: string; size: number; status: string }>('/upload', {
      method: 'POST',
      data: formData,
    })
  },
  remove: (id: string) => knowledgeApiRequest<{ message?: string }>(`/documents/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  }),
  reindex: (id: string) => knowledgeApiRequest<{ message?: string }>(`/documents/${encodeURIComponent(id)}/reindex`, {
    method: 'POST',
  }),
}

export const skillApi = {
  // 查询
  list: (params: any) => request.get('/skill/list', { params }),
  detail: (id: number) => request.get(`/skill/${id}`),
  versions: (id: number) => request.get(`/skill/${id}/versions`),

  // 审核
  audit: (id: number, data: { status: number; reviewComment?: string }) => request.post(`/skill/${id}/audit`, data),

  // 版本管理
  publishVersion: (id: number, data: any) => request.post(`/skill/${id}/versions`, data),
  deleteVersion: (id: number, version: string) => request.post(`/skill/${id}/versions/${version}/delete`),

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
  detail: (id: number) => request.get(`/mcp/${id}`),
  // 审核
  audit: (id: number, data: { status: number; reviewComment?: string }) => request.post(`/mcp/${id}/audit`, data),
  // CRUD
  create: (data: any) => request.post('/mcp', data),
  update: (id: number, data: any) => request.post(`/mcp/${id}`, data),
  delete: (id: number) => request.post(`/mcp/${id}/delete`),
  test: (id: number) => request.post(`/mcp/${id}/test`),
}

export const pluginApi = {
  list: (params: any) => request.get('/plugin/list', { params }),
  detail: (id: number) => request.get(`/plugin/${id}`),
  // 审核
  audit: (id: number, data: { status: number; reviewComment?: string }) => request.post(`/plugin/${id}/audit`, data),
  // 下载
  download: (id: number) => request.get(`/plugin/${id}/download`),
  // 版本管理
  versions: (id: number) => request.get(`/plugin/${id}/versions`),
}

export const tutorialApi = {
  list: (params: any) => request.get('/tutorial/list', { params }),
  detail: (id: number) => request.get(`/tutorial/${id}`),
  // 审核
  audit: (id: number, data: { status: number; reviewComment?: string }) => request.post(`/tutorial/${id}/audit`, data),
}

export const outputApi = {
  // 开放 API（无需认证，用于提交产出数据）
  openSubmit: (data: any) => {
    return fetch('/ai-studio/v1/open/output/submit', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data)
    })
  },
  // Admin 端产出管理
  today: () => request.get('/output/today'),
  history: (params: any) => request.get('/output/history', { params }),
  adminList: (params: any) => request.get('/output/list', { params }),
  adminByUsers: (params: any) => request.get('/output/by-users', { params }),
  stats: (params: any) => request.get('/output/stats', { params }),
  dashboardDetails: (params: any) => request.get('/output/dashboard-details', { params }),
  byDepartment: (params: any) => request.get('/output/by-department', { params }),
  byProject: (params: any) => request.get('/output/by-project', { params }),
  projectMembers: (params: any) => request.get('/output/project-members', { params }),
  // 导出
  export: (params: any) => request.get('/output/export', { params, responseType: 'blob' }),
}

export interface AdminProject {
  id: number
  projectName: string
  description?: string
  ownerId: number
  ownerName?: string
  deptId?: number
  deptName?: string
  teamId?: number
  teamName?: string
  ownerDeptId?: number
  ownerDeptName?: string
  status: 'ACTIVE' | 'ENDED'
  startedAt?: string
  endedAt?: string
  createdAt?: string
  updatedAt?: string
}

export interface AdminProjectPayload {
  projectName: string
  description?: string
  ownerId: number
  deptId: number
  teamId: number
  status?: 'ACTIVE' | 'ENDED'
  startedAt?: string
  endedAt?: string
}

function mapProjectResponse(item: any): AdminProject {
  return {
    id: item.id,
    projectName: item.project_name ?? item.projectName,
    description: item.description,
    ownerId: item.owner_id ?? item.ownerId,
    ownerName: item.owner_name ?? item.ownerName,
    deptId: item.dept_id ?? item.deptId,
    deptName: item.dept_name ?? item.deptName,
    teamId: item.team_id ?? item.teamId,
    teamName: item.team_name ?? item.teamName,
    ownerDeptId: item.owner_dept_id ?? item.ownerDeptId,
    ownerDeptName: item.owner_dept_name ?? item.ownerDeptName,
    status: item.status,
    startedAt: item.started_at ?? item.startedAt,
    endedAt: item.ended_at ?? item.endedAt,
    createdAt: item.created_at ?? item.createdAt,
    updatedAt: item.updated_at ?? item.updatedAt,
  }
}

function mapProjectPayload(data: Partial<AdminProjectPayload>) {
  return {
    project_name: data.projectName,
    description: data.description,
    owner_id: data.ownerId,
    dept_id: data.deptId,
    team_id: data.teamId,
    status: data.status,
    started_at: data.startedAt,
    ended_at: data.endedAt,
  }
}

export const projectApi = {
  list: async (params: any) => {
    const res = await request.get('/project/list', { params }) as any
    const data = res.data || {}
    return {
      ...res,
      data: {
        ...data,
        records: (data.records || []).map(mapProjectResponse),
      },
    }
  },
  detail: async (id: number) => {
    const res = await request.get(`/project/${id}`) as any
    return { ...res, data: mapProjectResponse(res.data) }
  },
  create: (data: AdminProjectPayload) => request.post('/project', mapProjectPayload(data)),
  update: (id: number, data: Partial<AdminProjectPayload>) => request.post(`/project/update/${id}`, mapProjectPayload(data)),
  delete: (id: number) => request.post(`/project/delete/${id}`),
  updateStatus: (id: number, status: 'ACTIVE' | 'ENDED') => request.post(`/project/${id}/status`, { status }),
}

export const departmentApi = {
  list: () => request.get('/department/list'),
  active: () => request.get('/department/active'),
  create: (data: any) => request.post('/department', data),
  update: (id: number, data: any) => request.post(`/department/${id}`, data),
  delete: (id: number) => request.post(`/department/${id}/delete`),
}

export const dashboardApi = {
  overview: () => request.get('/dashboard/overview'),
  trend: (params: any) => request.get('/dashboard/trend', { params }),
  ranking: (params: any) => request.get('/dashboard/ranking', { params }),
  detail: (params: any) => request.get('/dashboard/detail', { params }),
  portalOverview: () => request.get('/dashboard/portal-overview'),
  portalTrend: (params: any) => request.get('/dashboard/portal-trend', { params }),
  portalRanking: (params: any) => request.get('/dashboard/portal-ranking', { params }),
}

export const roleApi = {
  list: () => request.get('/role/list'),
  menus: (id: number) => request.get(`/role/${id}/menus`),
  updateMenus: (id: number, menuIds: number[]) => request.post(`/role/${id}/menus`, menuIds),
}

export const userApi = {
  list: (params: any) => request.get('/user/list', { params }),
  create: (data: any) => request.post('/user', data),
  update: (id: number, data: any) => request.post(`/user/update/${id}`, data),
  delete: (id: number) => request.post(`/user/delete/${id}`),
  assignRoles: (id: number, roleIds: number[]) => request.post(`/user/${id}/roles`, roleIds),
  roles: () => request.get('/user/roles'),
  // 批量导入
  batchImport: (data: any[]) => request.post('/user/batch', data),
  // 更新用户状态
  updateStatus: (id: number, status: number) => request.post(`/user/${id}/status`, null, { params: { status } }),
}

export const authApi = {
  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }) =>
    request.post('/auth/change-password', data),
}

export const teamApi = {
  // 团队列表
  list: (params?: { deptId?: number }) => request.get('/team/list', { params }),
  // 创建团队
  create: (data: { team_name: string; dept_id?: number; description?: string }) => request.post('/team', data),
  // 更新团队
  update: (id: number, data: { team_name?: string; description?: string; status?: number }) => request.post(`/team/${id}`, data),
  // 删除团队
  delete: (id: number) => request.post(`/team/${id}/delete`),
  // 获取团队成员
  members: (teamId: number) => request.get(`/team/${teamId}/members`),
  // 添加团队成员
  addMembers: (teamId: number, userIds: number[]) => request.post(`/team/${teamId}/members`, userIds),
  // 移除团队成员
  removeMember: (teamId: number, userId: number) => request.post(`/team/${teamId}/members/${userId}/delete`),
}

// 社区管理 - 文章
export const adminArticleApi = {
  list: () => request.get('/article/list'),
  detail: (id: number) => request.get(`/article/${id}`),
  takedown: (id: number, reason: string) => request.post(`/article/${id}/takedown`, { reason }),
}

// 社区管理 - 问题
export const adminQuestionApi = {
  list: (params: { keyword?: string; takenDown?: number; page?: number; size?: number }) => request.get('/question/list', { params }),
  detail: (id: number) => request.get(`/question/${id}`),
  takedown: (id: number, reason: string) => request.post(`/question/${id}/takedown`, { reason }),
  restore: (id: number) => request.post(`/question/${id}/restore`),
}

// 社区管理 - 回答
export const adminAnswerApi = {
  list: (params: { keyword?: string; questionId?: number; isBest?: number; page?: number; size?: number }) => request.get('/answer/list', { params }),
  detail: (id: number) => request.get(`/answer/${id}`),
  takedown: (id: number, reason: string) => request.post(`/answer/${id}/takedown`, { reason }),
  restore: (id: number) => request.post(`/answer/${id}/restore`),
}

// 社区管理 - 评论
export const adminCommentApi = {
  list: (params: { targetType?: string; targetId?: number; keyword?: string; page?: number; size?: number }) => request.get('/comment/list', { params }),
  detail: (id: number) => request.get(`/comment/${id}`),
  delete: (id: number, reason: string) => request.post(`/comment/${id}/delete`, { reason }),
}
