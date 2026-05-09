import request from '@/utils/request'

export interface ConsoleProject {
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

export interface ConsoleProjectPayload {
  projectName: string
  description?: string
  ownerId: number
  deptId?: number
  teamId?: number
  status?: 'ACTIVE' | 'ENDED'
  startedAt?: string
  endedAt?: string
}

function mapProjectResponse(item: any): ConsoleProject {
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

function mapProjectPayload(data: Partial<ConsoleProjectPayload>) {
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
  create: (data: ConsoleProjectPayload) => request.post('/project', mapProjectPayload(data)),
  update: (id: number, data: Partial<ConsoleProjectPayload>) => request.post(`/project/update/${id}`, mapProjectPayload(data)),
  delete: (id: number) => request.post(`/project/delete/${id}`),
  updateStatus: (id: number, status: 'ACTIVE' | 'ENDED') => request.post(`/project/${id}/status`, { status }),
}

export const departmentApi = {
  active: () => request.get('/department/active'),
}

export const teamApi = {
  list: (params?: { deptId?: number }) => request.get('/team/list', { params }),
}

export const userApi = {
  list: (params: any) => request.get('/user/list', { params }),
}
