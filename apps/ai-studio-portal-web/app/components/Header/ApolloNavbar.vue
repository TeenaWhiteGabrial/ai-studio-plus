<template>
  <header class="portal-header">
    <div class="header-row">
      <button class="brand-btn" @click="navigateTo('/')">
        <img v-if="siteConfig.logo" :src="siteConfig.logo" alt="logo" class="brand-logo">
        <span v-else class="brand-mark">AI</span>
        <span class="brand-name">{{ siteConfig.name || 'AI Studio' }}</span>
      </button>

      <form class="search-box" @submit.prevent="handleSearch">
        <input
          v-model="keyword"
          class="search-input"
          type="text"
          placeholder="搜索文章、Skill、Plugin"
        >
        <button class="search-button" type="submit">
          <Icon name="material-symbols:search" size="18" />
        </button>
      </form>

      <a class="publish-link" :href="publishArticleUrl" target="_blank" rel="noopener">
        <Icon name="material-symbols:edit-square-outline" size="18" />
        <span>发布文章</span>
      </a>
    </div>
  </header>
</template>

<script setup lang="ts">
const route = useRoute()
const config = useRuntimeConfig()
const { siteConfig } = useSite()

const keyword = ref('')

const publishArticleUrl = computed(() => {
  const base = String(config.public.consoleBase || '').replace(/\/$/, '')
  return `${base}/console/article/new/edit`
})

watch(
  () => route.query.keyword,
  value => {
    keyword.value = typeof value === 'string' ? value : ''
  },
  { immediate: true }
)

function handleSearch() {
  const value = keyword.value.trim()
  if (!value) return

  const path = route.path.startsWith('/resources') ? '/resources' : '/community'
  navigateTo({
    path,
    query: {
      ...route.query,
      keyword: value,
      page: undefined
    }
  })
}
</script>

<style scoped>
.portal-header {
  position: sticky;
  top: 0;
  z-index: 120;
  height: 56px;
  border-bottom: 1px solid var(--csdn-line);
  background: #fff;
  box-shadow: 0 1px 4px rgba(34, 34, 38, 0.04);
}

.header-row {
  height: 56px;
  display: grid;
  grid-template-columns: 220px minmax(320px, 560px) auto;
  align-items: center;
  justify-content: space-between;
  gap: 18px;
  padding: 0 18px;
}

.brand-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  border: 0;
  padding: 0;
  background: transparent;
  cursor: pointer;
}

.brand-logo,
.brand-mark {
  width: 30px;
  height: 30px;
  border-radius: 6px;
  flex: 0 0 auto;
}

.brand-logo {
  object-fit: cover;
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 800;
  background: var(--portal-gradient);
}

.brand-name {
  min-width: 0;
  color: #111827;
  font-size: 20px;
  font-weight: 800;
  white-space: nowrap;
}

.search-box {
  height: 36px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 48px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
  background: #fff;
  transition: border-color 0.18s ease, box-shadow 0.18s ease;
}

.search-box:focus-within {
  border-color: var(--portal-secondary);
  box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.1);
}

.search-input {
  width: 100%;
  min-width: 0;
  height: 100%;
  border: 0;
  outline: none;
  padding: 0 12px;
  color: var(--csdn-text);
  font-size: 14px;
}

.search-button {
  height: 100%;
  border: 0;
  border-left: 1px solid #e5e7eb;
  color: #fff;
  background: var(--portal-gradient);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.publish-link {
  height: 36px;
  border-radius: 4px;
  padding: 0 14px;
  color: #fff;
  background: var(--portal-gradient);
  box-shadow: 0 8px 18px rgba(79, 70, 229, 0.18);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 14px;
  font-weight: 700;
  white-space: nowrap;
  transition: transform 0.18s ease, box-shadow 0.18s ease;
}

.publish-link:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(79, 70, 229, 0.26);
}

@media (max-width: 900px) {
  .header-row {
    grid-template-columns: auto minmax(0, 1fr) auto;
    justify-content: stretch;
    padding: 0 10px;
    gap: 10px;
  }

  .brand-name {
    display: none;
  }
}

@media (max-width: 640px) {
  .publish-link span {
    display: none;
  }

  .publish-link {
    width: 36px;
    padding: 0;
  }
}
</style>
