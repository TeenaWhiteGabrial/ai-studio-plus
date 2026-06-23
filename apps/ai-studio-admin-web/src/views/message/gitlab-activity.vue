<template>
  <div class="activity-page">
    <section class="trace-hero">
      <div>
        <p>GitLab Activity Trace</p>
        <h1>GitLab 活动追踪</h1>
        <span>查看任务、事件日志以及显式关联出的 commits、MR、工作项和日报，便于排查采集与分析链路。</span>
      </div>
      <div class="trace-status">
        <strong>{{ taskState.total }} 个任务</strong>
        <span>{{ eventState.total }} 条事件日志</span>
        <small>支持任务重试与立即执行</small>
      </div>
    </section>

    <el-row :gutter="18" class="panels">
      <el-col :span="11">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>任务列表</span>
              <div class="header-actions">
                <el-select v-model="taskQuery.status" clearable placeholder="全部状态" style="width: 150px" @change="loadTasks">
                  <el-option label="PENDING" value="PENDING" />
                  <el-option label="RUNNING" value="RUNNING" />
                  <el-option label="RETRY" value="RETRY" />
                  <el-option label="FAILED" value="FAILED" />
                  <el-option label="SUCCESS" value="SUCCESS" />
                </el-select>
                <el-button :loading="taskLoading" @click="loadTasks">刷新</el-button>
              </div>
            </div>
          </template>

          <el-table :data="taskState.records" height="560" v-loading="taskLoading" @row-click="selectTask">
            <el-table-column prop="id" label="ID" width="72" />
            <el-table-column prop="taskType" label="任务类型" width="110" />
            <el-table-column prop="status" label="状态" width="100" />
            <el-table-column prop="eventLogId" label="事件ID" width="90" />
            <el-table-column prop="targetDate" label="目标日期" min-width="120" />
            <el-table-column label="操作" width="170" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click.stop="runTask(row)">立即执行</el-button>
                <el-button link type="warning" @click.stop="retryTask(row)">重试</el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pager">
            <el-pagination
              layout="total, prev, pager, next"
              :total="taskState.total"
              :page-size="taskQuery.size"
              :current-page="taskQuery.page"
              @current-change="handleTaskPageChange"
            />
          </div>
        </el-card>
      </el-col>

      <el-col :span="13">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>事件日志</span>
              <div class="header-actions">
                <el-input
                  v-model.number="eventQuery.gitlabProjectId"
                  clearable
                  placeholder="GitLab 项目ID"
                  style="width: 160px"
                />
                <el-select v-model="eventQuery.processStatus" clearable placeholder="全部状态" style="width: 150px">
                  <el-option label="PENDING" value="PENDING" />
                  <el-option label="RECORDED" value="RECORDED" />
                </el-select>
                <el-button :loading="eventLoading" @click="loadEventLogs">刷新</el-button>
              </div>
            </div>
          </template>

          <el-table :data="eventState.records" height="280" v-loading="eventLoading" @row-click="selectEvent">
            <el-table-column prop="id" label="ID" width="72" />
            <el-table-column prop="eventType" label="事件类型" width="160" />
            <el-table-column prop="gitlabProjectId" label="项目ID" width="90" />
            <el-table-column prop="processStatus" label="处理状态" width="100" />
            <el-table-column prop="refName" label="Ref" min-width="180" show-overflow-tooltip />
          </el-table>

          <div class="pager">
            <el-pagination
              layout="total, prev, pager, next"
              :total="eventState.total"
              :page-size="eventQuery.size"
              :current-page="eventQuery.page"
              @current-change="handleEventPageChange"
            />
          </div>

          <el-divider content-position="left">详情</el-divider>

          <div v-if="selectedEvent" class="detail-grid">
            <div class="detail-block">
              <h3>基础信息</h3>
              <ul>
                <li><strong>事件ID：</strong>{{ selectedEvent.id }}</li>
                <li><strong>Event UID：</strong>{{ selectedEvent.eventUid }}</li>
                <li><strong>事件类型：</strong>{{ selectedEvent.eventType }}</li>
                <li><strong>处理状态：</strong>{{ selectedEvent.processStatus || '-' }}</li>
                <li><strong>处理消息：</strong>{{ selectedEvent.processMessage || '-' }}</li>
              </ul>
            </div>

            <div class="detail-block">
              <h3>显式关联结果</h3>
              <ul>
                <li><strong>Commits：</strong>{{ selectedEvent.relatedCommitRefs.join(', ') || '-' }}</li>
                <li><strong>MR：</strong>{{ selectedEvent.relatedMrRefs.join(', ') || '-' }}</li>
                <li><strong>日报：</strong>{{ selectedEvent.relatedReportTitles.join('；') || '-' }}</li>
              </ul>
              <div class="work-items">
                <strong>工作项：</strong>
                <el-tag
                  v-for="item in selectedEvent.relatedWorkItemSummaries"
                  :key="item"
                  class="item-tag"
                  type="info"
                >
                  {{ item }}
                </el-tag>
              </div>
            </div>

            <div class="detail-block full-width">
              <h3>Payload</h3>
              <pre>{{ selectedEvent.payloadJson || '{}' }}</pre>
            </div>
          </div>
          <el-empty v-else description="点击上方事件日志查看详情" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { gitlabActivityAdminApi, type GitlabActivityTaskItem, type GitlabEventLogItem } from '@/api'

const taskLoading = ref(false)
const eventLoading = ref(false)
const selectedTask = ref<GitlabActivityTaskItem | null>(null)
const selectedEvent = ref<GitlabEventLogItem | null>(null)

const taskQuery = reactive({
  page: 1,
  size: 10,
  status: '',
})

const eventQuery = reactive({
  page: 1,
  size: 10,
  processStatus: '',
  gitlabProjectId: undefined as number | undefined,
})

const taskState = reactive<{ total: number; records: GitlabActivityTaskItem[] }>({
  total: 0,
  records: [],
})

const eventState = reactive<{ total: number; records: GitlabEventLogItem[] }>({
  total: 0,
  records: [],
})

onMounted(async () => {
  await Promise.all([loadTasks(), loadEventLogs()])
})

async function loadTasks() {
  taskLoading.value = true
  try {
    const data = await gitlabActivityAdminApi.listTasks({
      page: taskQuery.page,
      size: taskQuery.size,
      status: taskQuery.status || undefined,
    })
    taskState.total = data.total
    taskState.records = data.records
  } finally {
    taskLoading.value = false
  }
}

async function loadEventLogs() {
  eventLoading.value = true
  try {
    const data = await gitlabActivityAdminApi.listEventLogs({
      page: eventQuery.page,
      size: eventQuery.size,
      processStatus: eventQuery.processStatus || undefined,
      gitlabProjectId: eventQuery.gitlabProjectId,
    })
    eventState.total = data.total
    eventState.records = data.records
  } finally {
    eventLoading.value = false
  }
}

async function selectTask(row: GitlabActivityTaskItem) {
  selectedTask.value = await gitlabActivityAdminApi.getTask(row.id)
  if (selectedTask.value.eventLogId) {
    selectedEvent.value = await gitlabActivityAdminApi.getEventLog(selectedTask.value.eventLogId)
  }
}

async function selectEvent(row: GitlabEventLogItem) {
  selectedEvent.value = await gitlabActivityAdminApi.getEventLog(row.id)
}

async function retryTask(row: GitlabActivityTaskItem) {
  await gitlabActivityAdminApi.retryTask(row.id)
  ElMessage.success('任务已重新入队')
  await loadTasks()
}

async function runTask(row: GitlabActivityTaskItem) {
  await gitlabActivityAdminApi.runTaskNow(row.id)
  ElMessage.success('任务已触发执行')
  await loadTasks()
}

function handleTaskPageChange(page: number) {
  taskQuery.page = page
  loadTasks()
}

function handleEventPageChange(page: number) {
  eventQuery.page = page
  loadEventLogs()
}
</script>

<style scoped>
.activity-page {
  padding: 22px;
}

.trace-hero {
  min-height: 160px;
  border-radius: 8px;
  padding: 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  color: #fff;
  background:
    radial-gradient(circle at 80% 20%, rgba(250, 204, 21, 0.18), transparent 28%),
    linear-gradient(135deg, #111827, #1e293b);
}

.trace-hero p {
  margin: 0 0 8px;
  color: #fde68a;
  font-size: 12px;
  font-weight: 900;
}

.trace-hero h1 {
  margin: 0 0 8px;
  font-size: 30px;
}

.trace-hero span {
  color: rgba(226, 232, 240, 0.82);
}

.trace-status {
  min-width: 240px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid rgba(148, 163, 184, 0.22);
  background: rgba(15, 23, 42, 0.68);
}

.panels {
  margin-top: 18px;
}

.panel-card {
  border-radius: 8px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.pager {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.detail-block {
  padding: 16px;
  border-radius: 8px;
  background: #f8fafc;
  border: 1px solid #e5e7eb;
}

.detail-block h3 {
  margin: 0 0 12px;
  font-size: 15px;
}

.detail-block ul {
  margin: 0;
  padding-left: 18px;
  color: #374151;
  line-height: 1.8;
}

.full-width {
  grid-column: 1 / -1;
}

pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
  font-size: 12px;
  line-height: 1.6;
  color: #111827;
}

.work-items {
  margin-top: 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.item-tag {
  max-width: 100%;
}
</style>
