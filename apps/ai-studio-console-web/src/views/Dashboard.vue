<template>
  <div class="dashboard" v-loading="loading">
    <div class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">
          <span class="greeting">{{ greeting }}</span>，{{ displayName }}
        </h1>
        <p class="welcome-subtitle">今天是 {{ todayDate }}，开始你的工作吧</p>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" size="large" class="create-btn" @click="$router.push('/console/task')">
          <el-icon><Plus /></el-icon>
          <span>录入任务</span>
        </el-button>
      </div>
    </div>

    <div class="stats-grid">
      <el-card class="stat-card skill-card">
        <div class="stat-content">
          <div class="stat-icon skill-icon">
            <el-icon :size="28"><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.skillCount }}</div>
            <div class="stat-label">我的 Skills</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>本月新增 {{ stats.skillMonthCount }}</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card task-card">
        <div class="stat-content">
          <div class="stat-icon task-icon">
            <el-icon :size="28"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.taskCount }}</div>
            <div class="stat-label">今日任务</div>
            <div class="stat-trend">
              <el-icon><Clock /></el-icon>
              <span>{{ stats.todayTaskHours }}h</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card project-card">
        <div class="stat-content">
          <div class="stat-icon project-icon">
            <el-icon :size="28"><Folder /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.projectCount }}</div>
            <div class="stat-label">进行中项目</div>
            <div class="stat-trend">
              <el-icon><Document /></el-icon>
              <span>来自项目管理</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card hours-card">
        <div class="stat-content">
          <div class="stat-icon hours-icon">
            <el-icon :size="28"><DataLine /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.monthTaskHours }}h</div>
            <div class="stat-label">本月任务工时</div>
            <div class="stat-trend">
              <el-icon><CircleCheck /></el-icon>
              <span>任务管理汇总</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <div class="content-grid">
      <el-card class="tasks-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon><List /></el-icon>
              <span>最近任务</span>
            </div>
            <el-button link type="primary" size="small" @click="$router.push('/console/task')">
              查看全部
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="tasks-table-wrapper">
          <el-table v-if="recentTasks.length > 0" :data="recentTasks" style="width: 100%" :show-header="false" class="tasks-table">
            <el-table-column width="56">
              <template #default="{ row }">
                <div class="task-icon-wrap" :class="{ completed: row.status === 'COMPLETED' }">
                  <el-icon><Check v-if="row.status === 'COMPLETED'" /><Loading v-else /></el-icon>
                </div>
              </template>
            </el-table-column>
            <el-table-column min-width="220">
              <template #default="{ row }">
                <div class="task-main">
                  <span class="task-title">{{ row.content }}</span>
                  <span class="task-sub">{{ row.taskDate }} · {{ row.projectName || '未关联项目' }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column width="80" align="right">
              <template #default="{ row }">
                <span class="task-hours">{{ row.hours }}h</span>
              </template>
            </el-table-column>
            <el-table-column width="90" align="right">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)" size="small">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无任务" :image-size="80" />
        </div>
      </el-card>

      <el-card class="quick-access-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <el-icon><Compass /></el-icon>
              <span>快捷入口</span>
            </div>
          </div>
        </template>
        <div class="quick-actions">
          <div class="action-item" @click="$router.push('/console/task')">
            <div class="action-icon task-action-icon">
              <el-icon><Edit /></el-icon>
            </div>
            <span>录入任务</span>
          </div>
          <div class="action-item" @click="$router.push('/console/stats')">
            <div class="action-icon stats-action-icon">
              <el-icon><DataLine /></el-icon>
            </div>
            <span>产出统计</span>
          </div>
          <div class="action-item" @click="$router.push('/console/resource')">
            <div class="action-icon resource-action-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <span>上传资源</span>
          </div>
          <div class="action-item" @click="$router.push('/console/task')">
            <div class="action-icon project-action-icon">
              <el-icon><Folder /></el-icon>
            </div>
            <span>任务清单</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  ArrowRight,
  Box,
  Calendar,
  Check,
  CircleCheck,
  Clock,
  Compass,
  DataLine,
  Document,
  Edit,
  Folder,
  List,
  Loading,
  Plus,
  TrendCharts,
  Upload,
} from '@element-plus/icons-vue'
import { dashboardApi } from '@/api'

interface DashboardTask {
  id: number
  projectId?: number
  projectName?: string
  taskDate: string
  content: string
  hours: number
  status: string
}

const loading = ref(false)
const recentTasks = ref<DashboardTask[]>([])

const stats = reactive({
  skillCount: 0,
  skillMonthCount: 0,
  taskCount: 0,
  todayTaskHours: 0,
  projectCount: 0,
  monthTaskHours: 0,
})

const displayName = computed(() => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    return userInfo.realName || userInfo.real_name || userInfo.username || '开发者'
  } catch {
    return '开发者'
  }
})

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const todayDate = computed(() => {
  const now = new Date()
  const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric', weekday: 'long' }
  return now.toLocaleDateString('zh-CN', options)
})

const loadDashboard = async () => {
  loading.value = true
  try {
    const res: any = await dashboardApi.overview()
    const data = res?.data || {}
    stats.skillCount = Number(data.skillCount || 0)
    stats.skillMonthCount = Number(data.skillMonthCount || 0)
    stats.taskCount = Number(data.taskCount || 0)
    stats.todayTaskHours = Number(data.todayTaskHours || 0)
    stats.projectCount = Number(data.projectCount || 0)
    stats.monthTaskHours = Number(data.monthTaskHours || 0)
    recentTasks.value = data.recentTasks || []
  } catch (error) {
    console.error('加载 Dashboard 数据失败:', error)
    ElMessage.error('加载 Dashboard 数据失败')
  } finally {
    loading.value = false
  }
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
  const map: Record<string, 'warning' | 'success' | 'info'> = {
    PENDING: 'warning',
    COMPLETED: 'success',
    CANCELLED: 'info',
  }
  return map[status] || 'info'
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--ai-space-6) 0;
  margin-bottom: var(--ai-space-6);
  border-bottom: 1px solid hsl(var(--border));
}

.welcome-content {
  flex: 1;
}

.welcome-title {
  font-size: 28px;
  font-weight: 600;
  color: hsl(var(--foreground));
  margin: 0 0 var(--ai-space-2) 0;
  line-height: 1.3;
}

.greeting {
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 70% 65%));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-subtitle {
  font-size: 14px;
  color: hsl(var(--muted-foreground));
  margin: 0;
  line-height: 1.5;
}

.welcome-actions {
  flex-shrink: 0;
}

.create-btn {
  height: 44px;
  padding: 0 var(--ai-space-6);
  font-weight: 500;
  border-radius: 8px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--ai-space-4);
  margin-bottom: var(--ai-space-6);
}

.stat-card,
.tasks-card,
.quick-access-card {
  border-radius: 8px;
  border: 1px solid hsl(var(--border));
  overflow: hidden;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: var(--ai-space-4);
  padding: var(--ai-space-5);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  flex-shrink: 0;
}

.skill-icon {
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 70% 50%));
}

.task-icon {
  background: linear-gradient(135deg, hsl(280 65% 60%), hsl(280 65% 50%));
}

.project-icon {
  background: linear-gradient(135deg, hsl(190 80% 55%), hsl(200 80% 45%));
}

.hours-icon {
  background: linear-gradient(135deg, hsl(142 76% 46%), hsl(142 76% 38%));
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  color: hsl(var(--foreground));
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: hsl(var(--muted-foreground));
  margin-top: var(--ai-space-1);
  font-weight: 500;
}

.stat-trend {
  display: flex;
  align-items: center;
  gap: var(--ai-space-1);
  margin-top: var(--ai-space-2);
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--ai-space-4);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--ai-space-4) var(--ai-space-5);
  border-bottom: 1px solid hsl(var(--border));
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--ai-space-2);
  font-weight: 600;
  font-size: 16px;
  color: hsl(var(--foreground));
}

.header-left .el-icon {
  color: hsl(var(--primary));
  font-size: 18px;
}

.tasks-table-wrapper {
  min-height: 220px;
}

.tasks-table :deep(.el-table__row:hover) {
  background: hsl(var(--secondary)) !important;
}

.task-icon-wrap {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: hsl(38 92% 50%);
  background: hsl(38 92% 50% / 0.1);
}

.task-icon-wrap.completed {
  color: hsl(142 76% 36%);
  background: hsl(142 76% 36% / 0.1);
}

.task-main {
  min-width: 0;
  display: grid;
  gap: 4px;
}

.task-title {
  color: hsl(var(--foreground));
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.task-sub,
.task-hours {
  font-size: 12px;
  color: hsl(var(--muted-foreground));
}

.task-hours {
  font-weight: 600;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--ai-space-3);
  padding: var(--ai-space-4);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--ai-space-2);
  padding: var(--ai-space-4) var(--ai-space-2);
  border-radius: 8px;
  cursor: pointer;
  transition: all var(--ai-transition-base);
  background: hsl(var(--muted));
  border: 1px solid transparent;
}

.action-item:hover {
  background: hsl(var(--secondary));
  transform: translateY(-2px);
}

.action-icon {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
}

.task-action-icon {
  background: linear-gradient(135deg, hsl(220 70% 55%), hsl(220 70% 45%));
}

.stats-action-icon {
  background: linear-gradient(135deg, hsl(142 76% 46%), hsl(142 76% 38%));
}

.resource-action-icon {
  background: linear-gradient(135deg, hsl(38 92% 50%), hsl(38 92% 40%));
}

.project-action-icon {
  background: linear-gradient(135deg, hsl(190 80% 55%), hsl(200 80% 45%));
}

.action-item span {
  font-size: 13px;
  font-weight: 500;
  color: hsl(var(--foreground));
}

:deep(.el-card__header) {
  padding: 0;
  border: none;
}

:deep(.el-card__body) {
  padding: 0;
}

:deep(.el-empty) {
  padding: var(--ai-space-8) 0;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--ai-space-4);
  }

  .welcome-actions,
  .create-btn {
    width: 100%;
  }

  .stats-grid,
  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
