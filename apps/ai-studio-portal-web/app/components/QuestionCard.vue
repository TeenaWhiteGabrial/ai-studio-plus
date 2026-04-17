<template>
  <div class="question-card card-hover-shadow" @click="handleClick">
    <div class="card-content">
      <div class="card-header">
        <h3 class="card-title text-overflow-2">{{ question.title }}</h3>
        <el-tag v-if="question.isResolved" size="small" type="success">已解决</el-tag>
      </div>
      <p v-if="question.content" class="card-summary text-overflow-2">{{ question.content }}</p>
      <!-- 标签 -->
      <div v-if="question.tags && question.tags.length" class="card-tags">
        <el-tag v-for="tag in question.tags.slice(0, 3)" :key="tag" size="small" type="info">
          {{ tag }}
        </el-tag>
      </div>
      <!-- 底部信息 -->
      <div class="card-footer">
        <div class="author-info">
          <el-avatar v-if="question.authorAvatar" :src="question.authorAvatar" :size="20" />
          <span class="author-name">{{ question.authorName }}</span>
          <span class="publish-time">{{ formatTime(question.createTime) }}</span>
        </div>
        <div class="stats">
          <span class="stat-item">
            <Icon name="material-symbols:visibility-outline" />
            {{ question.viewCount }}
          </span>
          <span class="stat-item">
            <Icon name="material-symbols:chat-bubble-outline" />
            {{ question.answerCount }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Question } from '~~/shared/types/question'

const props = defineProps<{
  question: Question
}>()

function handleClick() {
  navigateTo(`/community/question/${props.question.id}`)
}

function formatTime(time: string) {
  const date = new Date(time)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))

  if (days === 0) {
    const hours = Math.floor(diff / (1000 * 60 * 60))
    if (hours === 0) {
      const minutes = Math.floor(diff / (1000 * 60))
      return `${minutes} 分钟前`
    }
    return `${hours} 小时前`
  }
  if (days < 7) {
    return `${days} 天前`
  }
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.question-card {
  @apply bg-white rounded-lg overflow-hidden cursor-pointer transition-all duration-300;
}

.card-content {
  @apply p-4;
}

.card-header {
  @apply flex items-start justify-between gap-2 mb-2;
}

.card-title {
  @apply text-base font-medium text-gray-800 flex-1;
}

.card-summary {
  @apply text-sm text-gray-500 mb-3;
}

.card-tags {
  @apply flex flex-wrap gap-1 mb-3;
}

.card-footer {
  @apply flex items-center justify-between text-sm text-gray-400;
}

.author-info {
  @apply flex items-center gap-2;
}

.author-name {
  @apply text-gray-600;
}

.publish-time {
  @apply text-gray-400;
}

.stats {
  @apply flex items-center gap-3;
}

.stat-item {
  @apply flex items-center gap-1;
}
</style>
