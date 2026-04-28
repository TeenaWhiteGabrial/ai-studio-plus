import request from '@/utils/request'

export interface DailyTaskPayload {
  projectId?: number | null
  taskDate: string
  content: string
  hours: number
  aiParticipation: number
  outputId?: number | null
  status?: string
}

const normalizeOutput = (output: any) => {
  if (!output) return output
  return {
    ...output,
    statDate: output.statDate ?? output.stat_date,
    projectRootName: output.projectRootName ?? output.project_root_name,
    totalCodeLines: output.totalCodeLines ?? output.total_code_lines,
  }
}

const normalizeProject = (project: any) => {
  if (!project) return project
  return {
    ...project,
    projectName: project.projectName ?? project.project_name,
    ownerId: project.ownerId ?? project.owner_id,
    ownerName: project.ownerName ?? project.owner_name,
    deptId: project.deptId ?? project.dept_id,
    deptName: project.deptName ?? project.dept_name,
    teamId: project.teamId ?? project.team_id,
    teamName: project.teamName ?? project.team_name,
    ownerDeptId: project.ownerDeptId ?? project.owner_dept_id,
    ownerDeptName: project.ownerDeptName ?? project.owner_dept_name,
    startedAt: project.startedAt ?? project.started_at,
    endedAt: project.endedAt ?? project.ended_at,
    createdAt: project.createdAt ?? project.created_at,
    updatedAt: project.updatedAt ?? project.updated_at,
  }
}

const normalizeTask = (task: any) => {
  if (!task) return task
  return {
    ...task,
    projectId: task.projectId ?? task.project_id,
    taskDate: task.taskDate ?? task.task_date,
    aiParticipation: task.aiParticipation ?? task.ai_participation,
    outputId: task.outputId ?? task.output_id,
    completedAt: task.completedAt ?? task.completed_at,
    createdAt: task.createdAt ?? task.created_at,
    updatedAt: task.updatedAt ?? task.updated_at,
    output: normalizeOutput(task.output),
  }
}

const normalizeResultData = (res: any, normalizer: (data: any) => any) => {
  if (!res || !('data' in res)) return normalizer(res)
  return {
    ...res,
    data: Array.isArray(res.data) ? res.data.map(normalizer) : normalizer(res.data),
  }
}

const toTaskRequest = (data: DailyTaskPayload) => ({
  project_id: data.projectId ?? null,
  task_date: data.taskDate,
  content: data.content,
  hours: data.hours,
  ai_participation: data.aiParticipation,
  output_id: data.outputId ?? null,
  status: data.status,
})

export const taskApi = {
  list: (params: { startDate?: string; endDate?: string; status?: string }) =>
    request.get('/task/list', { params }).then((res) => normalizeResultData(res, normalizeTask)),
  create: (data: DailyTaskPayload) =>
    request.post('/task', toTaskRequest(data)).then((res) => normalizeResultData(res, normalizeTask)),
  update: (id: number, data: DailyTaskPayload) =>
    request.put(`/task/${id}`, toTaskRequest(data)).then((res) => normalizeResultData(res, normalizeTask)),
  delete: (id: number) => request.delete(`/task/${id}`),
  stats: () =>
    request.get('/task/stats').then((res: any) => ({
      ...res,
      data: {
        ...res?.data,
        weekCompletedCount: res?.data?.weekCompletedCount ?? res?.data?.week_completed_count ?? 0,
        weekHours: res?.data?.weekHours ?? res?.data?.week_hours ?? 0,
        monthCompletedCount: res?.data?.monthCompletedCount ?? res?.data?.month_completed_count ?? 0,
        monthHours: res?.data?.monthHours ?? res?.data?.month_hours ?? 0,
      },
    })),
}

export const outputApi = {
  history: (params: { startDate: string; endDate: string }) =>
    request.get('/output/history', { params }).then((res) => normalizeResultData(res, normalizeOutput)),
}

export const consoleProjectApi = {
  active: () => request.get('/project/active').then((res) => normalizeResultData(res, normalizeProject)),
}
