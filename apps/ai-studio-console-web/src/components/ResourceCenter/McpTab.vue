<template>
  <div>
    <div class="resource-list" :class="`view-mode-${viewMode}`">
      <template v-if="viewMode === 'card'">
        <el-card v-for="mcp in mcpList" :key="mcp.id" class="resource-card mcp-card" shadow="hover">
          <div class="card-body">
            <div class="card-main">
              <div class="resource-icon mcp-icon">
                <el-icon :size="24"><Setting /></el-icon>
              </div>
              <div class="resource-info">
                <h3 class="resource-name">{{ mcp.name }}</h3>
                <p class="resource-desc">{{ mcp.description || '无描述' }}</p>
                <div class="resource-meta">
                  <el-tag size="small" :type="mcp.status === 1 ? 'success' : 'danger'">
                    {{ mcp.status === 1 ? '已启用' : '已禁用' }}
                  </el-tag>
                  <span class="endpoint-text">{{ mcp.endpoint }}</span>
                </div>
              </div>
            </div>
            <div class="card-actions">
              <el-button link type="primary" @click="$emit('test-connection', mcp)">测试连接</el-button>
            </div>
          </div>
        </el-card>
      </template>
      <template v-else>
        <div class="list-table mcp-list">
          <div class="list-header">
            <div class="list-cell name-cell">名称</div>
            <div class="list-cell">描述</div>
            <div class="list-cell">端点</div>
            <div class="list-cell">状态</div>
            <div class="list-cell actions-cell">操作</div>
          </div>
          <div v-for="mcp in mcpList" :key="mcp.id" class="list-row">
            <div class="list-cell name-cell">
              <div class="row-icon mcp-icon">
                <el-icon :size="18"><Setting /></el-icon>
              </div>
              <span class="row-name">{{ mcp.name }}</span>
            </div>
            <div class="list-cell">{{ mcp.description || '无描述' }}</div>
            <div class="list-cell">
              <span class="endpoint-text">{{ mcp.endpoint }}</span>
            </div>
            <div class="list-cell">
              <el-tag size="small" :type="mcp.status === 1 ? 'success' : 'danger'">
                {{ mcp.status === 1 ? '已启用' : '已禁用' }}
              </el-tag>
            </div>
            <div class="list-cell actions-cell">
              <el-button link type="primary" size="small" @click="$emit('test-connection', mcp)">测试连接</el-button>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-if="mcpList.length === 0 && !loading" description="暂无 MCP 服务器" />
      <div v-if="loading" class="loading-state">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>
    </div>
    <div v-if="!loading && mcpList.length > 0" class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="total"
        :page-sizes="[12, 24, 48, 96]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="$emit('page-change')"
        @current-change="$emit('page-change')"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { Setting, Loading } from '@element-plus/icons-vue'

defineProps<{
  mcpList: any[]
  loading: boolean
  viewMode: 'card' | 'list'
  pagination: { page: number; size: number }
  total: number
}>()

defineEmits<{
  'test-connection': [mcp: any]
  'page-change': []
}>()
</script>
