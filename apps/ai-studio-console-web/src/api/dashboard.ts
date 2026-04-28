import request from '@/utils/request'

const normalizeTask = (task: any) => ({
  ...task,
  projectId: task.projectId ?? task.project_id,
  projectName: task.projectName ?? task.project_name,
  taskDate: task.taskDate ?? task.task_date,
  createdAt: task.createdAt ?? task.created_at,
})

export const dashboardApi = {
  overview: () =>
    request.get('/dashboard/overview').then((res: any) => ({
      ...res,
      data: {
        ...res?.data,
        skillCount: res?.data?.skillCount ?? res?.data?.skill_count ?? 0,
        skillMonthCount: res?.data?.skillMonthCount ?? res?.data?.skill_month_count ?? 0,
        taskCount: res?.data?.taskCount ?? res?.data?.task_count ?? 0,
        todayTaskHours: res?.data?.todayTaskHours ?? res?.data?.today_task_hours ?? 0,
        projectCount: res?.data?.projectCount ?? res?.data?.project_count ?? 0,
        monthTaskHours: res?.data?.monthTaskHours ?? res?.data?.month_task_hours ?? 0,
        recentTasks: (res?.data?.recentTasks ?? res?.data?.recent_tasks ?? []).map(normalizeTask),
      },
    })),
}
