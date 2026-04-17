<template>
  <div class="resource-card card-hover-shadow" @click="handleClick">
    <!-- 封面图 -->
    <div v-if="resource.photo" class="card-cover">
      <img :src="resource.photo" :alt="resource.name" class="cover-image" />
    </div>
    <div v-else class="card-cover default-cover">
      <Icon :name="resourceIcon" class="cover-icon" />
    </div>
    <!-- 内容区 -->
    <div class="card-content">
      <h3 class="card-title text-overflow-2">{{ resource.name }}</h3>
      <p v-if="resource.introduction" class="card-intro text-overflow-2">{{ resource.introduction }}</p>
      <!-- 分类标签 -->
      <div class="card-meta">
        <el-tag v-if="resource.categoryName" size="small" type="info">
          {{ resource.categoryName }}
        </el-tag>
        <span class="publish-time">{{ formatTime(resource.publishTime || resource.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Resource } from '~~/shared/types/resource'

const props = defineProps<{
  resource: Resource
  type?: 'skill' | 'plugin' | 'tutorial'
}>()

const resourceType = computed(() => props.type || props.resource.type || 'skill')

const resourceIcon = computed(() => {
  switch (resourceType.value) {
    case 'skill':
      return 'material-symbols:smart-toy'
    case 'plugin':
      return 'material-symbols:extension'
    case 'tutorial':
      return 'material-symbols:menu-book'
    default:
      return 'material-symbols:folder'
  }
})

function handleClick() {
  navigateTo(`/resources/${resourceType.value}/${props.resource.id}`)
}

function formatTime(time: string) {
  if (!time)
    return ''
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.resource-card {
  @apply bg-white rounded-lg overflow-hidden cursor-pointer transition-all duration-300;
}

.card-cover {
  @apply w-full h-36 overflow-hidden bg-gray-100 flex items-center justify-center;
}

.cover-image {
  @apply w-full h-full object-cover;
}

.default-cover {
  @apply bg-gradient-to-br from-primary-faint to-primary-light;
}

.cover-icon {
  @apply text-5xl text-primary;
}

.card-content {
  @apply p-4;
}

.card-title {
  @apply text-base font-medium text-gray-800 mb-2;
}

.card-intro {
  @apply text-sm text-gray-500 mb-3;
}

.card-meta {
  @apply flex items-center justify-between;
}

.publish-time {
  @apply text-xs text-gray-400;
}
</style>
