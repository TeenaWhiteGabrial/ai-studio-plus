<template>
  <div class="article-card card-hover-shadow" @click="handleClick">
    <!-- 封面图 -->
    <div v-if="article.coverImage" class="card-cover">
      <img :src="article.coverImage" :alt="article.title" class="cover-image" />
    </div>
    <!-- 内容区 -->
    <div class="card-content">
      <h3 class="card-title text-overflow-2">{{ article.title }}</h3>
      <p v-if="article.summary" class="card-summary text-overflow-2">{{ article.summary }}</p>
      <!-- 标签 -->
      <div v-if="article.tags && article.tags.length" class="card-tags">
        <el-tag v-for="tag in article.tags.slice(0, 3)" :key="tag" size="small" type="info">
          {{ tag }}
        </el-tag>
      </div>
      <!-- 底部信息 -->
      <div class="card-footer">
        <div class="author-info">
          <el-avatar v-if="article.authorAvatar" :src="article.authorAvatar" :size="20" />
          <span class="author-name">{{ article.authorName }}</span>
        </div>
        <div class="stats">
          <span class="stat-item">
            <Icon name="material-symbols:visibility-outline" />
            {{ article.viewCount }}
          </span>
          <span class="stat-item">
            <Icon name="material-symbols:chat-bubble-outline" />
            {{ article.commentCount }}
          </span>
          <span class="stat-item">
            <Icon name="material-symbols:favorite-outline" />
            {{ article.likeCount }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'

const props = defineProps<{
  article: Article
}>()

const emit = defineEmits<{
  click: [article: Article]
}>()

function handleClick() {
  emit('click', props.article)
  navigateTo(`/community/article/${props.article.id}`)
}
</script>

<style scoped>
.article-card {
  @apply bg-white rounded-lg overflow-hidden cursor-pointer transition-all duration-300;
}

.card-cover {
  @apply w-full h-40 overflow-hidden;
}

.cover-image {
  @apply w-full h-full object-cover;
}

.card-content {
  @apply p-4;
}

.card-title {
  @apply text-base font-medium text-gray-800 mb-2;
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

.stats {
  @apply flex items-center gap-3;
}

.stat-item {
  @apply flex items-center gap-1;
}
</style>
