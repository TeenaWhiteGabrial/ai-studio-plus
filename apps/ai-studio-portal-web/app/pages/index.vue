<template>
  <main class="landing-page">
    <div class="grid-layer"></div>
    <div class="beam beam-one"></div>
    <div class="beam beam-two"></div>
    <div class="circuit-board" aria-hidden="true">
      <span class="trace trace-a"></span>
      <span class="trace trace-b"></span>
      <span class="trace trace-c"></span>
      <span class="trace trace-d"></span>
    </div>
    <div class="scanline"></div>

    <PortalLandingTopbar brand-target="/discover" brand-protected />

    <section class="landing-shell">
      <div class="hero-core">
        <div class="signal-panel panel-left" aria-hidden="true">
          <span>RAG</span>
          <strong>98.7</strong>
          <i></i>
        </div>

        <div class="brand-orbit" aria-hidden="true">
          <span class="orbit orbit-one"></span>
          <span class="orbit orbit-two"></span>
          <span class="orbit orbit-three"></span>
          <span class="node node-a"></span>
          <span class="node node-b"></span>
          <span class="node node-c"></span>
          <img :src="logoSrc" alt="" class="hero-logo">
        </div>

        <div class="signal-panel panel-right" aria-hidden="true">
          <span>VECTOR</span>
          <strong>24K</strong>
          <i></i>
        </div>

        <div class="brand-copy">
          <p class="brand-kicker">{{ isLoggedIn ? 'ACCESS GRANTED' : 'LOGIN REQUIRED' }}</p>
          <h1>{{ siteConfig.name || 'AI Studio' }}</h1>
        </div>

        <form class="search-console" @submit.prevent="handleSearch">
          <Icon name="material-symbols:database-search-outline" size="26" class="search-icon" />
          <input
            v-model="keyword"
            type="search"
            placeholder="搜索知识库、文章、资源"
            aria-label="搜索知识库、文章、资源"
          >
          <button type="submit" :disabled="searching || !keyword.trim()" :class="{ loading: searching }">
            <Icon :name="searching ? 'material-symbols:progress-activity' : 'material-symbols:arrow-forward'" size="24" />
          </button>
        </form>
      </div>

      <section v-if="hasSearched" class="unified-results">
        <div class="result-headline">
          <div>
            <span class="result-kicker">Unified Search</span>
            <h2>{{ searching ? '正在检索' : `共找到 ${totalResultCount} 条结果` }}</h2>
          </div>
          <button class="result-jump" type="button" @click="openKnowledgePage">
            <Icon name="material-symbols:database-search-outline" size="18" />
            <span>知识库页</span>
          </button>
        </div>

        <div class="result-groups">
          <section class="result-group">
            <div class="group-title">
              <Icon name="material-symbols:database-outline" size="20" />
              <span>知识库</span>
              <em>{{ knowledgeResults.length }}</em>
            </div>
            <div v-if="searching" class="result-empty">检索中...</div>
            <div v-else-if="knowledgeResults.length === 0" class="result-empty">暂无知识片段</div>
            <template v-else>
              <button
                v-for="item in knowledgeResults"
                :key="item.id"
                class="result-item"
                type="button"
                @click="openKnowledgePage"
              >
                <strong>{{ getKnowledgeTitle(item) }}</strong>
                <span>{{ item.metadata?.content || '暂无片段内容' }}</span>
              </button>
            </template>
          </section>

          <section class="result-group">
            <div class="group-title">
              <Icon name="material-symbols:article-outline" size="20" />
              <span>文章</span>
              <em>{{ articleResults.length }}</em>
            </div>
            <div v-if="searching" class="result-empty">检索中...</div>
            <div v-else-if="articleResults.length === 0" class="result-empty">暂无文章</div>
            <template v-else>
              <button
                v-for="article in articleResults"
                :key="article.id"
                class="result-item"
                type="button"
                @click="navigateTo(`/community/article/${article.id}`)"
              >
                <strong>{{ article.title }}</strong>
                <span>{{ article.summary || stripHtml(article.content) || '暂无摘要' }}</span>
                <small>{{ article.authorName || '未知作者' }}</small>
              </button>
            </template>
          </section>

          <section class="result-group">
            <div class="group-title">
              <Icon name="material-symbols:extension-outline" size="20" />
              <span>资源</span>
              <em>{{ resourceResults.length }}</em>
            </div>
            <div v-if="searching" class="result-empty">检索中...</div>
            <div v-else-if="resourceResults.length === 0" class="result-empty">暂无资源</div>
            <template v-else>
              <button
                v-for="resource in resourceResults"
                :key="`${resource.type}-${resource.id}`"
                class="result-item"
                type="button"
                @click="navigateTo(`/resources/${resource.type || 'skill'}/${resource.id}`)"
              >
                <strong>{{ resource.name || resource.title }}</strong>
                <span>{{ resource.introduction || resource.description || '暂无介绍' }}</span>
                <small>{{ formatResourceType(resource.type) }}</small>
              </button>
            </template>
          </section>
        </div>
      </section>
    </section>

    <FooterApollo transparent />
  </main>
</template>

<script setup lang="ts">
import type { Article } from '~~/shared/types/article'
import type { Resource } from '~~/shared/types/resource'
import type { KnowledgeSearchResult } from '~/composables/useKnowledgeBase'

definePageMeta({
  layout: false,
})

const route = useRoute()
const runtimeConfig = useRuntimeConfig()
const authStore = useAuthStore()
const { siteConfig } = useSite()
const { search: searchKnowledge } = useKnowledgeBase()
const { getArticleList } = useArticle()
const { getResourceList } = useResource()
const keyword = ref('')
const hasSearched = ref(false)
const searching = ref(false)
const lastSearchQuery = ref('')
const knowledgeResults = ref<KnowledgeSearchResult[]>([])
const articleResults = ref<Article[]>([])
const resourceResults = ref<Resource[]>([])

const isLoggedIn = computed(() => Boolean(authStore.token))
const totalResultCount = computed(() => knowledgeResults.value.length + articleResults.value.length + resourceResults.value.length)
const logoSrc = computed(() => {
  if (/^(https?:)?\/\//.test(siteConfig.logo)) return siteConfig.logo
  const baseURL = runtimeConfig.app.baseURL || '/'
  return `${baseURL.replace(/\/$/, '')}/${siteConfig.logo.replace(/^\//, '')}`
})

function loginRedirect() {
  return `/login?redirect=${encodeURIComponent(route.fullPath)}`
}

function requireLogin() {
  if (isLoggedIn.value) return true
  ElMessage.warning('请先登录后再搜索')
  navigateTo(loginRedirect())
  return false
}

async function handleSearch() {
  const query = keyword.value.trim()
  if (!query) return
  if (!requireLogin()) return

  hasSearched.value = true
  searching.value = true
  lastSearchQuery.value = query
  knowledgeResults.value = []
  articleResults.value = []
  resourceResults.value = []

  const [knowledge, articles, skills, plugins] = await Promise.allSettled([
    searchKnowledge(query, 5),
    getArticleList({ keyword: query, page: 1, pageSize: 5, sort: 'latest' }),
    getResourceList({ type: 'skill', keyword: query, page: 1, pageSize: 4, sort: 'latest' }),
    getResourceList({ type: 'plugin', keyword: query, page: 1, pageSize: 4, sort: 'latest' }),
  ])

  if (knowledge.status === 'fulfilled') {
    knowledgeResults.value = knowledge.value || []
  }
  if (articles.status === 'fulfilled') {
    articleResults.value = articles.value.records || []
  }
  const resources: Resource[] = []
  if (skills.status === 'fulfilled') {
    resources.push(...(skills.value.records || []))
  }
  if (plugins.status === 'fulfilled') {
    resources.push(...(plugins.value.records || []))
  }
  resourceResults.value = resources
  searching.value = false

  if (totalResultCount.value === 0) {
    ElMessage.info('没有找到匹配结果')
  }
}

function openKnowledgePage() {
  const query = lastSearchQuery.value || keyword.value.trim()
  if (!query) return
  navigateTo({
    path: '/knowledge',
    query: { query },
  })
}

function getKnowledgeTitle(item: KnowledgeSearchResult) {
  const meta = item.metadata || {}
  if (meta.fileName) return meta.fileName
  if (meta.filePath) return String(meta.filePath).split(/[\\/]/).pop() || meta.filePath
  return meta.documentId || item.id
}

function stripHtml(value = '') {
  return value.replace(/<[^>]+>/g, '').trim()
}

function formatResourceType(type?: string) {
  if (type === 'plugin') return 'Plugin'
  if (type === 'tutorial') return 'Tutorial'
  return 'Skill'
}
</script>

<style scoped>
.landing-page {
  position: relative;
  min-height: 100vh;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  background:
    var(--portal-page-bg);
  color: var(--portal-text);
}

.grid-layer {
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148, 163, 184, 0.12) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148, 163, 184, 0.12) 1px, transparent 1px);
  background-size: 46px 46px;
  mask-image: radial-gradient(circle at center, black, transparent 72%);
  animation: gridFlow 18s linear infinite;
}

.beam {
  position: absolute;
  width: 60vw;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--portal-primary), transparent);
  filter: drop-shadow(0 0 18px color-mix(in srgb, var(--color-primary) 70%, transparent));
  animation: drift 10s linear infinite;
}

.beam-one {
  top: 24%;
  left: -16vw;
}

.beam-two {
  right: -14vw;
  bottom: 24%;
  animation-delay: -4s;
}

.scanline {
  position: absolute;
  inset: 0;
  background: repeating-linear-gradient(
    180deg,
    rgba(255, 255, 255, 0.03) 0,
    rgba(255, 255, 255, 0.03) 1px,
    transparent 1px,
    transparent 5px
  );
  pointer-events: none;
  animation: scanPulse 7s ease-in-out infinite;
}

.circuit-board {
  position: absolute;
  inset: 72px 8vw 72px;
  pointer-events: none;
  opacity: 0.82;
}

.trace {
  position: absolute;
  display: block;
  border: 1px solid color-mix(in srgb, var(--color-primary) 42%, transparent);
  filter: drop-shadow(0 0 14px color-mix(in srgb, var(--color-primary) 38%, transparent));
  animation: tracePulse 5.8s ease-in-out infinite;
}

.trace::before,
.trace::after {
  content: "";
  position: absolute;
  width: 7px;
  height: 7px;
  border: 1px solid var(--portal-primary);
  background: var(--portal-surface-strong);
  box-shadow: 0 0 16px color-mix(in srgb, var(--color-primary) 54%, transparent);
}

.trace-a {
  left: 2%;
  top: 18%;
  width: 18vw;
  height: 86px;
  border-right: 0;
  border-bottom: 0;
}

.trace-a::before {
  left: -4px;
  top: -4px;
}

.trace-a::after {
  right: -4px;
  top: -4px;
}

.trace-b {
  right: 0;
  top: 24%;
  width: 21vw;
  height: 116px;
  border-left: 0;
  border-bottom: 0;
  animation-delay: -1.8s;
}

.trace-b::before {
  right: -4px;
  top: -4px;
}

.trace-b::after {
  left: -4px;
  top: -4px;
}

.trace-c {
  left: 7%;
  bottom: 17%;
  width: 16vw;
  height: 74px;
  border-right: 0;
  border-top: 0;
  animation-delay: -3.2s;
}

.trace-c::before {
  left: -4px;
  bottom: -4px;
}

.trace-c::after {
  right: -4px;
  bottom: -4px;
}

.trace-d {
  right: 11%;
  bottom: 14%;
  width: 19vw;
  height: 96px;
  border-left: 0;
  border-top: 0;
  animation-delay: -4.4s;
}

.trace-d::before {
  right: -4px;
  bottom: -4px;
}

.trace-d::after {
  left: -4px;
  bottom: -4px;
}

.landing-shell {
  position: relative;
  z-index: 1;
  flex: 1;
  min-height: 0;
  width: min(1120px, calc(100% - 32px));
  margin: 0 auto;
  display: grid;
  align-items: center;
  justify-items: center;
  gap: 22px;
  padding: 104px 0 34px;
}

.hero-core {
  position: relative;
  width: min(860px, 100%);
  min-height: 520px;
  display: grid;
  grid-template-rows: 300px auto auto;
  justify-items: center;
  align-content: center;
  gap: 18px;
}

.hero-core::before,
.hero-core::after {
  content: "";
  position: absolute;
  inset: 42px 10% auto;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--portal-primary), var(--portal-secondary), transparent);
  opacity: 0.68;
  filter: drop-shadow(0 0 18px color-mix(in srgb, var(--color-primary) 54%, transparent));
  animation: railGlow 4s ease-in-out infinite;
}

.hero-core::after {
  inset: auto 16% 64px;
  animation-delay: -1.8s;
}

.brand-orbit {
  position: relative;
  width: 300px;
  height: 300px;
  display: grid;
  place-items: center;
  animation: coreArrival 0.9s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.brand-orbit::before {
  content: "";
  position: absolute;
  inset: 54px;
  border-radius: 50%;
  background:
    linear-gradient(var(--portal-surface-glass), var(--portal-surface-glass)) padding-box,
    conic-gradient(from 120deg, var(--portal-primary), transparent 34%, var(--portal-secondary), transparent 72%, var(--portal-primary)) border-box;
  border: 1px solid transparent;
  box-shadow:
    0 0 0 1px color-mix(in srgb, var(--color-primary) 20%, transparent) inset,
    0 0 58px color-mix(in srgb, var(--color-primary) 22%, transparent);
  backdrop-filter: var(--portal-backdrop-filter);
}

.hero-logo {
  position: relative;
  z-index: 3;
  width: 118px;
  height: 118px;
  border-radius: 24px;
  object-fit: cover;
  filter:
    drop-shadow(0 20px 36px rgba(0, 0, 0, 0.26))
    drop-shadow(0 0 28px color-mix(in srgb, var(--color-primary) 24%, transparent));
  animation: logoHover 4.2s ease-in-out infinite;
}

.orbit {
  position: absolute;
  border-radius: 50%;
  border: 1px solid color-mix(in srgb, var(--color-primary) 28%, transparent);
}

.orbit-one {
  inset: 22px;
  border-top-color: var(--portal-primary);
  animation: orbitSpin 14s linear infinite;
}

.orbit-two {
  inset: 48px;
  border-right-color: var(--portal-secondary);
  transform: rotateX(64deg);
  animation: orbitSpinReverse 10s linear infinite;
}

.orbit-three {
  inset: 84px;
  border-left-color: color-mix(in srgb, var(--portal-primary) 64%, var(--portal-secondary));
  animation: orbitSpin 7.5s linear infinite;
}

.node {
  position: absolute;
  z-index: 2;
  width: 9px;
  height: 9px;
  border-radius: 50%;
  background: var(--portal-primary);
  box-shadow: 0 0 18px color-mix(in srgb, var(--color-primary) 68%, transparent);
  animation: nodeBlink 2.8s ease-in-out infinite;
}

.node-a {
  top: 42px;
  left: 104px;
}

.node-b {
  right: 48px;
  top: 156px;
  animation-delay: -0.9s;
}

.node-c {
  bottom: 64px;
  left: 72px;
  animation-delay: -1.8s;
}

.brand-copy {
  text-align: center;
  animation: copyArrival 0.8s 0.12s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.brand-kicker {
  margin: 0 0 8px;
  color: var(--portal-primary);
  font-size: 12px;
  font-weight: 950;
  letter-spacing: 0;
}

.brand-copy h1 {
  margin: 0;
  color: var(--portal-text);
  font-size: clamp(42px, 8vw, 78px);
  line-height: 0.96;
  font-weight: 950;
  text-shadow: 0 0 34px color-mix(in srgb, var(--color-primary) 24%, transparent);
}

.signal-panel {
  position: absolute;
  top: 166px;
  width: 150px;
  min-height: 82px;
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 13px 14px;
  color: var(--portal-text);
  background: var(--portal-surface-glass);
  box-shadow: var(--portal-shadow);
  backdrop-filter: var(--portal-backdrop-filter);
  overflow: hidden;
  animation: panelFloat 5.4s ease-in-out infinite;
}

.signal-panel::before {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(105deg, transparent, color-mix(in srgb, var(--color-primary) 14%, transparent), transparent);
  transform: translateX(-110%);
  animation: panelSweep 4.8s ease-in-out infinite;
}

.signal-panel span,
.signal-panel strong,
.signal-panel i {
  position: relative;
  z-index: 1;
}

.signal-panel span {
  display: block;
  color: var(--portal-muted);
  font-size: 11px;
  font-weight: 900;
  letter-spacing: 0;
}

.signal-panel strong {
  display: block;
  margin-top: 7px;
  color: var(--portal-text);
  font-size: 30px;
  line-height: 1;
}

.signal-panel i {
  display: block;
  width: 100%;
  height: 4px;
  margin-top: 12px;
  border-radius: 999px;
  background: linear-gradient(90deg, var(--portal-primary), var(--portal-secondary));
  box-shadow: 0 0 18px color-mix(in srgb, var(--color-primary) 36%, transparent);
}

.panel-left {
  left: 0;
}

.panel-right {
  right: 0;
  animation-delay: -2.7s;
}

.search-console button {
  border: 0;
  cursor: pointer;
}

.search-console {
  position: relative;
  isolation: isolate;
  width: min(720px, 100%);
  height: 72px;
  display: grid;
  grid-template-columns: 64px minmax(0, 1fr) 64px;
  align-items: center;
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  background: var(--portal-surface-glass);
  box-shadow:
    0 0 0 1px color-mix(in srgb, var(--color-primary) 18%, transparent) inset,
    var(--portal-shadow);
  backdrop-filter: var(--portal-backdrop-filter);
  transition: border-color 0.22s ease, box-shadow 0.22s ease, transform 0.22s ease;
  animation: consoleRise 0.8s 0.22s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.search-console::before {
  content: "";
  position: absolute;
  inset: -1px;
  z-index: -1;
  border-radius: inherit;
  background: linear-gradient(115deg, transparent 0%, var(--portal-primary) 26%, var(--portal-secondary) 50%, transparent 74%);
  opacity: 0;
  filter: blur(14px);
  animation: consoleGlow 4.6s ease-in-out infinite;
}

.search-console::after {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background: linear-gradient(90deg, transparent, color-mix(in srgb, var(--color-primary) 18%, transparent), transparent);
  transform: translateX(-110%);
  animation: consoleSweep 5.2s ease-in-out infinite;
  pointer-events: none;
}

.search-console:focus-within {
  border-color: color-mix(in srgb, var(--color-primary) 58%, var(--portal-line));
  box-shadow:
    0 0 0 1px color-mix(in srgb, var(--color-primary) 26%, transparent) inset,
    var(--portal-shadow),
    0 0 54px color-mix(in srgb, var(--color-primary) 20%, transparent);
  transform: translateY(-2px);
}

.search-icon {
  margin: 0 auto;
  color: var(--portal-primary);
  animation: iconFloat 2.8s ease-in-out infinite;
}

.search-console input {
  width: 100%;
  min-width: 0;
  height: 100%;
  border: 0;
  outline: none;
  background: transparent;
  color: var(--portal-text);
  font-size: 22px;
}

.search-console input::placeholder {
  color: var(--portal-muted);
}

.search-console button {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary-text);
  background: var(--portal-gradient);
  box-shadow: 0 0 28px color-mix(in srgb, var(--color-primary) 32%, transparent);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.search-console button:not(:disabled):hover {
  transform: translateX(2px) scale(1.04);
  box-shadow: 0 0 38px color-mix(in srgb, var(--color-primary) 44%, transparent);
}

.search-console button .iconify {
  transition: transform 0.18s ease;
}

.search-console button.loading .iconify {
  animation: spin 1s linear infinite;
}

.search-console button:disabled {
  opacity: 0.45;
  cursor: not-allowed;
}

.unified-results {
  width: min(1080px, 100%);
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 16px;
  color: var(--portal-text);
  background: var(--portal-surface-glass);
  box-shadow: var(--portal-shadow);
  backdrop-filter: var(--portal-backdrop-filter);
  animation: resultsArrival 0.42s cubic-bezier(0.22, 1, 0.36, 1) both;
}

.result-headline {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 14px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--portal-line);
}

.result-kicker {
  display: inline-flex;
  margin-bottom: 5px;
  color: var(--portal-primary);
  font-size: 12px;
  font-weight: 900;
}

.result-headline h2 {
  margin: 0;
  color: var(--portal-text);
  font-size: 22px;
  line-height: 1.25;
}

.result-jump {
  height: 36px;
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--portal-subtext);
  background: var(--portal-surface-soft);
  cursor: pointer;
  font-weight: 800;
}

.result-jump:hover {
  color: var(--portal-text);
  border-color: color-mix(in srgb, var(--color-primary) 48%, var(--portal-line));
}

.result-groups {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 12px;
  padding-top: 14px;
}

.result-group {
  min-width: 0;
  border: 1px solid var(--portal-line);
  border-radius: 8px;
  padding: 12px;
  background: var(--portal-surface-soft);
}

.group-title {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-bottom: 10px;
  color: var(--portal-text);
  font-weight: 900;
}

.group-title em {
  margin-left: auto;
  min-width: 24px;
  height: 22px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary-text);
  background: var(--portal-gradient);
  font-style: normal;
  font-size: 12px;
}

.result-empty {
  min-height: 132px;
  display: grid;
  place-items: center;
  color: var(--portal-muted);
  font-size: 13px;
}

.result-item {
  width: 100%;
  min-height: 92px;
  border: 0;
  border-top: 1px solid var(--portal-line);
  padding: 10px 0;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 5px;
  text-align: left;
  color: var(--portal-subtext);
  background: transparent;
  cursor: pointer;
}

.result-item:first-of-type {
  border-top: 0;
}

.result-item:hover strong {
  color: var(--portal-primary);
}

.result-item strong {
  width: 100%;
  color: var(--portal-text);
  font-size: 14px;
  line-height: 1.45;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-item span {
  display: -webkit-box;
  width: 100%;
  color: var(--portal-subtext);
  line-height: 1.58;
  font-size: 13px;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.result-item small {
  color: var(--portal-muted);
  font-size: 12px;
}

@keyframes drift {
  from {
    transform: translateX(-12vw);
  }
  to {
    transform: translateX(112vw);
  }
}

@keyframes gridFlow {
  from {
    background-position: 0 0, 0 0;
  }
  to {
    background-position: 46px 46px, 46px 46px;
  }
}

@keyframes scanPulse {
  0%,
  100% {
    opacity: 0.42;
  }
  50% {
    opacity: 0.72;
  }
}

@keyframes tracePulse {
  0%,
  100% {
    opacity: 0.28;
    transform: translateY(0);
  }
  50% {
    opacity: 0.88;
    transform: translateY(-4px);
  }
}

@keyframes consoleRise {
  from {
    opacity: 0;
    transform: translateY(18px) scale(0.98);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes consoleGlow {
  0%,
  100% {
    opacity: 0.12;
  }
  50% {
    opacity: 0.34;
  }
}

@keyframes consoleSweep {
  0%,
  32% {
    transform: translateX(-110%);
  }
  62%,
  100% {
    transform: translateX(110%);
  }
}

@keyframes iconFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes resultsArrival {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes coreArrival {
  from {
    opacity: 0;
    transform: translateY(16px) scale(0.92);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes copyArrival {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes orbitSpin {
  to {
    transform: rotate(360deg);
  }
}

@keyframes orbitSpinReverse {
  to {
    transform: rotateX(64deg) rotate(-360deg);
  }
}

@keyframes logoHover {
  0%,
  100% {
    transform: translateY(0) scale(1);
  }
  50% {
    transform: translateY(-6px) scale(1.03);
  }
}

@keyframes nodeBlink {
  0%,
  100% {
    opacity: 0.42;
    transform: scale(0.82);
  }
  50% {
    opacity: 1;
    transform: scale(1.18);
  }
}

@keyframes panelFloat {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes panelSweep {
  0%,
  42% {
    transform: translateX(-110%);
  }
  70%,
  100% {
    transform: translateX(110%);
  }
}

@keyframes railGlow {
  0%,
  100% {
    opacity: 0.28;
  }
  50% {
    opacity: 0.74;
  }
}

@media (max-width: 680px) {
  .landing-shell {
    padding-top: 86px;
  }

  .hero-core {
    min-height: 500px;
    grid-template-rows: 230px auto auto;
    gap: 14px;
  }

  .brand-orbit {
    width: 230px;
    height: 230px;
  }

  .brand-orbit::before {
    inset: 42px;
  }

  .hero-logo {
    width: 92px;
    height: 92px;
    border-radius: 20px;
  }

  .signal-panel {
    display: none;
  }

  .brand-copy h1 {
    font-size: 42px;
  }

  .search-console {
    height: 62px;
    grid-template-columns: 52px minmax(0, 1fr) 56px;
  }

  .result-headline {
    flex-direction: column;
  }

  .result-groups {
    grid-template-columns: 1fr;
  }

  .search-console input {
    font-size: 18px;
  }

  .circuit-board {
    inset: 72px 14px 70px;
  }

  .trace {
    display: none;
  }
}
</style>
