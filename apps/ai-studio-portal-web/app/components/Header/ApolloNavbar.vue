<template>
  <header class="portal-header">
    <div class="csdn-container header-inner">
      <div class="header-left">
        <button class="brand-btn" @click="navigateTo('/')">
          <img v-if="siteConfig.logo" :src="siteConfig.logo" alt="logo" class="brand-logo">
          <span class="brand-text">{{ siteConfig.name || 'AI Studio' }}</span>
        </button>

        <nav class="channel-nav">
          <NuxtLink
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            class="channel-item"
            :class="{ active: isActive(item.to) }"
          >
            {{ item.label }}
          </NuxtLink>
        </nav>
      </div>

      <div class="header-right">
        <form class="header-search" @submit.prevent="handleSearch">
          <Icon name="material-symbols:search" size="18" class="search-icon" />
          <input
            v-model="keyword"
            type="text"
            placeholder="搜技术、问题、资源"
            class="search-input"
          >
        </form>

        <el-button type="primary" class="write-btn" @click="goCreate">
          <Icon name="material-symbols:edit-square-outline" size="18" />
          <span>发布问题</span>
        </el-button>

        <template v-if="isLoggedIn">
          <el-dropdown trigger="click">
            <button class="user-entry">
              <img v-if="authStore.avatar" :src="authStore.avatar" alt="avatar" class="user-avatar">
              <span v-else class="user-avatar avatar-fallback">{{ avatarFallback }}</span>
              <span class="user-name text-overflow-1">{{ displayName }}</span>
              <Icon name="material-symbols:keyboard-arrow-down" size="18" />
            </button>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="navigateTo('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item @click="navigateTo('/profile/settings')">账号设置</el-dropdown-item>
                <el-dropdown-item divided @click="goLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>

        <template v-else>
          <el-button plain @click="goLoginPage()">登录</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
const route = useRoute()
const authStore = useAuthStore()
const { siteConfig } = useSite()

const keyword = ref('')

const navItems = [
  { label: '首页', to: '/' },
  { label: '社区', to: '/community' },
  { label: '资源中心', to: '/resources' }
]

const isLoggedIn = computed(() => !!authStore.token)

const displayName = computed(() => {
  return authStore.real_name || authStore.userName || authStore.username || '开发者'
})

const avatarFallback = computed(() => {
  const base = displayName.value || 'AI'
  return base.slice(0, 1).toUpperCase()
})

watch(
  () => route.query.keyword,
  value => {
    keyword.value = typeof value === 'string' ? value : ''
  },
  { immediate: true }
)

function isActive(path: string) {
  if (path === '/') {
    return route.path === '/'
  }
  return route.path.startsWith(path)
}

function handleSearch() {
  const value = keyword.value.trim()
  if (!value) {
    return
  }

  if (route.path.startsWith('/resources')) {
    navigateTo({
      path: '/resources',
      query: {
        ...route.query,
        keyword: value
      }
    })
    return
  }

  navigateTo({
    path: '/community',
    query: {
      ...route.query,
      keyword: value
    }
  })
}

function goCreate() {
  if (!authStore.token) {
    goLoginPage()
    return
  }
  navigateTo('/community/ask')
}
</script>

<style scoped>
.portal-header {
  position: sticky;
  top: 0;
  z-index: 120;
  border-bottom: 1px solid var(--csdn-line);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: saturate(180%) blur(12px);
}

.header-inner {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 20px;
}

.brand-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  border: 0;
  padding: 0;
  background: transparent;
  cursor: pointer;
}

.brand-logo {
  width: 28px;
  height: 28px;
  border-radius: 6px;
}

.brand-text {
  font-size: 20px;
  font-weight: 700;
  background: var(--portal-gradient);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.channel-nav {
  display: flex;
  align-items: center;
  gap: 8px;
}

.channel-item {
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 14px;
  color: var(--csdn-subtext);
  transition: all 0.2s ease;
}

.channel-item:hover {
  background: var(--csdn-hover);
  color: var(--csdn-primary);
}

.channel-item.active {
  background: var(--csdn-primary-soft);
  color: var(--csdn-primary);
  font-weight: 600;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-search {
  display: flex;
  align-items: center;
  gap: 6px;
  width: 320px;
  height: 40px;
  border: 1px solid var(--csdn-line);
  border-radius: 999px;
  background: #fff;
  padding: 0 12px;
  transition: border-color 0.2s ease;
}

.header-search:focus-within {
  border-color: var(--csdn-primary);
}

.search-icon {
  color: var(--csdn-muted);
}

.search-input {
  width: 100%;
  height: 100%;
  border: 0;
  outline: none;
  background: transparent;
  color: var(--csdn-text);
  font-size: 14px;
}

.write-btn {
  --el-button-bg-color: transparent;
  --el-button-hover-bg-color: transparent;
  --el-button-active-bg-color: transparent;
  --el-button-border-color: transparent;
  --el-button-hover-border-color: transparent;
  --el-button-active-border-color: transparent;
  height: 40px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: 0;
  background: var(--portal-gradient);
  color: #fff;
}

.write-btn:hover {
  opacity: 0.92;
}

.user-entry {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  border: 0;
  background: transparent;
  cursor: pointer;
  max-width: 180px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-fallback {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #dbeafe;
  color: #1d4ed8;
  font-size: 14px;
  font-weight: 600;
}

.user-name {
  max-width: 96px;
  font-size: 14px;
  color: var(--csdn-subtext);
}

@media (max-width: 1200px) {
  .channel-nav {
    display: none;
  }
}

@media (max-width: 1024px) {
  .header-search {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .header-inner {
    height: 56px;
  }

  .header-search {
    display: none;
  }

  .write-btn {
    display: none;
  }

  .brand-text {
    font-size: 18px;
  }
}
</style>
