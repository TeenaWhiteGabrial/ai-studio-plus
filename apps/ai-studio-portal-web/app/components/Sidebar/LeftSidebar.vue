<template>
  <div class="left-rail">
    <nav class="rail-nav" aria-label="频道">
      <button
        v-for="item in channelItems"
        :key="item.key"
        class="rail-item"
        :class="{ active: item.active }"
        :title="item.label"
        @click="navigateTo(item.to)"
      >
        <Icon :name="item.icon" size="22" />
        <span>{{ item.label }}</span>
      </button>
    </nav>

    <div class="rail-user">
      <template v-if="isLoggedIn">
        <button class="avatar-btn" title="个人中心" @click="navigateTo('/profile')">
          <img v-if="authStore.avatar" :src="authStore.avatar" alt="avatar" class="user-avatar">
          <span v-else class="user-avatar avatar-fallback">{{ avatarFallback }}</span>
        </button>
        <button class="rail-mini-btn" title="个人设置" @click="navigateTo('/profile/settings')">
          <Icon name="material-symbols:settings-outline" size="21" />
        </button>
        <button class="rail-mini-btn" title="退出登录" @click="goLogout">
          <Icon name="material-symbols:logout" size="21" />
        </button>
      </template>

      <template v-else>
        <button class="login-entry" title="登录" @click="goLoginPage()">
          <Icon name="material-symbols:person-outline" size="22" />
          <span>登录</span>
        </button>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
const route = useRoute()
const authStore = useAuthStore()

const isLoggedIn = computed(() => !!authStore.token)

const displayName = computed(() => {
  return authStore.real_name || authStore.userName || authStore.username || '开发者'
})

const avatarFallback = computed(() => {
  const base = displayName.value || 'AI'
  return base.slice(0, 1).toUpperCase()
})

const channelItems = computed(() => [
  {
    key: 'home',
    label: '推荐',
    icon: 'material-symbols:home-outline',
    to: '/',
    active: route.path === '/'
  },
  {
    key: 'latest',
    label: '最新',
    icon: 'material-symbols:newspaper-outline',
    to: '/community?sort=latest',
    active: route.path.startsWith('/community') && route.query.sort === 'latest'
  },
  {
    key: 'hot',
    label: '热门',
    icon: 'material-symbols:local-fire-department-outline',
    to: '/community?sort=hot',
    active: route.path.startsWith('/community') && route.query.sort === 'hot'
  },
  {
    key: 'skill',
    label: 'Skill',
    icon: 'material-symbols:psychology-alt-outline',
    to: '/resources?type=skill',
    active: route.path.startsWith('/resources') && route.query.type !== 'plugin'
  },
  {
    key: 'plugin',
    label: 'Plugin',
    icon: 'material-symbols:extension-outline',
    to: '/resources?type=plugin',
    active: route.path.startsWith('/resources') && route.query.type === 'plugin'
  }
])
</script>

<style scoped>
.left-rail {
  height: calc(100vh - 56px);
  width: 72px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  border-right: 1px solid var(--csdn-line);
  background: #fff;
}

.rail-nav,
.rail-user {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 4px;
  padding: 10px 6px;
}

.rail-user {
  border-top: 1px solid #f0f1f3;
}

.rail-item,
.rail-mini-btn,
.login-entry,
.avatar-btn {
  border: 0;
  background: transparent;
  cursor: pointer;
}

.rail-item,
.login-entry {
  min-height: 54px;
  border-radius: 6px;
  color: var(--csdn-subtext);
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  font-size: 12px;
  transition: background 0.18s ease, color 0.18s ease;
}

.rail-item:hover,
.rail-item.active,
.login-entry:hover {
  color: var(--portal-secondary);
  background: var(--portal-gradient-soft);
}

.rail-item.active {
  font-weight: 700;
}

.avatar-btn,
.rail-mini-btn {
  width: 42px;
  height: 42px;
  margin: 0 auto;
  border-radius: 8px;
  color: var(--csdn-subtext);
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.avatar-btn:hover,
.rail-mini-btn:hover {
  color: var(--portal-secondary);
  background: var(--portal-gradient-soft);
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
  color: var(--portal-secondary);
  background: #eef4ff;
  font-size: 14px;
  font-weight: 800;
}
</style>
