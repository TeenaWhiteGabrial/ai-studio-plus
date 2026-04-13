<template>
  <div class="audit-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资源审核</span>
          <el-tag type="warning" size="large">{{ pendingCount }} 待审核</el-tag>
        </div>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="Skill" name="skill">
          <audit-list type="skill" />
        </el-tab-pane>
        <el-tab-pane label="MCP" name="mcp">
          <audit-list type="mcp" />
        </el-tab-pane>
        <el-tab-pane label="Plugin" name="plugin">
          <audit-list type="plugin" />
        </el-tab-pane>
        <el-tab-pane label="教程" name="tutorial">
          <audit-list type="tutorial" />
        </el-tab-pane>
        <el-tab-pane label="安装包" name="installer">
          <audit-list type="installer" />
        </el-tab-pane>
        <el-tab-pane label="视频" name="video">
          <audit-list type="video" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, provide } from 'vue'

const activeTab = ref('skill')
const pendingCount = ref(0)

// Provide audit type mapping
provide('auditTypes', {
  skill: 'Skill',
  mcp: 'MCP',
  plugin: 'Plugin',
  tutorial: '教程',
  installer: '安装包',
  video: '视频',
})
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
