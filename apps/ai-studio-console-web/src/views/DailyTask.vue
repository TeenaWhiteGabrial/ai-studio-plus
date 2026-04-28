<template>
  <div class="task-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>每日任务</span>
          <div class="header-actions">
            <el-button
              :type="showMetrics ? 'primary' : 'default'"
              :icon="DataAnalysis"
              :aria-pressed="showMetrics"
              @click="showMetrics = !showMetrics"
            >
              {{ showMetrics ? '收起统计' : '数据统计' }}
            </el-button>
            <div class="view-switch" role="tablist" aria-label="任务视图切换">
              <button
                v-for="item in viewOptions"
                :key="item.value"
                class="view-switch__item"
                :class="{ 'is-active': viewMode === item.value }"
                type="button"
                role="tab"
                :aria-selected="viewMode === item.value"
                @click="viewMode = item.value"
              >
                <el-icon>
                  <component :is="item.icon" />
                </el-icon>
                <span>{{ item.label }}</span>
              </button>
            </div>
            <el-button type="primary" :icon="Plus" @click="handleAdd">录入任务</el-button>
          </div>
        </div>
      </template>

      <div class="filters">
        <div class="filter-group">
          <span class="filter-label">时间范围</span>
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            :shortcuts="dateShortcuts"
          />
        </div>
        <div class="filter-group filter-status">
          <span class="filter-label">状态</span>
          <el-select v-model="statusFilter" clearable placeholder="全部状态">
            <el-option label="待处理" value="PENDING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </div>
        <el-button type="primary" :icon="Search" :loading="loading" @click="loadTasks">查询</el-button>
      </div>

      <section v-if="showMetrics" class="metrics-panel">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ stats.weekCompletedCount }}</div>
            <div class="stat-label">本周完成任务</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.weekHours }}h</div>
            <div class="stat-label">本周工时</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.monthCompletedCount }}</div>
            <div class="stat-label">本月完成任务</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ stats.monthHours }}h</div>
            <div class="stat-label">本月工时</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ taskHourRatio }}h</div>
            <div class="stat-label">任务工时比</div>
          </div>
        </div>
      </section>

      <el-table v-if="viewMode === 'list'" v-loading="loading" :data="tasks" class="task-table">
        <el-table-column prop="taskDate" label="日期" width="120" />
        <el-table-column label="所属项目" min-width="150" show-overflow-tooltip>
          <template #default="{ row }">{{ getProjectName(row.projectId) }}</template>
        </el-table-column>
        <el-table-column prop="content" label="任务内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="hours" label="工时" width="90">
          <template #default="{ row }">{{ row.hours }}h</template>
        </el-table-column>
        <el-table-column prop="aiParticipation" label="AI 辅助程度" width="140">
          <template #default="{ row }">{{ getAiAssistanceLabel(row.aiParticipation) }}</template>
        </el-table-column>
        <el-table-column label="关联产出" min-width="180">
          <template #default="{ row }">{{ formatOutput(row.output) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-else-if="viewMode === 'card'" v-loading="loading" class="task-grid">
        <el-empty v-if="tasks.length === 0" description="暂无任务" />
        <article v-for="task in tasks" :key="task.id" class="task-card">
          <div class="task-card__header">
            <span>{{ task.taskDate }}</span>
            <el-tag :type="getStatusType(task.status)">{{ getStatusText(task.status) }}</el-tag>
          </div>
          <p class="task-card__content">{{ task.content }}</p>
          <div class="task-meta">
            <span>{{ getProjectName(task.projectId) }}</span>
            <span>{{ task.hours }}h</span>
            <span>{{ getAiAssistanceLabel(task.aiParticipation) }}</span>
            <span>{{ formatOutput(task.output) }}</span>
          </div>
          <div class="task-card__actions">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(task)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(task)">删除</el-button>
          </div>
        </article>
      </div>

      <el-calendar v-else v-model="currentDate" v-loading="loading" class="task-calendar">
        <template #date-cell="{ data }">
          <div class="calendar-cell">
            <span class="date-num">{{ data.day.split('-').slice(2).join('') }}</span>
            <div class="calendar-task-list">
              <button
                v-for="task in getTasksByDate(data.day)"
                :key="task.id"
                class="calendar-task"
                type="button"
                @click="handleEdit(task)"
              >
                <span>{{ task.content }}</span>
                <b>{{ task.hours }}h</b>
              </button>
            </div>
          </div>
        </template>
      </el-calendar>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingTask ? '编辑任务' : '录入任务'" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="任务日期" prop="taskDate">
          <el-date-picker v-model="form.taskDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="所属项目" prop="projectId">
          <el-select v-model="form.projectId" filterable placeholder="请选择进行中的项目">
            <el-option
              v-for="project in projectOptions"
              :key="project.id"
              :label="project.projectName"
              :value="project.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="任务内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入任务内容" />
        </el-form-item>
        <el-form-item prop="aiParticipation">
          <template #label>
            <span class="field-label-with-help">
              AI 辅助程度
              <el-tooltip placement="top" trigger="hover" popper-class="ai-help-tooltip">
                <template #content>
                  <div class="ai-help-content">
                    <p v-for="item in aiOptions" :key="item.value">
                      <b>{{ item.label }}</b>：{{ item.description }}
                    </p>
                  </div>
                </template>
                <button class="help-icon" type="button" aria-label="AI 辅助程度说明">?</button>
              </el-tooltip>
            </span>
          </template>
          <el-select v-model="form.aiParticipation" placeholder="请选择 AI 辅助程度" class="ai-assistance-select">
            <el-option v-for="item in aiOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务工时" prop="hours">
          <el-input-number v-model="form.hours" :min="0" :max="24" :step="0.5" />
        </el-form-item>
        <el-form-item label="关联产出统计ID">
          <el-select v-model="form.outputId" clearable filterable placeholder="选择本人产出记录">
            <el-option
              v-for="output in outputOptions"
              :key="output.id"
              :label="formatOutputOption(output)"
              :value="output.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="待处理" value="PENDING" />
            <el-option label="已完成" value="COMPLETED" />
            <el-option label="已取消" value="CANCELLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitTask">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import dayjs from 'dayjs'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Calendar, DataAnalysis, Delete, Edit, Grid, List, Plus, Search } from '@element-plus/icons-vue'
import { consoleProjectApi, outputApi, taskApi, type DailyTaskPayload } from '@/api'

interface MemberOutput {
  id: number
  statDate: string
  projectRootName?: string
  totalCodeLines?: number
  remark?: string
}

interface DailyTask {
  id: number
  projectId?: number | null
  taskDate: string
  content: string
  hours: number
  aiParticipation: number
  outputId?: number | null
  output?: MemberOutput | null
  status: string
}

interface ProjectOption {
  id: number
  projectName: string
  status: string
}

const aiOptions = [
  { value: 0, label: '无 AI 参与', description: '完全人工完成。' },
  { value: 25, label: '轻度辅助', description: 'AI 只用于查资料、解释概念、生成少量代码片段。' },
  { value: 50, label: '中度辅助', description: 'AI 参与部分实现，例如生成函数、测试、重构建议。' },
  { value: 75, label: '高度辅助', description: 'AI 生成了主要代码，人负责修改、集成、验证。' },
  { value: 90, label: 'AI 主导', description: 'AI 完成大部分实现，人主要负责需求描述、审查、调试和验收。' },
]
const dateShortcuts = [
  {
    text: '本周',
    value: () => {
      const weekStart = dayjs().startOf('day').subtract((dayjs().day() + 6) % 7, 'day')
      return [weekStart.toDate(), weekStart.add(6, 'day').endOf('day').toDate()]
    },
  },
  {
    text: '本月',
    value: () => [
      dayjs().startOf('month').toDate(),
      dayjs().endOf('month').toDate(),
    ],
  },
]
const viewOptions = [
  { label: '卡片', value: 'card', icon: Grid },
  { label: '列表', value: 'list', icon: List },
  { label: '日历', value: 'calendar', icon: Calendar },
]

const currentDate = ref(new Date())
const viewMode = ref('card')
const showMetrics = ref(false)
const loading = ref(false)
const submitting = ref(false)
const dialogVisible = ref(false)
const editingTask = ref<DailyTask | null>(null)
const tasks = ref<DailyTask[]>([])
const monthlyTasks = ref<DailyTask[]>([])
const projectOptions = ref<ProjectOption[]>([])
const outputOptions = ref<MemberOutput[]>([])
const formRef = ref<FormInstance>()
const dateRange = ref<[string, string]>([
  dayjs().startOf('month').format('YYYY-MM-DD'),
  dayjs().endOf('month').format('YYYY-MM-DD'),
])
const statusFilter = ref('')

const stats = reactive({
  weekCompletedCount: 0,
  weekHours: 0,
  monthCompletedCount: 0,
  monthHours: 0,
})

const form = reactive<DailyTaskPayload>({
  projectId: null,
  taskDate: dayjs().format('YYYY-MM-DD'),
  content: '',
  hours: 0,
  aiParticipation: 0,
  outputId: null,
  status: 'PENDING',
})

const rules: FormRules = {
  projectId: [{ required: true, message: '请选择所属项目', trigger: 'change' }],
  taskDate: [{ required: true, message: '请选择任务日期', trigger: 'change' }],
  content: [{ required: true, message: '请输入任务内容', trigger: 'blur' }],
  hours: [{ required: true, message: '请填写任务工时', trigger: 'change' }],
  aiParticipation: [{ required: true, message: '请选择 AI 参与度', trigger: 'change' }],
}

const taskMap = computed(() => {
  return tasks.value.reduce<Record<string, DailyTask[]>>((map, task) => {
    if (!map[task.taskDate]) {
      map[task.taskDate] = []
    }
    map[task.taskDate].push(task)
    return map
  }, {})
})

const projectNameMap = computed(() => {
  return projectOptions.value.reduce<Record<number, string>>((map, project) => {
    map[project.id] = project.projectName
    return map
  }, {})
})

const totalTaskHours = computed(() => {
  const total = monthlyTasks.value.reduce((sum, task) => sum + Number(task.hours || 0), 0)
  return Number(total.toFixed(1))
})

const taskHourRatio = computed(() => {
  if (monthlyTasks.value.length === 0) return 0
  return Number((totalTaskHours.value / monthlyTasks.value.length).toFixed(1))
})

const unwrap = <T,>(res: any): T => res?.data ?? res

const loadTasks = async () => {
  loading.value = true
  try {
    const [startDate, endDate] = dateRange.value || []
    const res = await taskApi.list({
      startDate,
      endDate,
      status: statusFilter.value || undefined,
    })
    tasks.value = unwrap<DailyTask[]>(res) || []
  } finally {
    loading.value = false
  }
}

const loadMonthlyTasks = async () => {
  const res = await taskApi.list({
    startDate: dayjs().startOf('month').format('YYYY-MM-DD'),
    endDate: dayjs().endOf('month').format('YYYY-MM-DD'),
  })
  monthlyTasks.value = unwrap<DailyTask[]>(res) || []
}

const loadStats = async () => {
  const data = unwrap<any>(await taskApi.stats()) || {}
  stats.weekCompletedCount = data.weekCompletedCount || 0
  stats.weekHours = data.weekHours || 0
  stats.monthCompletedCount = data.monthCompletedCount || 0
  stats.monthHours = data.monthHours || 0
}

const loadOutputOptions = async () => {
  const res = await outputApi.history({
    startDate: dayjs().subtract(180, 'day').format('YYYY-MM-DD'),
    endDate: dayjs().format('YYYY-MM-DD'),
  })
  outputOptions.value = unwrap<MemberOutput[]>(res) || []
}

const loadProjectOptions = async () => {
  const res = await consoleProjectApi.active()
  projectOptions.value = unwrap<ProjectOption[]>(res) || []
}

const resetForm = () => {
  Object.assign(form, {
    projectId: null,
    taskDate: dayjs().format('YYYY-MM-DD'),
    content: '',
    hours: 0,
    aiParticipation: 0,
    outputId: null,
    status: 'PENDING',
  })
  editingTask.value = null
  formRef.value?.clearValidate()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (task: DailyTask) => {
  editingTask.value = task
  Object.assign(form, {
    projectId: task.projectId || null,
    taskDate: task.taskDate,
    content: task.content,
    hours: Number(task.hours || 0),
    aiParticipation: task.aiParticipation,
    outputId: task.outputId || null,
    status: task.status || 'PENDING',
  })
  dialogVisible.value = true
}

const submitTask = async () => {
  await formRef.value?.validate()
  submitting.value = true
  try {
    const payload = { ...form, outputId: form.outputId || null }
    if (editingTask.value) {
      await taskApi.update(editingTask.value.id, payload)
      ElMessage.success('任务已更新')
    } else {
      await taskApi.create(payload)
      ElMessage.success('任务已录入')
    }
    dialogVisible.value = false
    await Promise.all([loadTasks(), loadMonthlyTasks(), loadStats()])
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (task: DailyTask) => {
  try {
    await ElMessageBox.confirm('确认删除这条任务吗？', '提示', { type: 'warning' })
    await taskApi.delete(task.id)
    ElMessage.success('任务已删除')
    await Promise.all([loadTasks(), loadMonthlyTasks(), loadStats()])
  } catch (error) {
    if (error !== 'cancel') {
      throw error
    }
  }
}

const getTasksByDate = (date: string) => taskMap.value[date] || []

const getProjectName = (projectId?: number | null) => {
  if (!projectId) return '-'
  return projectNameMap.value[projectId] || `项目 #${projectId}`
}

const getAiAssistanceLabel = (score?: number | null) => {
  return aiOptions.find(item => item.value === score)?.label || '-'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '待处理',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
  }
  return map[status] || '未知'
}

const getStatusType = (status: string) => {
  const map: Record<string, 'info' | 'success' | 'warning'> = {
    PENDING: 'warning',
    COMPLETED: 'success',
    CANCELLED: 'info',
  }
  return map[status] || 'info'
}

const formatOutput = (output?: MemberOutput | null) => {
  if (!output) return '-'
  return `${output.statDate || ''} #${output.id}${output.projectRootName ? ` ${output.projectRootName}` : ''}`
}

const formatOutputOption = (output: MemberOutput) => {
  const project = output.projectRootName || '未关联项目'
  const lines = output.totalCodeLines ?? 0
  return `${output.statDate} | #${output.id} | ${project} | ${lines} 行`
}

onMounted(async () => {
  await Promise.all([loadTasks(), loadMonthlyTasks(), loadStats(), loadOutputOptions(), loadProjectOptions()])
})
</script>

<style scoped>
.task-page {
  padding: 0;
}

.card-header,
.header-actions,
.filters,
.filter-group,
.task-card__header,
.task-card__actions,
.task-meta {
  display: flex;
  align-items: center;
}

.card-header {
  justify-content: space-between;
  gap: 16px;
  padding: 2px 0;
}

.card-header > span {
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.header-actions {
  gap: 12px;
}

.view-switch {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  background: var(--el-fill-color-lighter);
}

.view-switch__item {
  min-width: 76px;
  height: 34px;
  border: 0;
  border-radius: 6px;
  padding: 0 12px;
  color: var(--el-text-color-secondary);
  background: transparent;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 500;
  transition: color 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;
}

.view-switch__item:hover {
  color: var(--el-color-primary);
  background: var(--el-fill-color-light);
}

.view-switch__item.is-active {
  color: var(--el-color-primary);
  background: var(--el-bg-color);
  box-shadow: 0 2px 8px rgba(31, 45, 61, 0.12);
}

.view-switch__item .el-icon {
  font-size: 16px;
}

.filters {
  gap: 16px;
  flex-wrap: wrap;
  margin-bottom: 18px;
  padding: 14px;
  background: linear-gradient(180deg, var(--el-fill-color-lighter), var(--el-bg-color));
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
}

.filter-group {
  gap: 8px;
}

.filter-label {
  flex: 0 0 auto;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.filter-status :deep(.el-select) {
  width: 160px;
}

.ai-assistance-select {
  width: 100%;
}

.field-label-with-help {
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.help-icon {
  width: 15px;
  height: 15px;
  border: 1px solid var(--el-border-color);
  border-radius: 50%;
  padding: 0;
  color: var(--el-text-color-secondary);
  background: var(--el-fill-color-light);
  cursor: help;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 11px;
  line-height: 1;
}

.help-icon:hover,
.help-icon:focus {
  color: var(--el-color-primary);
  border-color: var(--el-color-primary);
  outline: none;
}

:global(.ai-help-tooltip) {
  max-width: 360px;
}

:global(.ai-help-tooltip .ai-help-content) {
  display: grid;
  gap: 6px;
}

:global(.ai-help-tooltip p) {
  margin: 0;
  line-height: 1.5;
}

.metrics-panel {
  margin-bottom: 16px;
  padding: 14px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  background: var(--el-bg-color);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 10px;
}

.stat-item {
  min-height: 74px;
  text-align: left;
  padding: 12px 14px;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-light);
  border-radius: 8px;
}

.task-card:hover {
  border-color: var(--el-color-primary-light-5);
  box-shadow: 0 8px 24px rgba(31, 45, 61, 0.08);
  transform: translateY(-1px);
}

.stat-value {
  font-size: 22px;
  line-height: 1.2;
  font-weight: 700;
  color: var(--el-color-primary);
}

.stat-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-top: 6px;
}

.task-table {
  width: 100%;
}

.task-grid {
  min-height: 220px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}

.task-grid :deep(.el-empty) {
  grid-column: 1 / -1;
}

.task-card {
  min-height: 172px;
  padding: 16px;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  background: var(--el-bg-color);
  display: flex;
  flex-direction: column;
  gap: 12px;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
}

.task-card__header {
  justify-content: space-between;
  gap: 10px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.task-card__content {
  flex: 1;
  margin: 0;
  color: var(--el-text-color-primary);
  line-height: 1.6;
  word-break: break-word;
}

.task-meta {
  gap: 8px;
  flex-wrap: wrap;
  color: var(--el-text-color-secondary);
  font-size: 12px;
}

.task-meta span {
  padding: 4px 8px;
  border-radius: 6px;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
}

.task-card__actions {
  justify-content: flex-end;
  gap: 8px;
}

.task-calendar :deep(.el-calendar-day) {
  min-height: 116px;
  height: auto;
  padding: 4px;
}

.calendar-cell {
  min-height: 108px;
}

.date-num {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.calendar-task-list {
  margin-top: 4px;
  display: grid;
  gap: 4px;
}

.calendar-task {
  width: 100%;
  border: 0;
  border-radius: 6px;
  padding: 4px 6px;
  background: rgba(64, 158, 255, 0.1);
  color: var(--el-text-color-primary);
  cursor: pointer;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 6px;
  align-items: center;
  text-align: left;
  font-size: 12px;
}

.calendar-task span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.calendar-task b {
  color: var(--el-color-primary);
  font-weight: 600;
}

:deep(.el-date-editor.el-input__wrapper) {
  width: 320px;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .card-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
    align-items: stretch;
  }

  .view-switch {
    flex: 1;
  }

  .view-switch__item {
    flex: 1;
    min-width: 0;
    padding: 0 8px;
  }

  .filters :deep(.el-date-editor),
  .filters :deep(.el-select) {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .metrics-panel {
    padding: 12px;
  }

  .filter-group {
    width: 100%;
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
