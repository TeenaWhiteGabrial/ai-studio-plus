<template>
  <div>
    <div class="resource-list" :class="`view-mode-${viewMode}`">
      <template v-if="viewMode === 'card'">
        <el-card v-for="tutorial in tutorialList" :key="tutorial.id" class="resource-card tutorial-card" shadow="hover">
          <div class="card-body">
            <div class="card-main">
              <div class="resource-icon tutorial-icon">
                <el-icon :size="24"><Reading /></el-icon>
              </div>
              <div class="resource-info">
                <div class="resource-header-row">
                  <h3 class="resource-name">
                    {{ tutorial.title }}
                    <el-tag v-if="isCurrentUserResource(tutorial)" size="small" type="primary" class="my-tag">我的</el-tag>
                  </h3>
                </div>
                <p class="resource-desc">{{ tutorial.description }}</p>
                <div class="resource-meta">
                  <el-tag size="small" type="info">{{ tutorial.category }}</el-tag>
                  <el-tag size="small" :type="getStatusType(tutorial.status)">{{ getStatusText(tutorial.status) }}</el-tag>
                  <el-tag v-if="tutorial.contentType === 'video'" size="small" type="warning">视频</el-tag>
                  <el-tag v-if="tutorial.zipFileUrl" size="small" type="success">含附件</el-tag>
                </div>
              </div>
            </div>
            <div class="card-actions">
              <el-button v-if="isCurrentUserResource(tutorial)" link type="primary" @click="$emit('publish-version', 'tutorial', tutorial)">发布版本</el-button>
              <el-dropdown v-if="isCurrentUserResource(tutorial)" @command="(cmd: string) => $emit('action', cmd, tutorial)">
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
        <div class="list-table tutorial-list">
          <div class="list-header">
            <div class="list-cell name-cell">标题</div>
            <div class="list-cell">描述</div>
            <div class="list-cell">分类</div>
            <div class="list-cell">状态</div>
            <div class="list-cell">类型</div>
            <div class="list-cell actions-cell">操作</div>
          </div>
          <div v-for="tutorial in tutorialList" :key="tutorial.id" class="list-row">
            <div class="list-cell name-cell">
              <div class="row-icon tutorial-icon">
                <el-icon :size="18"><Reading /></el-icon>
              </div>
              <span class="row-name">
                {{ tutorial.title }}
                <el-tag v-if="isCurrentUserResource(tutorial)" size="small" type="primary" class="my-tag">我的</el-tag>
              </span>
            </div>
            <div class="list-cell">{{ tutorial.description }}</div>
            <div class="list-cell">
              <el-tag size="small" type="info">{{ tutorial.category }}</el-tag>
            </div>
            <div class="list-cell">
              <el-tag size="small" :type="getStatusType(tutorial.status)">{{ getStatusText(tutorial.status) }}</el-tag>
            </div>
            <div class="list-cell">
              <el-tag v-if="tutorial.contentType === 'video'" size="small" type="warning">视频</el-tag>
              <el-tag v-else size="small" type="info">文本</el-tag>
              <el-tag v-if="tutorial.zipFileUrl" size="small" type="success">含附件</el-tag>
            </div>
            <div class="list-cell actions-cell">
              <el-button v-if="isCurrentUserResource(tutorial)" link type="primary" size="small" @click="$emit('publish-version', 'tutorial', tutorial)">发布版本</el-button>
              <el-dropdown v-if="isCurrentUserResource(tutorial)" @command="(cmd: string) => $emit('action', cmd, tutorial)">
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
      <el-empty v-if="tutorialList.length === 0 && !loading" description="暂无教程" />
      <div v-if="loading" class="loading-state">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>
    </div>
    <div v-if="!loading && tutorialList.length > 0" class="pagination-wrapper">
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
import { Reading, Loading, MoreFilled } from '@element-plus/icons-vue'
import { useResourceUtils } from '@/composables/useResourceCenter'

const { getStatusType, getStatusText } = useResourceUtils()

defineProps<{
  tutorialList: any[]
  loading: boolean
  viewMode: 'card' | 'list'
  pagination: { page: number; size: number }
  total: number
  isCurrentUserResource: (resource: any) => boolean
}>()

defineEmits<{
  'publish-version': [type: string, resource: any]
  'action': [command: string, tutorial: any]
  'page-change': []
}>()
</script>
