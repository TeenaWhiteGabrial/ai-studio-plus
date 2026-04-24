<template>
  <article class="article-card csdn-card csdn-hover-card" @click="handleClick">
    <div class="article-main">
      <header class="article-head">
        <h3 class="article-title text-overflow-2">{{ article.title }}</h3>
        <span v-if="firstTag" class="csdn-tag">{{ firstTag }}</span>
      </header>

      <p v-if="articleSummary" class="article-summary text-overflow-3">{{ articleSummary }}</p>

      <footer class="article-footer">
        <div class="author-box">
          <img v-if="article.authorAvatar" :src="article.authorAvatar" alt="avatar" class="author-avatar">
          <span v-else class="author-avatar fallback">{{ authorInitial }}</span>
          <span class="author-name">{{ article.authorName || '匿名作者' }}</span>
          <span class="dot">·</span>
          <span class="time">{{ formatTime(timeValue) }}</span>
        </div>

        <div class="stats">
          <span><Icon name="material-symbols:visibility-outline" size="16" /> {{ viewCount }}</span>
          <span><Icon name="material-symbols:chat-bubble-outline" size="16" /> {{ commentCount }}</span>
          <span><Icon name="material-symbols:thumb-up-outline" size="16" /> {{ likeCount }}</span>
        </div>
      </footer>
    </div>

    <div v-if="article.coverImage" class="article-cover">
      <img :src="article.coverImage" :alt="article.title">
    </div>
  </article>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'

const props = defineProps<{
  article: Article
}>()

const emit = defineEmits<{
  click: [article: Article]
}>()

const firstTag = computed(() => {
  const tags = props.article.tags || []
  return Array.isArray(tags) ? tags[0] : ''
})

const viewCount = computed(() => props.article.viewCount ?? props.article.viewsCount ?? 0)
const commentCount = computed(() => props.article.commentCount ?? props.article.commentsCount ?? 0)
const likeCount = computed(() => props.article.likeCount ?? props.article.likesCount ?? 0)

const articleSummary = computed(() => {
  return props.article.summary || stripHtml(props.article.content || '')
})

const timeValue = computed(() => {
  return props.article.publishTime || props.article.publishedAt || props.article.createTime || props.article.createdAt || ''
})

const authorInitial = computed(() => {
  const name = props.article.authorName || 'A'
  return name.slice(0, 1).toUpperCase()
})

function stripHtml(value: string) {
  return value.replace(/<[^>]+>/g, '').trim()
}

function handleClick() {
  emit('click', props.article)
  navigateTo(`/community/article/${props.article.id}`)
}

function formatTime(value: string) {
  if (!value) {
    return '刚刚'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return '刚刚'
  }
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
.article-card {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 140px;
  gap: 14px;
  padding: 14px;
  cursor: pointer;
}

.article-main {
  min-width: 0;
}

.article-head {
  display: flex;
  align-items: flex-start;
  gap: 8px;
}

.article-title {
  margin: 0;
  font-size: 17px;
  line-height: 1.45;
  color: var(--csdn-text);
}

.article-summary {
  margin: 8px 0 12px;
  color: var(--csdn-subtext);
  line-height: 1.75;
  font-size: 14px;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.author-box {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--csdn-muted);
  font-size: 13px;
}

.author-avatar {
  width: 22px;
  height: 22px;
  border-radius: 50%;
  object-fit: cover;
}

.author-avatar.fallback {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #e8f0ff;
  color: #2457c5;
  font-size: 12px;
}

.author-name {
  color: var(--csdn-subtext);
}

.dot {
  color: #c0c3cc;
}

.stats {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  color: var(--csdn-muted);
  font-size: 13px;
}

.stats span {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.article-cover {
  width: 140px;
  height: 94px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #edf0f5;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

@media (max-width: 768px) {
  .article-card {
    grid-template-columns: minmax(0, 1fr);
  }

  .article-cover {
    width: 100%;
    height: 160px;
  }
}
</style>
