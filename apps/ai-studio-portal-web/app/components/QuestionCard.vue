<template>
  <article class="question-card csdn-card csdn-hover-card" @click="handleClick">
    <header class="question-head">
      <h3 class="question-title text-overflow-2">{{ question.title }}</h3>
      <span class="status-tag" :class="{ resolved: resolved }">{{ resolved ? '已解决' : '待回答' }}</span>
    </header>

    <p v-if="summaryText" class="question-summary text-overflow-3">{{ summaryText }}</p>

    <footer class="question-footer">
      <div class="meta-left">
        <span class="author">{{ question.authorName || '匿名用户' }}</span>
        <span class="dot">·</span>
        <span>{{ formatTime(timeValue) }}</span>
        <span v-if="tagText" class="question-tag">#{{ tagText }}</span>
      </div>

      <div class="meta-right">
        <span><Icon name="material-symbols:visibility-outline" size="16" /> {{ viewCount }}</span>
        <span><Icon name="material-symbols:chat-bubble-outline" size="16" /> {{ answerCount }}</span>
      </div>
    </footer>
  </article>
</template>

<script setup lang="ts">
import type { Question } from '~~/shared/types/question'

const props = defineProps<{
  question: Question
}>()

const resolved = computed(() => {
  return Boolean(props.question.isResolved || props.question.hasBestAnswer)
})

const summaryText = computed(() => stripHtml(props.question.content || ''))
const viewCount = computed(() => props.question.viewCount ?? props.question.viewsCount ?? 0)
const answerCount = computed(() => props.question.answerCount ?? props.question.answersCount ?? 0)
const timeValue = computed(() => props.question.createTime || props.question.createdAt || '')

const tagText = computed(() => {
  if (Array.isArray(props.question.tags)) {
    return props.question.tags[0] || ''
  }
  if (typeof props.question.tags === 'string') {
    return props.question.tags.split(',')[0] || ''
  }
  return ''
})

function stripHtml(value: string) {
  return value.replace(/<[^>]+>/g, '').trim()
}

function handleClick() {
  navigateTo(`/community/question/${props.question.id}`)
}

function formatTime(value: string) {
  if (!value) return '刚刚'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '刚刚'
  const diff = Date.now() - date.getTime()
  const minutes = Math.floor(diff / (1000 * 60))
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours} 小时前`
  const days = Math.floor(hours / 24)
  if (days < 7) return `${days} 天前`
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.question-card {
  padding: 14px;
  cursor: pointer;
}

.question-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.question-title {
  margin: 0;
  font-size: 17px;
  line-height: 1.45;
  color: var(--csdn-text);
}

.status-tag {
  flex-shrink: 0;
  border-radius: 999px;
  padding: 2px 10px;
  font-size: 12px;
  color: #b26a00;
  background: #fff6e6;
}

.status-tag.resolved {
  color: #0f7a3f;
  background: #e9f9f0;
}

.question-summary {
  margin: 8px 0 12px;
  color: var(--csdn-subtext);
  line-height: 1.75;
  font-size: 14px;
}

.question-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.meta-left {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--csdn-muted);
  font-size: 13px;
}

.author {
  color: var(--csdn-subtext);
}

.dot {
  color: #c0c3cc;
}

.question-tag {
  color: var(--csdn-primary);
}

.meta-right {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: var(--csdn-muted);
  font-size: 13px;
}

.meta-right span {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}
</style>
