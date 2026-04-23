<template>
  <div>
    <div class="resource-list" :class="`view-mode-${viewMode}`">
      <template v-if="viewMode === 'card'">
        <el-card v-for="skill in skillList" :key="skill.id" class="resource-card skill-card" shadow="hover">
          <div class="card-body">
            <div class="card-main">
              <div class="resource-icon skill-icon">
                <el-icon :size="24"><Box /></el-icon>
              </div>
              <div class="resource-info">
                <div class="resource-header-row">
                  <h3 class="resource-name">
                    {{ skill.name }}
                    <el-tag v-if="isCurrentUserResource(skill)" size="small" type="primary" class="my-tag">我的</el-tag>
                  </h3>
                </div>
                <p class="resource-desc">{{ skill.description }}</p>
                <div class="resource-meta">
                  <el-tag size="small" type="info">{{ skill.category }}</el-tag>
                  <el-tag size="small" :type="getStatusType(skill.status)">{{ getStatusText(skill.status) }}</el-tag>
                  <span class="version-text">v{{ skill.latest_version || skill.version }}</span>
                </div>
              </div>
            </div>
            <div class="card-actions">
              <el-button link type="primary" @click="$emit('view-versions', skill)">版本</el-button>
              <el-button v-if="isCurrentUserResource(skill)" link type="primary" @click="$emit('publish-version', 'skill', skill)">发布版本</el-button>
              <el-button link type="primary" @click="$emit('download', skill)">下载</el-button>
              <el-dropdown v-if="isCurrentUserResource(skill)" @command="(cmd: string) => $emit('action', cmd, skill)">
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
        <div class="list-table skill-list">
          <div class="list-header">
            <div class="list-cell name-cell">名称</div>
            <div class="list-cell">描述</div>
            <div class="list-cell">分类</div>
            <div class="list-cell">状态</div>
            <div class="list-cell">版本</div>
            <div class="list-cell actions-cell">操作</div>
          </div>
          <div v-for="skill in skillList" :key="skill.id" class="list-row">
            <div class="list-cell name-cell">
              <div class="row-icon skill-icon">
                <el-icon :size="18"><Box /></el-icon>
              </div>
              <span class="row-name">
                {{ skill.name }}
                <el-tag v-if="isCurrentUserResource(skill)" size="small" type="primary" class="my-tag">我的</el-tag>
              </span>
            </div>
            <div class="list-cell">{{ skill.description }}</div>
            <div class="list-cell">
              <el-tag size="small" type="info">{{ skill.category }}</el-tag>
            </div>
            <div class="list-cell">
              <el-tag size="small" :type="getStatusType(skill.status)">{{ getStatusText(skill.status) }}</el-tag>
            </div>
            <div class="list-cell">v{{ skill.latest_version || skill.version }}</div>
            <div class="list-cell actions-cell">
              <el-button link type="primary" size="small" @click="$emit('view-versions', skill)">版本</el-button>
              <el-button v-if="isCurrentUserResource(skill)" link type="primary" size="small" @click="$emit('publish-version', 'skill', skill)">发布版本</el-button>
              <el-button link type="primary" size="small" @click="$emit('download', skill)">下载</el-button>
              <el-dropdown v-if="isCurrentUserResource(skill)" @command="(cmd: string) => $emit('action', cmd, skill)">
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
      <el-empty v-if="skillList.length === 0 && !loading" description="暂无 Skill" />
      <div v-if="loading" class="loading-state">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      </div>
    </div>
    <div v-if="!loading && skillList.length > 0" class="pagination-wrapper">
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
import { Box, Loading, MoreFilled } from '@element-plus/icons-vue'
import { useResourceUtils } from '@/composables/useResourceCenter'

const { getStatusType, getStatusText } = useResourceUtils()

defineProps<{
  skillList: any[]
  loading: boolean
  viewMode: 'card' | 'list'
  pagination: { page: number; size: number }
  total: number
  isCurrentUserResource: (resource: any) => boolean
}>()

defineEmits<{
  'view-versions': [skill: any]
  'publish-version': [type: string, resource: any]
  'download': [skill: any]
  'action': [command: string, skill: any]
  'page-change': []
}>()
</script>
