<template>
  <div class="resource-page">
    <el-card>
      <template #header>
        <span>资源中心</span>
      </template>

      <el-tabs v-model="activeTab">
        <el-tab-pane label="Skill" name="skill">
          <div class="resource-list">
            <el-empty description="暂无 Skill" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="MCP" name="mcp">
          <div class="resource-list">
            <el-empty description="暂无 MCP" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="Plugin" name="plugin">
          <div class="resource-list">
            <el-empty description="暂无 Plugin" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="教程" name="tutorial">
          <div class="resource-list">
            <el-empty description="暂无教程" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="安装包" name="installer">
          <div class="resource-list">
            <el-empty description="暂无安装包" />
          </div>
        </el-tab-pane>
        <el-tab-pane label="视频" name="video">
          <div class="resource-list">
            <el-empty description="暂无视频" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

const activeTab = ref('skill')
</script>

<style scoped>
.resource-page {
  padding: 0;
}

.resource-list {
  min-height: 200px;
}
</style>
