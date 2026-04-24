<template>
  <div class="audit-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资源审核</span>
          <el-tag type="warning" size="large">{{ pendingCount }} 待审核</el-tag>
        </div>
      </template>

      <el-tabs v-model="activeTab" @change="handleTabChange">
        <el-tab-pane label="Skill" name="skill">
          <AuditList type="skill" @update-pending="updatePendingCount" />
        </el-tab-pane>
        <el-tab-pane label="MCP" name="mcp">
          <AuditList type="mcp" @update-pending="updatePendingCount" />
        </el-tab-pane>
        <el-tab-pane label="Plugin" name="plugin">
          <AuditList type="plugin" @update-pending="updatePendingCount" />
        </el-tab-pane>
        <el-tab-pane label="教程" name="tutorial">
          <AuditList type="tutorial" @update-pending="updatePendingCount" />
        </el-tab-pane>
        <el-tab-pane label="安装包" name="installer">
          <AuditList type="installer" @update-pending="updatePendingCount" />
        </el-tab-pane>
        <el-tab-pane label="视频" name="video">
          <AuditList type="video" @update-pending="updatePendingCount" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import AuditList from './components/AuditList.vue'

const activeTab = ref('skill')
const pendingCount = ref(0)

const updatePendingCount = (count: number) => {
  pendingCount.value = count
}

const handleTabChange = () => {
  // Tab 切换时的处理
}
</script>

<style scoped>
.audit-page {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
