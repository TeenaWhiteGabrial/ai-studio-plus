<template>
  <div>
    <div class="resource-list" :class="`view-mode-${viewMode}`">
      <template v-if="viewMode === 'card'">
        <el-card v-for="plugin in pluginList" :key="plugin.id" class="resource-card plugin-card" shadow="hover">
          <div class="card-body">
            <div class="card-main">
              <div class="resource-icon plugin-icon">
                <el-icon :size="24"><Connection /></el-icon>
              </div>
              <div class="resource-info">
                <div class="resource-header-row">
                  <h3 class="resource-name">
                    {{ plugin.name }}
                    <el-tag v-if="isCurrentUserResource(plugin)" size="small" type="primary" class="my-tag">我的</el-tag>
                  </h3>
                </div>
                <p class="resource-desc">{{ plugin.description }}</p>
                <div class="resource-meta">
                  <el-tag size="small" type="info">{{ plugin.category }}</el-tag>
                  <el-tag size="small" :type="getStatusType(plugin.status)">{{ getStatusText(plugin.status) }}</el-tag>
                  <span class="version-text">v{{ plugin.latest_version }}</span>
                </div>
              </div>
            </div>
            <div class="card-actions">
              <el-button link type="primary" @click="$emit('download', plugin)">下载</el-button>
              <el-button v-if="isCurrentUserResource(plugin)" link type="primary" @click="$emit('publish-version', 'plugin', plugin)">发布版本</el-button>
              <el-dropdown v-if="isCurrentUserResource(plugin)" @command="(cmd: string) => $emit('action', cmd, plugin)">
                <el-button link>
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">编辑</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-card>
      </template>
      <template v-else>
        <div class="list-table plugin-list">
          <div class="list-header">
            <div class="list-cell name-cell">名称</div>
            <div class="list-cell">描述</div>
            <div class="list-cell">分类</div>
            <div class="list-cell">状态</div>
            <div class="list-cell">版本</div>
            <div class="list-cell actions-cell">操作</div>
          </div>
          <div v-for="plugin in pluginList" :key="plugin.id" class="list-row">
            <div class="list-cell name-cell">
              <div class="row-icon plugin-icon">
                <el-icon :size="18"><Connection /></el-icon>
              </div>
              <span class="row-name">
                {{ plugin.name }}
                <el-tag v-if="isCurrentUserResource(plugin)" size="small" type="primary" class="my-tag">我的</el-tag>
              </span>
            </div>
            <div class="list-cell">{{ plugin.description }}</div>
            <div class="list-cell">
              <el-tag size="small" type="info">{{ plugin.category }}</el-tag>
            </div>
            <div class="list-cell">
              <el-tag size="small" :type="getStatusType(plugin.status)">{{ getStatusText(plugin.status) }}</el-tag>
            </div>
            <div class="list-cell">v{{ plugin.latest_version }}</div>
            <div class="list-cell actions-cell">
              <el-button v-if="isCurrentUserResource(plugin)" link type="primary" size="small" @click="$emit('publish-version', 'plugin', plugin)">发布版本</el-button>
              <el-button link type="primary" size="small" @click="$emit('download', plugin)">下载</el-button>
              <el-dropdown v-if="isCurrentUserResource(plugin)" @command="(cmd: string) => $emit('action', cmd, plugin)">
                <el-button link size="small">
                  <el-icon><MoreFilled /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="edit">编辑</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </div>
      </template>
      <el-empty v-if="pluginList.length === 0 && !loading" description="暂无 Plugin" />
      <div v-if="loading" class="loading-state">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>
    </div>
    <div v-if="!loading && pluginList.length > 0" class="pagination-wrapper">
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
import { Connection, Loading, MoreFilled } from '@element-plus/icons-vue'
import { useResourceUtils } from '@/composables/useResourceCenter'

const { getStatusType, getStatusText } = useResourceUtils()

defineProps<{
  pluginList: any[]
  loading: boolean
  viewMode: 'card' | 'list'
  pagination: { page: number; size: number }
  total: number
  isCurrentUserResource: (resource: any) => boolean
}>()

defineEmits<{
  'publish-version': [type: string, resource: any]
  'download': [plugin: any]
  'action': [command: string, plugin: any]
  'page-change': []
}>()
</script>
