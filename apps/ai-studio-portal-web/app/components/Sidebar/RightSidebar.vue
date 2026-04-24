<template>
  <div class="right-wrap">
    <section class="csdn-card panel">
      <div class="panel-head">
        <h3 class="panel-title">文章热榜</h3>
        <NuxtLink to="/community?type=article&sort=hot" class="csdn-link">更多</NuxtLink>
      </div>
      <div v-if="loadingArticles" class="csdn-empty">加载中...</div>
      <ol v-else class="rank-list">
        <li
          v-for="(item, index) in hotArticles"
          :key="item.id"
          class="rank-item"
          @click="navigateTo(`/community/article/${item.id}`)"
        >
          <span class="rank-index" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <span class="rank-title text-overflow-2">{{ item.title }}</span>
        </li>
      </ol>
    </section>

    <section class="csdn-card panel">
      <div class="panel-head">
        <h3 class="panel-title">问答热榜</h3>
        <NuxtLink to="/community?type=question&sort=hot" class="csdn-link">更多</NuxtLink>
      </div>
      <div v-if="loadingQuestions" class="csdn-empty">加载中...</div>
      <ol v-else class="rank-list">
        <li
          v-for="(item, index) in hotQuestions"
          :key="item.id"
          class="rank-item"
          @click="navigateTo(`/community/question/${item.id}`)"
        >
          <span class="rank-index" :class="{ top: index < 3 }">{{ index + 1 }}</span>
          <span class="rank-title text-overflow-2">{{ item.title }}</span>
        </li>
      </ol>
    </section>

    <section class="csdn-card panel">
      <div class="panel-head">
        <h3 class="panel-title">站点公告</h3>
      </div>
      <div v-if="loadingAnnouncements" class="csdn-empty">加载中...</div>
      <ul v-else-if="announcements.length" class="notice-list">
        <li v-for="(item, index) in announcements" :key="index" class="notice-item text-overflow-2">
          {{ item.title || item.content || item }}
        </li>
      </ul>
      <div v-else class="csdn-empty">暂无公告</div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'
import type { Question } from '~~/shared/types/question'

const { getArticleList } = useArticle()
const { getQuestionList } = useQuestion()

const hotArticles = ref<Article[]>([])
const hotQuestions = ref<Question[]>([])
const announcements = ref<any[]>([])

const loadingArticles = ref(false)
const loadingQuestions = ref(false)
const loadingAnnouncements = ref(false)

async function loadHotArticles() {
  loadingArticles.value = true
  try {
    const res = await getArticleList({ sort: 'hot', page: 1, pageSize: 10 })
    hotArticles.value = res.records || []
  }
  finally {
    loadingArticles.value = false
  }
}

async function loadHotQuestions() {
  loadingQuestions.value = true
  try {
    const res = await getQuestionList({ sort: 'hot', page: 1, pageSize: 10 })
    hotQuestions.value = res.records || []
  }
  finally {
    loadingQuestions.value = false
  }
}

async function loadAnnouncements() {
  loadingAnnouncements.value = true
  try {
    const res = await useSimpleFetch<any>('/portal/open/public/announcement', { noToken: true })
    const data = (res && (res as any).data) || []
    announcements.value = Array.isArray(data) ? data : []
  }
  finally {
    loadingAnnouncements.value = false
  }
}

onMounted(() => {
  loadHotArticles()
  loadHotQuestions()
  loadAnnouncements()
})
</script>

<style scoped>
.right-wrap {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.panel {
  padding: 14px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.panel-title {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
}

.rank-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.rank-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 8px 0;
  cursor: pointer;
}

.rank-item + .rank-item {
  border-top: 1px dashed var(--csdn-line);
}

.rank-index {
  flex: 0 0 22px;
  height: 22px;
  line-height: 22px;
  border-radius: 6px;
  text-align: center;
  font-size: 12px;
  color: #7a7c88;
  background: #f0f1f5;
}

.rank-index.top {
  color: #fff;
  background: linear-gradient(135deg, #ff6a3d, #ff8f2b);
}

.rank-title {
  color: var(--csdn-subtext);
  font-size: 13px;
  line-height: 1.6;
}

.rank-item:hover .rank-title {
  color: var(--csdn-primary);
}

.notice-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.notice-item {
  font-size: 13px;
  color: var(--csdn-subtext);
  line-height: 1.7;
  padding: 6px 0;
}

.notice-item + .notice-item {
  border-top: 1px dashed var(--csdn-line);
}
</style>
