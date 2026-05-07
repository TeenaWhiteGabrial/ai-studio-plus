<template>
  <div class="right-wrap">
    <section class="csdn-card panel">
      <div class="panel-head">
        <h3 class="panel-title">文章热榜</h3>
        <NuxtLink to="/community?sort=hot" class="csdn-link">更多</NuxtLink>
      </div>
      <div v-if="loadingArticles" class="csdn-empty compact-empty">加载中...</div>
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
        <h3 class="panel-title">资源入口</h3>
        <NuxtLink to="/resources" class="csdn-link">全部</NuxtLink>
      </div>
      <div class="resource-shortcuts">
        <button class="shortcut-item" @click="navigateTo('/resources?type=skill')">
          <Icon name="material-symbols:psychology-alt-outline" size="18" />
          <span>Skill</span>
        </button>
        <button class="shortcut-item" @click="navigateTo('/resources?type=plugin')">
          <Icon name="material-symbols:extension-outline" size="18" />
          <span>Plugin</span>
        </button>
      </div>
    </section>

    <section class="csdn-card panel">
      <div class="panel-head">
        <h3 class="panel-title">站点公告</h3>
      </div>
      <div v-if="loadingAnnouncements" class="csdn-empty compact-empty">加载中...</div>
      <ul v-else-if="announcements.length" class="notice-list">
        <li v-for="(item, index) in announcements" :key="index" class="notice-item text-overflow-2">
          {{ item.title || item.content || item }}
        </li>
      </ul>
      <div v-else class="csdn-empty compact-empty">暂无公告</div>
    </section>
  </div>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'

const { getArticleList } = useArticle()

const hotArticles = ref<Article[]>([])
const announcements = ref<any[]>([])

const loadingArticles = ref(false)
const loadingAnnouncements = ref(false)

async function loadHotArticles() {
  loadingArticles.value = true
  try {
    const res = await getArticleList({ sort: 'hot', page: 1, pageSize: 10 })
    hotArticles.value = res.records || []
  } finally {
    loadingArticles.value = false
  }
}

async function loadAnnouncements() {
  loadingAnnouncements.value = true
  try {
    const res = await useSimpleFetch<any>('/portal/open/public/announcement', { noToken: true })
    const data = (res && (res as any).data) || []
    announcements.value = Array.isArray(data) ? data : []
  } finally {
    loadingAnnouncements.value = false
  }
}

onMounted(() => {
  loadHotArticles()
  loadAnnouncements()
})
</script>

<style scoped>
.right-wrap {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.panel {
  padding: 12px;
}

.panel-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.panel-title {
  margin: 0;
  font-size: 14px;
  font-weight: 800;
  color: var(--portal-text);
}

.rank-list,
.notice-list {
  margin: 0;
  padding: 0;
  list-style: none;
}

.rank-item {
  display: grid;
  grid-template-columns: 22px minmax(0, 1fr);
  gap: 8px;
  padding: 8px 0;
  cursor: pointer;
}

.rank-item + .rank-item,
.notice-item + .notice-item {
  border-top: 1px dashed var(--csdn-line);
}

.rank-index {
  height: 22px;
  line-height: 22px;
  border-radius: 4px;
  text-align: center;
  font-size: 12px;
  color: var(--portal-muted);
  background: var(--portal-surface-soft);
}

.rank-index.top {
  color: #fff;
  background: var(--portal-gradient);
}

.rank-title {
  color: var(--csdn-subtext);
  font-size: 13px;
  line-height: 1.55;
}

.rank-item:hover .rank-title {
  color: var(--portal-secondary);
}

.resource-shortcuts {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px;
}

.shortcut-item {
  height: 40px;
  border: 1px solid var(--csdn-line);
  border-radius: 4px;
  background: var(--portal-surface-soft);
  color: var(--csdn-subtext);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.18s ease;
}

.shortcut-item:hover {
  border-color: color-mix(in srgb, var(--color-primary) 48%, var(--portal-line));
  color: var(--portal-secondary);
  background: var(--portal-gradient-soft);
}

.notice-item {
  font-size: 13px;
  color: var(--csdn-subtext);
  line-height: 1.7;
  padding: 7px 0;
}

.compact-empty {
  padding: 14px 0;
}
</style>
