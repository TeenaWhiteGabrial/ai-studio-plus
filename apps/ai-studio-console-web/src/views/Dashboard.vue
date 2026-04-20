<template>
  <div class="dashboard">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <h1 class="welcome-title">
          <span class="greeting">{{ greeting }}</span>，开发者
        </h1>
        <p class="welcome-subtitle">今天是 {{ todayDate }}，开始你的一天吧</p>
      </div>
      <div class="welcome-actions">
        <el-button type="primary" size="large" class="create-btn" @click="$router.push('/console/project')">
          <el-icon><Plus /></el-icon>
          <span>新建项目</span>
        </el-button>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card skill-card">
        <div class="stat-content">
          <div class="stat-icon skill-icon">
            <el-icon :size="28"><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.skillCount }}</div>
            <div class="stat-label">我的 Skills</div>
            <div class="stat-trend" v-if="stats.skillTrend">
              <el-icon><TrendCharts /></el-icon>
              <span :class="{ positive: stats.skillTrend > 0, negative: stats.skillTrend < 0 }">
                {{ stats.skillTrend > 0 ? '+' : '' }}{{ stats.skillTrend }}%
              </span>
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
              <span>{{ taskTime }}</span>
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
              <span>活跃</span>
            </div>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card api-card">
        <div class="stat-content">
          <div class="stat-icon api-icon">
            <el-icon :size="28"><Key /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.apiKeyCount }}</div>
            <div class="stat-label">API Keys</div>
            <div class="stat-trend">
              <el-icon><CircleCheck /></el-icon>
              <span>已配置</span>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 主内容区域 -->
    <div class="content-grid">
      <!-- 最近任务 -->
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
          <el-table :data="recentTasks" style="width: 100%" :show-header="false" class="tasks-table">
            <el-table-column width="60">
              <template #default="{ row, $index }">
                <div class="task-icon-wrap">
                  <el-icon><Check v-if="row.status === 'COMPLETED'" /><Loading v-else /></el-icon>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="content" />
            <el-table-column width="80" align="right">
              <template #default="{ row }">
                <span class="task-hours">{{ row.hours }}h</span>
              </template>
            </el-table-column>
            <el-table-column width="90" align="right">
              <template #default="{ row }">
                <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'" size="small">
                  {{ row.status === 'COMPLETED' ? '已完成' : '进行中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="recentTasks.length === 0" description="暂无任务" :image-size="80" />
        </div>
      </el-card>

      <!-- 快捷入口 -->
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
          <div class="action-item" @click="$router.push('/console/apikey')">
            <div class="action-icon apikey-action-icon">
              <el-icon><Key /></el-icon>
            </div>
            <span>管理 API Key</span>
          </div>
          <div class="action-item" @click="$router.push('/console/resource')">
            <div class="action-icon resource-action-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <span>上传资源</span>
          </div>
          <div class="action-item" @click="$router.push('/console/project')">
            <div class="action-icon project-action-icon">
              <el-icon><Folder /></el-icon>
            </div>
            <span>我的项目</span>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { Box, Calendar, Folder, Key, Plus, TrendCharts, Clock, Document, CircleCheck, List, ArrowRight, Check, Loading, Compass, Edit, Upload } from '@element-plus/icons-vue'

const stats = ref({
  skillCount: 12,
  skillTrend: 8,
  taskCount: 3,
  projectCount: 5,
  apiKeyCount: 2,
})

const recentTasks = ref([
  { date: '2026-04-13', content: '完成用户模块开发', hours: 8, status: 'COMPLETED' },
  { date: '2026-04-12', content: '修复登录问题', hours: 3, status: 'COMPLETED' },
  { date: '2026-04-12', content: 'API对接', hours: 5, status: 'COMPLETED' },
])

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

const taskTime = computed(() => {
  const total = recentTasks.value.reduce((sum, task) => sum + task.hours, 0)
  return `${total}h`
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

/* ============================================
   Welcome Section
   ============================================ */
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
  border-radius: medi(12px);
  transition: all var(--ai-transition-base);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--ai-glow-sm);
}

/* ============================================
   Stats Grid
   ============================================ */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--ai-space-4);
  margin-bottom: var(--ai-space-6);
}

.stat-card {
  border-radius: 12px;
  border: 1px solid hsl(var(--border));
  transition: all var(--ai-transition-base);
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--ai-glow-md);
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
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;
  flex-shrink: 0;
  transition: transform var(--ai-transition-base);
}

.stat-card:hover .stat-icon {
  transform: scale(1.05);
}

.stat-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: inherit;
  border-radius: inherit;
  opacity: 0.15;
  filter: blur(8px);
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

.api-icon {
  background: linear-gradient(135deg, hsl(142 76% 46%), hsl(142 76% 38%));
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-value {
  font-size: 32px;
  font-weight: 700;
  color: hsl(var(--foreground));
  line-height: 1.2;
  letter-spacing: -0.02em;
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

.stat-trend .el-icon {
  font-size: 14px;
}

.stat-trend span.positive {
  color: hsl(142 76% 46%);
  font-weight: 600;
}

.stat-trend span.negative {
  color: hsl(0 72% 51%);
  font-weight: 600;
}

/* ============================================
   Content Grid
   ============================================ */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--ai-space-4);
}

/* ============================================
   Card Styling
   ============================================ */
.tasks-card,
.quick-access-card {
  border-radius: 12px;
  border: 1px solid hsl(var(--border));
  overflow: hidden;
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

/* ============================================
   Tasks Table
   ============================================ */
.tasks-table-wrapper {
  min-height: 200px;
}

.tasks-table :deep(.el-table__row) {
  transition: background-color var(--ai-transition-fast);
}

.tasks-table :deep(.el-table__row:hover) {
  background: hsl(var(--secondary)) !important;
}

.task-icon-wrap {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
}

.tasks-table :deep(.el-icon.is-loading) {
  color: hsl(38 92% 50%);
}

.tasks-table :deep(.el-icon:not(.is-loading)) {
  color: hsl(142 76% 46%);
}

.task-hours {
  font-size: 13px;
  font-weight: 500;
  color: hsl(var(--muted-foreground));
}

/* ============================================
   Quick Actions
   ============================================ */
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
  border-radius: 12px;
  cursor: pointer;
  transition: all var(--ai(transition-base));
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
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  transition: transform var(--ai-transition-base);
}

.action-item:hover .action-icon {
  transform: scale(1.1);
}

.task-action-icon {
  background: linear-gradient(135deg, hsl(220 70% 55%), hsl(220 70% 45%));
}

.apikey-action-icon {
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

/* ============================================
   Card Overrides
   ============================================ */
:deep(.el-card__header) {
  padding: 0;
  border: none;
}

:deep(.el-card__body) {
  padding: 0;
}

/* ============================================
   Tag Styling
   ============================================ */
:deep(.el-tag--success) {
  --el-tag-bg-color: hsla(142 76% 36% 0.1);
  --el-tag-border-color: hsla(142 76% 36% 0.2);
  --el-tag-text-color: hsl(142 76% 36%);
  border-radius: 6px;
  font-weight: 500;
}

:deep(.el-tag--warning) {
  --el-tag-bg-color: hsla(38 92% 50% 0.1);
  --el-tag-border-color: hsla(38 92% 50% 0.2);
  --el-tag-text-color: hsl(38 92% 50%);
  border-radius: 6px;
  font-weight: 500;
}

/* ============================================
   Empty State
   ============================================ */
:deep(.el-empty) {
  padding: var(--ai-space-8) 0;
}

:deep(.el-empty__description) {
  color: hsl(var(--muted-foreground));
}

/* ============================================
   Responsive
   ============================================ */
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

  .welcome-actions {
    width: 100%;
  }

  .create-btn {
    width: 100%;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }
}
</style>
