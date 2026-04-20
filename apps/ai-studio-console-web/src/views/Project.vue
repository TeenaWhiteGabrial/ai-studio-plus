<template>
  <div class="project-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>项目管理</span>
          <el-button type="primary" @click="createProject">新建项目</el-button>
        </div>
      </template>

      <el-table :data="projects" style="width: 100%">
        <el-table-column prop="name" label="项目名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="owner" label="负责人" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '进行中' : '已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startedAt" label="开始时间" width="120" />
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button link type="primary" @click="editProject(row)">编辑</el-button>
            <el-button link :type="row.status === 'ACTIVE' ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 'ACTIVE' ? '结束' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const projects = ref([
  { id: 1, name: 'AI 平台开发', description: 'AI Studio 平台开发项目', owner: '张三', status: 'ACTIVE', startedAt: '2026-01-01' },
])

const createProject = () => {
  ElMessage.info('新建项目功能开发中')
}

const editProject = (_row: any) => {
  ElMessage.info('编辑项目功能开发中')
}

const toggleStatus = (row: any) => {
  row.status = row.status === 'ACTIVE' ? 'ENDED' : 'ACTIVE'
  ElMessage.success('状态已更新')
}
</script>

<style scoped>
.project-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: 600;
  color: var(--ai-text-primary);
}

:deep(.el-card__header) {
  border-bottom: 1px solid var(--ai-border-default);
}

/* Table styling */
:deep(.el-table) {
  border-radius: var(--ai-radius-md);
  overflow: hidden;
}

:deep(.el-table th.el-table__cell) {
  color: var(--ai-text-tertiary);
  font-weight: 500;
}

:deep(.el-table td.el-table__cell) {
  color: var(--ai-text-secondary);
}

/* Button styling */
:deep(.el-button) {
  font-weight: 500;
}

/* Tag styling */
:deep(.el-tag--success) {
  --el-tag-bg-color: hsla(142, 76%, 36%, 0.1);
  --el-tag-border-color: hsla(142, 76%, 36%, 0.2);
  --el-tag-text-color: var(--ai-success);
}

:deep(.el-tag--warning) {
  --el-tag-bg-color: hsla(38, 92%, 50%, 0.1);
  --el-tag-border-color: hsla(38, 92%, 50%, 0.2);
  --el-tag-text-color: var(--ai-warning);
}

:deep(.el-tag--info) {
  --el-tag-bg-color: hsla(199, 89%, 48%, 0.1);
  --el-tag-border-color: hsla(199, 89%, 48%, 0.2);
  --el-tag-text-color: var(--ai-info);
}
</style>
