<template>
  <div class="article-page">
    <div v-if="loading" class="csdn-card csdn-empty">内容加载中...</div>
    <div v-else-if="!article" class="csdn-card csdn-empty">文章不存在或已下线</div>

    <template v-else>
      <article class="csdn-card article-card">
        <header class="article-header">
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="meta-row">
            <div class="author-meta">
              <img v-if="article.authorAvatar" :src="article.authorAvatar" class="author-avatar" alt="avatar">
              <span v-else class="author-avatar fallback">{{ authorInitial }}</span>
              <span>{{ article.authorName || '匿名作者' }}</span>
              <span class="dot">·</span>
              <span>{{ formatTime(timeValue) }}</span>
            </div>

            <div class="stats-meta">
              <span><Icon name="material-symbols:visibility-outline" size="16" /> {{ viewCount }}</span>
              <span><Icon name="material-symbols:chat-bubble-outline" size="16" /> {{ commentCount }}</span>
              <span><Icon name="material-symbols:thumb-up-outline" size="16" /> {{ likeCount }}</span>
            </div>
          </div>

          <div v-if="tags.length" class="tag-row">
            <span v-for="tag in tags" :key="tag" class="tag-item"># {{ tag }}</span>
          </div>
        </header>

        <div v-if="article.coverImage" class="cover-wrap">
          <img :src="article.coverImage" :alt="article.title">
        </div>

        <div class="csdn-prose article-content" v-html="article.content" />

        <div class="action-bar">
          <el-button :type="isLiked ? 'primary' : 'default'" @click="handleLike">
            <Icon :name="isLiked ? 'material-symbols:thumb-up' : 'material-symbols:thumb-up-outline'" size="17" />
            <span>点赞 {{ likeCount }}</span>
          </el-button>
          <el-button :type="isFavorited ? 'primary' : 'default'" @click="handleFavorite">
            <Icon :name="isFavorited ? 'material-symbols:bookmark' : 'material-symbols:bookmark-outline'" size="17" />
            <span>{{ isFavorited ? '已收藏' : '收藏' }}</span>
          </el-button>
          <el-button @click="handleShare">
            <Icon name="material-symbols:share" size="17" />
            <span>分享</span>
          </el-button>
        </div>
      </article>

      <section class="csdn-card comment-card">
        <CommentForm
          target-type="article"
          :target-id="String(article.id)"
          @success="handleCommentSuccess"
        />
        <CommentList
          target-type="article"
          :target-id="String(article.id)"
          @comment-change="handleCommentSuccess"
        />
      </section>
    </template>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'

const route = useRoute()
const authStore = useAuthStore()
const { getArticleDetail, likeArticle, isArticleLiked } = useArticle()
const { createFavorite, deleteFavorite, checkFavorite, createBrowseHistory } = useCommunity()

const article = ref<Article | null>(null)
const loading = ref(true)
const isLiked = ref(false)
const isFavorited = ref(false)

const tags = computed(() => {
  const source = article.value?.tags || []
  return Array.isArray(source) ? source : String(source).split(',').filter(Boolean)
})

const viewCount = computed(() => article.value?.viewCount ?? article.value?.viewsCount ?? 0)
const commentCount = computed(() => article.value?.commentCount ?? article.value?.commentsCount ?? 0)
const likeCount = computed(() => article.value?.likeCount ?? article.value?.likesCount ?? 0)
const timeValue = computed(() => article.value?.publishTime || article.value?.publishedAt || article.value?.createTime || article.value?.createdAt || '')

const authorInitial = computed(() => {
  const name = article.value?.authorName || 'A'
  return name.slice(0, 1).toUpperCase()
})

async function loadArticle() {
  loading.value = true
  const id = route.params.id as string
  try {
    const detail = await getArticleDetail(id)
    article.value = detail

    if (authStore.token) {
      createBrowseHistory({ targetType: 'article', targetId: id })
      isLiked.value = await isArticleLiked(id)
      isFavorited.value = await checkFavorite('article', id)
    }
  } finally {
    loading.value = false
  }
}

async function handleLike() {
  if (!authStore.token) {
    goLoginPage()
    return
  }
  if (!article.value) return

  await likeArticle(String(article.value.id))
  isLiked.value = !isLiked.value
  if (article.value.likeCount !== undefined) {
    article.value.likeCount += isLiked.value ? 1 : -1
  } else if (article.value.likesCount !== undefined) {
    article.value.likesCount += isLiked.value ? 1 : -1
  }
}

async function handleFavorite() {
  if (!authStore.token) {
    goLoginPage()
    return
  }
  if (!article.value) return

  const id = String(article.value.id)
  if (isFavorited.value) {
    await deleteFavorite('article', id)
    isFavorited.value = false
    ElMessage.success('已取消收藏')
    return
  }

  await createFavorite({ targetType: 'article', targetId: id })
  isFavorited.value = true
  ElMessage.success('收藏成功')
}

async function handleShare() {
  await navigator.clipboard.writeText(window.location.href)
  ElMessage.success('链接已复制')
}

function handleCommentSuccess() {
  if (!article.value) return
  if (article.value.commentCount !== undefined) {
    article.value.commentCount += 1
  } else if (article.value.commentsCount !== undefined) {
    article.value.commentsCount += 1
  }
}

function formatTime(value: string) {
  if (!value) return '刚刚'
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return '刚刚'
  return date.toLocaleString('zh-CN')
}

onMounted(loadArticle)
</script>

<style scoped>
.article-page {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.article-card {
  padding: 20px 24px;
}

.article-header {
  padding-bottom: 12px;
  border-bottom: 1px solid var(--csdn-line);
}

.article-title {
  margin: 0;
  font-size: 30px;
  line-height: 1.35;
  color: var(--csdn-text);
}

.meta-row {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  flex-wrap: wrap;
}

.author-meta {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--csdn-muted);
  font-size: 14px;
}

.author-avatar {
  width: 28px;
  height: 28px;
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

.dot {
  color: #c0c3cc;
}

.stats-meta {
  display: inline-flex;
  align-items: center;
  gap: 14px;
  color: var(--csdn-muted);
  font-size: 13px;
}

.stats-meta span {
  display: inline-flex;
  align-items: center;
  gap: 2px;
}

.tag-row {
  margin-top: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  border-radius: 6px;
  background: #f2f7ff;
  color: var(--csdn-primary);
  font-size: 12px;
  padding: 3px 8px;
}

.cover-wrap {
  margin: 18px 0;
  border-radius: 12px;
  overflow: hidden;
}

.cover-wrap img {
  width: 100%;
  object-fit: cover;
}

.article-content {
  margin-top: 8px;
}

.action-bar {
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px solid var(--csdn-line);
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.comment-card {
  padding: 10px 24px 18px;
}

@media (max-width: 768px) {
  .article-card {
    padding: 16px;
  }

  .article-title {
    font-size: 24px;
  }

  .comment-card {
    padding: 8px 16px 14px;
  }
}
</style>
