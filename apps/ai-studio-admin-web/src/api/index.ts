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

export interface MailConfig {
  senderEmail: string
  authCode: string
  hasAuthCode: boolean
  updatedAt?: string
}

export interface MailConfigPayload {
  senderEmail: string
  authCode: string
}

function mapMailConfigResponse(item: any): MailConfig {
  return {
    senderEmail: item.sender_email ?? item.senderEmail ?? '',
    authCode: '',
    hasAuthCode: item.has_auth_code ?? item.hasAuthCode ?? false,
    updatedAt: item.updated_at ?? item.updatedAt ?? '',
  }
}

function mapMailConfigPayload(data: MailConfigPayload) {
  return {
    sender_email: data.senderEmail,
    auth_code: data.authCode,
  }
}

export const mailConfigApi = {
  get: async () => {
    const res = await request.get('/system/mail-config') as any
    return mapMailConfigResponse(res.data)
  },
  update: async (data: MailConfigPayload) => {
    const res = await request.post('/system/mail-config', mapMailConfigPayload(data)) as any
    return mapMailConfigResponse(res.data)
  },
}

export interface GitlabRuntimeConfig {
  baseUrl: string
  privateToken: string
  webhookToken: string
  hasPrivateToken: boolean
  hasWebhookToken: boolean
  schedulesEnabled: boolean
  dailyAnalyzeCron: string
  dailyReportCron: string
  backfillCron: string
  enabled: number
}

export interface GitlabRuntimeConfigPayload {
  baseUrl: string
  privateToken: string
  webhookToken: string
  schedulesEnabled: boolean
  dailyAnalyzeCron: string
  dailyReportCron: string
  backfillCron: string
  enabled: number
}

function mapGitlabRuntimeConfigResponse(item: any): GitlabRuntimeConfig {
  return {
    baseUrl: item.base_url ?? item.baseUrl ?? '',
    privateToken: '',
    webhookToken: '',
    hasPrivateToken: item.has_private_token ?? item.hasPrivateToken ?? false,
    hasWebhookToken: item.has_webhook_token ?? item.hasWebhookToken ?? false,
    schedulesEnabled: item.schedules_enabled ?? item.schedulesEnabled ?? false,
    dailyAnalyzeCron: item.daily_analyze_cron ?? item.dailyAnalyzeCron ?? '',
    dailyReportCron: item.daily_report_cron ?? item.dailyReportCron ?? '',
    backfillCron: item.backfill_cron ?? item.backfillCron ?? '',
    enabled: item.enabled ?? 1,
  }
}

function mapGitlabRuntimeConfigPayload(data: GitlabRuntimeConfigPayload) {
  return {
    base_url: data.baseUrl,
    private_token: data.privateToken,
    webhook_token: data.webhookToken,
    schedules_enabled: data.schedulesEnabled,
    daily_analyze_cron: data.dailyAnalyzeCron,
    daily_report_cron: data.dailyReportCron,
    backfill_cron: data.backfillCron,
    enabled: data.enabled,
  }
}

export const gitlabRuntimeConfigApi = {
  get: async () => {
    const res = await request.get('/system/gitlab-runtime-config') as any
    return mapGitlabRuntimeConfigResponse(res.data)
  },
  update: async (data: GitlabRuntimeConfigPayload) => {
    const res = await request.post('/system/gitlab-runtime-config', mapGitlabRuntimeConfigPayload(data)) as any
    return mapGitlabRuntimeConfigResponse(res.data)
  },
}

export interface AiModelConfig {
  providerName: string
  baseUrl: string
  modelName: string
  apiKey: string
  hasApiKey: boolean
  promptVersion: string
  requestTimeoutMs: number
  maxRetries: number
  enabled: number
}

export interface AiModelConfigPayload {
  providerName: string
  baseUrl: string
  modelName: string
  apiKey: string
  promptVersion: string
  requestTimeoutMs: number
  maxRetries: number
  enabled: number
}

function mapAiModelConfigResponse(item: any): AiModelConfig {
  return {
    providerName: item.provider_name ?? item.providerName ?? '',
    baseUrl: item.base_url ?? item.baseUrl ?? '',
    modelName: item.model_name ?? item.modelName ?? '',
    apiKey: '',
    hasApiKey: item.has_api_key ?? item.hasApiKey ?? false,
    promptVersion: item.prompt_version ?? item.promptVersion ?? '',
    requestTimeoutMs: item.request_timeout_ms ?? item.requestTimeoutMs ?? 30000,
    maxRetries: item.max_retries ?? item.maxRetries ?? 2,
    enabled: item.enabled ?? 0,
  }
}

function mapAiModelConfigPayload(data: AiModelConfigPayload) {
  return {
    provider_name: data.providerName,
    base_url: data.baseUrl,
    model_name: data.modelName,
    api_key: data.apiKey,
    prompt_version: data.promptVersion,
    request_timeout_ms: data.requestTimeoutMs,
    max_retries: data.maxRetries,
    enabled: data.enabled,
  }
}

export const aiModelConfigApi = {
  get: async () => {
    const res = await request.get('/system/ai-model-config') as any
    return mapAiModelConfigResponse(res.data)
  },
  update: async (data: AiModelConfigPayload) => {
    const res = await request.post('/system/ai-model-config', mapAiModelConfigPayload(data)) as any
    return mapAiModelConfigResponse(res.data)
  },
}

export interface GitlabActivityTaskItem {
  id: number
  taskType: string
  targetDate?: string
  userId?: number
  projectId?: number
  eventLogId?: number
  targetRef?: string
  status: string
  attemptCount: number
  nextRunAt?: string
  errorMessage?: string
  payloadJson?: string
  createdAt?: string
  updatedAt?: string
}

export interface GitlabEventLogItem {
  id: number
  eventUid: string
  eventType: string
  projectId?: number
  gitlabProjectId?: number
  gitlabUserId?: number
  refName?: string
  eventTime?: string
  payloadJson?: string
  processStatus?: string
  processMessage?: string
  relatedCommitRefs: string[]
  relatedMrRefs: number[]
  relatedWorkItemSummaries: string[]
  relatedReportTitles: string[]
  createdAt?: string
  updatedAt?: string
}

function mapGitlabTaskResponse(item: any): GitlabActivityTaskItem {
  return {
    id: item.id,
    taskType: item.task_type ?? item.taskType ?? '',
    targetDate: item.target_date ?? item.targetDate ?? '',
    userId: item.user_id ?? item.userId,
    projectId: item.project_id ?? item.projectId,
    eventLogId: item.event_log_id ?? item.eventLogId,
    targetRef: item.target_ref ?? item.targetRef ?? '',
    status: item.status ?? '',
    attemptCount: item.attempt_count ?? item.attemptCount ?? 0,
    nextRunAt: item.next_run_at ?? item.nextRunAt ?? '',
    errorMessage: item.error_message ?? item.errorMessage ?? '',
    payloadJson: item.payload_json ?? item.payloadJson ?? '',
    createdAt: item.created_at ?? item.createdAt ?? '',
    updatedAt: item.updated_at ?? item.updatedAt ?? '',
  }
}

function mapGitlabEventLogResponse(item: any): GitlabEventLogItem {
  return {
    id: item.id,
    eventUid: item.event_uid ?? item.eventUid ?? '',
    eventType: item.event_type ?? item.eventType ?? '',
    projectId: item.project_id ?? item.projectId,
    gitlabProjectId: item.gitlab_project_id ?? item.gitlabProjectId,
    gitlabUserId: item.gitlab_user_id ?? item.gitlabUserId,
    refName: item.ref_name ?? item.refName ?? '',
    eventTime: item.event_time ?? item.eventTime ?? '',
    payloadJson: item.payload_json ?? item.payloadJson ?? '',
    processStatus: item.process_status ?? item.processStatus ?? '',
    processMessage: item.process_message ?? item.processMessage ?? '',
    relatedCommitRefs: item.related_commit_refs ?? item.relatedCommitRefs ?? [],
    relatedMrRefs: item.related_mr_refs ?? item.relatedMrRefs ?? [],
    relatedWorkItemSummaries: item.related_work_item_summaries ?? item.relatedWorkItemSummaries ?? [],
    relatedReportTitles: item.related_report_titles ?? item.relatedReportTitles ?? [],
    createdAt: item.created_at ?? item.createdAt ?? '',
    updatedAt: item.updated_at ?? item.updatedAt ?? '',
  }
}

function mapPageResult<T>(payload: any, mapper: (item: any) => T) {
  return {
    total: payload.total ?? 0,
    records: Array.isArray(payload.records) ? payload.records.map(mapper) : [],
  }
}

export const gitlabActivityAdminApi = {
  listTasks: async (params: { page: number; size: number; status?: string }) => {
    const res = await request.get('/gitlab-activity/tasks/list', { params }) as any
    return mapPageResult(res.data, mapGitlabTaskResponse)
  },
  getTask: async (taskId: number) => {
    const res = await request.get(`/gitlab-activity/tasks/${taskId}`) as any
    return mapGitlabTaskResponse(res.data)
  },
  retryTask: (taskId: number) => request.post(`/gitlab-activity/tasks/${taskId}/retry`),
  runTaskNow: (taskId: number) => request.post(`/gitlab-activity/tasks/${taskId}/run-now`),
  listEventLogs: async (params: { page: number; size: number; processStatus?: string; gitlabProjectId?: number }) => {
    const res = await request.get('/gitlab-activity/event-logs/list', { params }) as any
    return mapPageResult(res.data, mapGitlabEventLogResponse)
  },
  getEventLog: async (eventLogId: number) => {
    const res = await request.get(`/gitlab-activity/event-logs/${eventLogId}`) as any
    return mapGitlabEventLogResponse(res.data)
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
  users: (id: number) => request.get(`/role/${id}/users`),
  updateUsers: (id: number, userIds: number[]) => request.post(`/role/${id}/users`, userIds),
}

export interface AdminMenu {
  id: number
  parentId: number
  name: string
  path: string
  component: string
  icon: string
  permission: string
  sort: number
  hidden: number
  createdAt?: string
  children?: AdminMenu[]
}

export interface AdminMenuPayload {
  parentId: number
  name: string
  path?: string
  component?: string
  icon?: string
  permission?: string
  sort?: number
  hidden?: number
}

function mapMenuResponse(item: any): AdminMenu {
  return {
    id: item.id,
    parentId: item.parent_id ?? item.parentId ?? 0,
    name: item.name ?? '',
    path: item.path ?? '',
    component: item.component ?? '',
    icon: item.icon ?? '',
    permission: item.permission ?? '',
    sort: item.sort ?? 0,
    hidden: item.hidden ?? 0,
    createdAt: item.created_at ?? item.createdAt,
    children: Array.isArray(item.children) ? item.children.map(mapMenuResponse) : undefined,
  }
}

function mapMenuPayload(data: AdminMenuPayload) {
  return {
    parent_id: data.parentId,
    name: data.name,
    path: data.path || '',
    component: data.component || '',
    icon: data.icon || '',
    permission: data.permission || '',
    sort: data.sort ?? 0,
    hidden: data.hidden ?? 0,
  }
}

export const menuApi = {
  list: async () => {
    const res = await request.get('/menu/list') as any
    return {
      ...res,
      data: (res.data || []).map(mapMenuResponse),
    }
  },
  detail: async (id: number) => {
    const res = await request.get(`/menu/${id}`) as any
    return {
      ...res,
      data: mapMenuResponse(res.data),
    }
  },
  create: (data: AdminMenuPayload) => request.post('/menu', mapMenuPayload(data)),
  update: (id: number, data: AdminMenuPayload) => request.post(`/menu/update/${id}`, mapMenuPayload(data)),
  delete: (id: number) => request.post(`/menu/${id}/delete`),
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

export const announcementApi = {
  list: (params: any) => request.get('/announcement/list', { params }),
  create: (data: any) => request.post('/announcement', data),
  update: (id: number, data: any) => request.post(`/announcement/${id}`, data),
  publish: (id: number) => request.post(`/announcement/${id}/publish`),
  offline: (id: number) => request.post(`/announcement/${id}/offline`),
  delete: (id: number) => request.post(`/announcement/${id}/delete`),
}

export const notificationApi = {
  list: (params: any) => request.get('/notification/list', { params }),
}

export const notificationRuleApi = {
  groups: () => request.get('/notification-rule/groups'),
  createGroup: (data: any) => request.post('/notification-rule/groups', data),
  updateGroup: (id: number, data: any) => request.post(`/notification-rule/groups/${id}`, data),
  deleteGroup: (id: number) => request.post(`/notification-rule/groups/${id}/delete`),
  rules: (id: number) => request.get(`/notification-rule/groups/${id}/rules`),
  updateRules: (id: number, data: any[]) => request.post(`/notification-rule/groups/${id}/rules`, data),
  users: (id: number) => request.get(`/notification-rule/groups/${id}/users`),
  bindUsers: (id: number, userIds: number[]) => request.post(`/notification-rule/groups/${id}/users`, { userIds }),
}

export interface EmailReportRule {
  id?: number
  name: string
  toRecipientUserIds: number[]
  ccRecipientUserIds: number[]
  recipients: string[]
  ccRecipients: string[]
  userIds: number[]
  sendTime: string
  status: number
  lastSentAt?: string
  createdAt?: string
}

export interface EmailReportRulePayload {
  name: string
  toRecipientUserIds: number[]
  ccRecipientUserIds: number[]
  recipients?: string[]
  userIds: number[]
  sendTime: string
  status: number
}

export interface EmailReportSendLog {
  id: number
  ruleId: number
  ruleName: string
  reportDate: string
  triggerType: 'MANUAL' | 'SCHEDULED'
  status: 'SUCCESS' | 'FAILED'
  toRecipients: string[]
  ccRecipients: string[]
  subject?: string
  errorMessage?: string
  sentAt?: string
}

function mapEmailReportRuleResponse(item: any): EmailReportRule {
  return {
    id: item.id,
    name: item.name ?? '',
    toRecipientUserIds: item.to_recipient_user_ids ?? item.toRecipientUserIds ?? item.recipient_user_ids ?? item.recipientUserIds ?? [],
    ccRecipientUserIds: item.cc_recipient_user_ids ?? item.ccRecipientUserIds ?? [],
    recipients: item.recipients ?? [],
    ccRecipients: item.cc_recipients ?? item.ccRecipients ?? [],
    userIds: item.user_ids ?? item.userIds ?? [],
    sendTime: item.send_time ?? item.sendTime ?? '18:00',
    status: item.status ?? 1,
    lastSentAt: item.last_sent_at ?? item.lastSentAt,
    createdAt: item.created_at ?? item.createdAt,
  }
}

function mapEmailReportRulePayload(data: EmailReportRulePayload) {
  return {
    name: data.name,
    to_recipient_user_ids: data.toRecipientUserIds || [],
    cc_recipient_user_ids: data.ccRecipientUserIds || [],
    recipients: data.recipients || [],
    user_ids: data.userIds || [],
    send_time: data.sendTime,
    status: data.status,
  }
}

function mapEmailReportSendLogResponse(item: any): EmailReportSendLog {
  return {
    id: item.id,
    ruleId: item.rule_id ?? item.ruleId,
    ruleName: item.rule_name ?? item.ruleName ?? '',
    reportDate: item.report_date ?? item.reportDate ?? '',
    triggerType: item.trigger_type ?? item.triggerType ?? 'MANUAL',
    status: item.status ?? 'FAILED',
    toRecipients: item.to_recipients ?? item.toRecipients ?? [],
    ccRecipients: item.cc_recipients ?? item.ccRecipients ?? [],
    subject: item.subject,
    errorMessage: item.error_message ?? item.errorMessage,
    sentAt: item.sent_at ?? item.sentAt,
  }
}

export const emailReportApi = {
  rules: async () => {
    const res = await request.get('/email-report/rules') as any
    return {
      ...res,
      data: (res.data || []).map(mapEmailReportRuleResponse),
    }
  },
  createRule: (data: EmailReportRulePayload) => request.post('/email-report/rules', mapEmailReportRulePayload(data)),
  updateRule: (id: number, data: EmailReportRulePayload) => request.post(`/email-report/rules/${id}`, mapEmailReportRulePayload(data)),
  deleteRule: (id: number) => request.post(`/email-report/rules/${id}/delete`),
  logs: async (id: number) => {
    const res = await request.get(`/email-report/rules/${id}/logs`) as any
    return {
      ...res,
      data: (res.data || []).map(mapEmailReportSendLogResponse),
    }
  },
  preview: (id: number, date?: string) => request.get(`/email-report/rules/${id}/preview`, { params: { date } }),
  send: (id: number, date?: string) => request.post(`/email-report/rules/${id}/send`, null, { params: { date }, silentError: true } as any),
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
  list: (params?: { keyword?: string; status?: number; page?: number; size?: number }) => request.get('/article/list', { params }),
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
