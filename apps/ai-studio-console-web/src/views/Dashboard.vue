<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon skill-icon">
            <el-icon :size="32"><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.skillCount }}</div>
            <div class="stat-label">我的 Skills</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon task-icon">
            <el-icon :size="32"><Calendar /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.taskCount }}</div>
            <div class="stat-label">今日任务</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon project-icon">
            <el-icon :size="32"><Folder /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.projectCount }}</div>
            <div class="stat-label">进行中项目</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-icon api-icon">
            <el-icon :size="32"><Key /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.apiKeyCount }}</div>
            <div class="stat-label">API Keys</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>最近任务</span>
          </template>
          <el-table :data="recentTasks" style="width: 100%">
            <el-table-column prop="date" label="日期" width="120" />
            <el-table-column prop="content" label="任务内容" />
            <el-table-column prop="hours" label="工时" width="80" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'COMPLETED' ? 'success' : 'warning'">
                  {{ row.status === 'COMPLETED' ? '已完成' : '进行中' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="$router.push('/console/task')">录入任务</el-button>
            <el-button type="success" @click="$router.push('/console/apikey')">管理 API Key</el-button>
            <el-button type="warning" @click="$router.push('/console/resource')">上传资源</el-button>
            <el-button @click="$router.push('/console/project')">我的项目</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Box, Calendar, Folder, Key } from '@element-plus/icons-vue'

const stats = ref({
  skillCount: 0,
  taskCount: 0,
  projectCount: 0,
  apiKeyCount: 0,
})

const recentTasks = ref([
  { date: '2026-04-13', content: '完成用户模块开发', hours: 8, status: 'COMPLETED' },
  { date: '2026-04-12', content: '修复登录问题', hours: 3, status: 'COMPLETED' },
  { date: '2026-04-12', content: 'API对接', hours: 5, status: 'COMPLETED' },
])
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.skill-icon { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.task-icon { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.project-icon { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
.api-icon { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.mt-20 {
  margin-top: 20px;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.quick-actions .el-button {
  width: 100%;
}
</style>
