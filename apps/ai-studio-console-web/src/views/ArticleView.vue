<template>
  <div class="article-view" v-loading="loading">
    <section class="view-hero">
      <div>
        <div class="eyebrow">Console / Article Preview</div>
        <h1>{{ article?.title || '文章详情' }}</h1>
        <el-breadcrumb class="folder-breadcrumb" separator="/">
          <el-breadcrumb-item>
            <button class="breadcrumb-button" @click="goFolder(null)">全部文章</button>
          </el-breadcrumb-item>
          <template v-if="article && !article.folderId">
            <el-breadcrumb-item>
              <button class="breadcrumb-button current" @click="goFolder(-1)">未分类</button>
            </el-breadcrumb-item>
          </template>
          <template v-else>
            <el-breadcrumb-item v-for="folder in folderPath" :key="folder.id">
              <button class="breadcrumb-button" :class="{ current: folder.id === article?.folderId }" @click="goFolder(folder.id)">
                {{ folder.folderName }}
              </button>
            </el-breadcrumb-item>
          </template>
        </el-breadcrumb>
      </div>
    </section>

    <section v-if="article" class="view-layout">
      <main class="article-document">
        <img v-if="article.coverImage" class="cover-image" :src="article.coverImage" alt="文章封面" />
        <div class="article-meta">
          <el-tag :type="getStatusType(article.status)" effect="light">{{ getStatusText(article.status) }}</el-tag>
          <span>{{ formatTime(article.publishedAt || article.createdAt) }}</span>
          <span>阅读 {{ article.viewsCount || 0 }}</span>
          <span>点赞 {{ article.likesCount || 0 }}</span>
          <span>收藏 {{ article.favoriteCount || 0 }}</span>
        </div>
        <p v-if="article.summary" class="summary">{{ article.summary }}</p>
        <article class="article-content" v-html="article.content" />
      </main>

      <aside class="article-side">
        <div class="side-block">
          <div class="side-title">归属文件夹</div>
          <button class="folder-chip" @click="goFolder(article.folderId || -1)">
            {{ article.folderId ? currentFolderName : '未分类' }}
          </button>
        </div>
        <div class="side-block">
          <div class="side-title">文章数据</div>
          <div class="metric-grid">
            <div>
              <strong>{{ article.viewsCount || 0 }}</strong>
              <span>阅读</span>
            </div>
            <div>
              <strong>{{ article.likesCount || 0 }}</strong>
              <span>点赞</span>
            </div>
            <div>
              <strong>{{ article.favoriteCount || 0 }}</strong>
              <span>收藏</span>
            </div>
            <div>
              <strong>{{ article.commentsCount || 0 }}</strong>
              <span>评论</span>
            </div>
          </div>
        </div>
      </aside>
    </section>

    <div class="floating-actions">
      <button class="fab secondary" title="返回文章列表" @click="goArticleList">
        <el-icon><ArrowLeft /></el-icon>
      </button>
      <button v-if="article" class="fab primary" title="编辑文章" @click="goEdit">
        <el-icon><EditPen /></el-icon>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, EditPen } from '@element-plus/icons-vue'
import { articleApi, type ArticleFolder } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const article = ref<any>(null)
const folders = ref<ArticleFolder[]>([])

const folderPath = computed<ArticleFolder[]>(() => {
  if (!article.value?.folderId) return []
  const path: ArticleFolder[] = []
  let current = folders.value.find(folder => folder.id === article.value.folderId) || null
  while (current) {
    path.unshift(current)
    current = folders.value.find(folder => folder.id === current?.parentId) || null
  }
  return path
})

const currentFolderName = computed(() => folderPath.value[folderPath.value.length - 1]?.folderName || '当前文件夹')

function getStatusText(status: number) {
  return ({ 0: '草稿', 1: '已发布', 2: '已下架' } as Record<number, string>)[status] || '未知'
}

function getStatusType(status: number) {
  return ({ 0: 'info', 1: 'success', 2: 'danger' } as Record<number, string>)[status] || ''
}

function formatTime(time?: string) {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

function goFolder(folderId: number | null) {
  router.push({ name: 'Article', query: folderId == null ? {} : { folderId } })
}

function goArticleList() {
  const folderId = article.value?.folderId ?? route.query.folderId
  router.push({ name: 'Article', query: folderId ? { folderId } : {} })
}

function goEdit() {
  router.push({ name: 'ArticleEdit', params: { id: article.value.id }, query: article.value.folderId ? { folderId: article.value.folderId } : {} })
}

async function loadPage() {
  loading.value = true
  try {
    const [articleDetail, folderList] = await Promise.all([
      articleApi.detail(Number(route.params.id)),
      articleApi.folders(),
    ])
    article.value = articleDetail
    folders.value = folderList
  } finally {
    loading.value = false
  }
}

onMounted(loadPage)
</script>

<style scoped>
.article-view {
  min-height: calc(100vh - 84px);
}

.view-hero,
.article-document,
.article-side {
  background: hsl(var(--card));
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
}

.view-hero {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 20px 22px;
  margin-bottom: 14px;
}

.eyebrow {
  font-size: 12px;
  color: hsl(var(--primary));
  font-weight: 700;
  letter-spacing: 0;
}

.view-hero h1 {
  margin: 6px 0 8px;
  font-size: 24px;
  line-height: 1.25;
}

.folder-breadcrumb {
  font-size: 13px;
}

.breadcrumb-button,
.folder-chip {
  border: 0;
  background: transparent;
  cursor: pointer;
  color: inherit;
  font: inherit;
}

.breadcrumb-button {
  padding: 0;
  color: hsl(var(--muted-foreground));
}

.breadcrumb-button:hover,
.breadcrumb-button.current {
  color: hsl(var(--primary));
}

.view-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 280px;
  gap: 14px;
  align-items: start;
}

.article-document {
  padding: 24px;
  min-width: 0;
}

.cover-image {
  width: 100%;
  max-height: 360px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 18px;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  color: hsl(var(--muted-foreground));
  font-size: 13px;
}

.summary {
  margin: 18px 0;
  padding: 14px 16px;
  border-radius: 8px;
  background: hsl(var(--secondary));
  color: hsl(var(--muted-foreground));
  line-height: 1.8;
}

.article-content {
  line-height: 1.9;
  font-size: 15px;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 8px;
}

.article-content :deep(pre) {
  padding: 14px;
  background: hsl(var(--secondary));
  border-radius: 8px;
  overflow: auto;
}

.article-content :deep(blockquote) {
  margin: 14px 0;
  padding-left: 14px;
  border-left: 3px solid hsl(var(--border));
  color: hsl(var(--muted-foreground));
}

.article-side {
  position: sticky;
  top: 84px;
  padding: 14px;
}

.side-block + .side-block {
  margin-top: 16px;
}

.side-title {
  margin-bottom: 10px;
  font-weight: 700;
}

.folder-chip {
  width: 100%;
  min-height: 36px;
  border-radius: 8px;
  background: hsl(var(--secondary));
  color: hsl(var(--primary));
}

.metric-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.metric-grid div {
  padding: 12px;
  border-radius: 8px;
  background: hsl(var(--secondary));
}

.metric-grid strong {
  display: block;
  font-size: 20px;
}

.metric-grid span {
  color: hsl(var(--muted-foreground));
  font-size: 12px;
}

.floating-actions {
  position: fixed;
  right: 28px;
  top: calc(50vh + 30px);
  z-index: 80;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 0;
  background: transparent;
  border: none;
  box-shadow: none;
}

.fab {
  width: 44px;
  height: 44px;
  border: 0;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font: inherit;
  font-size: 14px;
  font-weight: 700;
  transition: transform 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.fab:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 22px rgba(15, 23, 42, 0.18);
}

.fab.primary {
  color: white;
  background: hsl(var(--primary));
}

.fab.secondary {
  color: hsl(var(--foreground));
  background: hsl(var(--secondary));
}

@media (max-width: 900px) {
  .view-hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .view-layout {
    grid-template-columns: 1fr;
  }

  .article-side {
    position: static;
  }

  .floating-actions {
    top: auto;
    right: 16px;
    bottom: 16px;
    transform: none;
    flex-direction: row;
  }
}
</style>
