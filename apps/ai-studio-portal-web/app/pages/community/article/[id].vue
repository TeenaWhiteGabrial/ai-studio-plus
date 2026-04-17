<template>
  <div class="article-detail-page">
    <!-- 主内容区 -->
    <div class="main-area">
      <div v-if="loading" class="text-center py-12">
        加载中...
      </div>
      <div v-else-if="!article" class="text-center py-12">
        文章不存在
      </div>
      <template v-else>
        <!-- 文章头部 -->
        <article class="article-container bg-white rounded-lg p-6">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <div class="author-info">
              <el-avatar v-if="article.authorAvatar" :src="article.authorAvatar" :size="32" />
              <span class="author-name">{{ article.authorName }}</span>
            </div>
            <div class="meta-info">
              <span>{{ formatTime(article.publishTime || article.createTime) }}</span>
              <span class="split">|</span>
              <span>{{ article.viewCount }} 阅读</span>
              <span class="split">|</span>
              <span>{{ article.likeCount }} 点赞</span>
            </div>
          </div>
          <!-- 标签 -->
          <div v-if="article.tags && article.tags.length" class="article-tags">
            <el-tag v-for="tag in article.tags" :key="tag" size="small" class="mr-2">
              {{ tag }}
            </el-tag>
          </div>
          <!-- 封面图 -->
          <div v-if="article.coverImage" class="article-cover">
            <img :src="article.coverImage" :alt="article.title" />
          </div>
          <!-- 文章内容 -->
          <div class="article-content" v-html="article.content"></div>
          <!-- 操作栏 -->
          <div class="article-actions">
            <el-button :type="isLiked ? 'primary' : 'default'" @click="handleLike">
              <Icon :name="isLiked ? 'material-symbols:favorite' : 'material-symbols:favorite-outline'" class="mr-1" />
              {{ article.likeCount }} 点赞
            </el-button>
            <el-button :type="isFavorited ? 'primary' : 'default'" @click="handleFavorite">
              <Icon :name="isFavorited ? 'material-symbols:bookmark' : 'material-symbols:bookmark-outline'" class="mr-1" />
              {{ isFavorited ? '已收藏' : '收藏' }}
            </el-button>
            <el-button @click="handleShare">
              <Icon name="material-symbols:share" class="mr-1" />
              分享
            </el-button>
          </div>
        </article>

        <!-- 评论区 -->
        <div class="comment-section bg-white rounded-lg p-6 mt-6">
          <CommentForm
            target-type="article"
            :target-id="article.id"
            @success="handleCommentSuccess"
          />
          <CommentList
            target-type="article"
            :target-id="article.id"
            @comment-change="handleCommentChange"
          />
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'

const route = useRoute()
const { getArticleDetail, likeArticle } = useArticle()
const { createFavorite, deleteFavorite, checkFavorite } = useCommunity()
const { createBrowseHistory } = useCommunity()
const authStore = useAuthStore()

const article = ref<Article | null>(null)
const loading = ref(true)
const isLiked = ref(false)
const isFavorited = ref(false)

async function loadArticle() {
  loading.value = true
  const id = route.params.id as string
  try {
    const res = await getArticleDetail(id)
    article.value = res
    // 创建浏览记录
    if (authStore.token) {
      createBrowseHistory({ targetType: 'article', targetId: id })
    }
  }
  catch (err) {
    console.error('加载文章失败:', err)
  }
  finally {
    loading.value = false
  }
}

async function checkIsFavorited() {
  if (!authStore.token || !article.value)
    return
  try {
    const res = await checkFavorite('article', article.value.id)
    isFavorited.value = res.isFavorited
  }
  catch (err) {
    console.error('检查收藏状态失败:', err)
  }
}

async function handleLike() {
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  if (!article.value)
    return
  try {
    await likeArticle(article.value.id)
    isLiked.value = !isLiked.value
    article.value.likeCount += isLiked.value ? 1 : -1
  }
  catch (err) {
    ElMessage.error('点赞失败')
  }
}

async function handleFavorite() {
  if (!authStore.token) {
    ElMessage.warning('请先登录')
    return
  }
  if (!article.value)
    return
  try {
    if (isFavorited.value) {
      await deleteFavorite('article', article.value.id)
      isFavorited.value = false
      ElMessage.success('已取消收藏')
    }
    else {
      await createFavorite({ targetType: 'article', targetId: article.value.id })
      isFavorited.value = true
      ElMessage.success('收藏成功')
    }
  }
  catch (err) {
    ElMessage.error('操作失败')
  }
}

function handleShare() {
  if (article.value) {
    navigator.clipboard.writeText(window.location.href)
    ElMessage.success('链接已复制到剪贴板')
  }
}

function handleCommentSuccess() {
  if (article.value) {
    article.value.commentCount++
  }
}

function handleCommentChange() {
  if (article.value) {
    article.value.commentCount++
  }
}

function formatTime(time: string) {
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadArticle().then(() => {
    checkIsFavorited()
  })
})
</script>

<style scoped>
.article-detail-page {
  @apply flex gap-6;
}

.main-area {
  @apply flex-1 min-w-0;
}

.article-container {
  @apply mb-6;
}

.article-title {
  @apply text-2xl font-bold text-gray-800 mb-4;
}

.article-meta {
  @apply flex items-center justify-between mb-4 pb-4 border-b border-gray-100;
}

.author-info {
  @apply flex items-center gap-2;
}

.author-name {
  @apply font-medium text-gray-700;
}

.meta-info {
  @apply flex items-center gap-2 text-sm text-gray-400;
}

.split {
  @apply text-gray-300;
}

.article-tags {
  @apply mb-4;
}

.article-cover {
  @apply mb-6 rounded-lg overflow-hidden;
}

.article-cover img {
  @apply w-full;
}

.article-content {
  @apply text-gray-700 leading-relaxed;
}

.article-content :deep(p) {
  @apply mb-4;
}

.article-content :deep(pre) {
  @apply bg-gray-100 p-4 rounded-lg overflow-x-auto mb-4;
}

.article-content :deep(img) {
  @apply max-w-full rounded-lg;
}

.article-actions {
  @apply flex items-center gap-4 mt-6 pt-6 border-t border-gray-100;
}
</style>
