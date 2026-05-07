<template>
  <header class="landing-topbar">
    <button class="corner-brand" type="button" @click="handleBrandClick">
      <img :src="logoSrc" alt="logo" class="corner-logo">
      <span>{{ siteConfig.name || 'AI Studio' }}</span>
    </button>

    <nav class="topbar-nav" aria-label="站点栏目">
      <button v-for="item in navItems" :key="item.path" type="button" @click="goProtected(item.path)">
        {{ item.label }}
      </button>
    </nav>

    <div class="topbar-actions">
      <PortalThemeSwitcher />

      <button v-if="!isLoggedIn" class="account-btn" type="button" @click="goLogin">
        <Icon name="material-symbols:login" size="20" />
        <span>登录</span>
      </button>
      <el-dropdown v-else trigger="click" popper-class="landing-user-dropdown" @command="handleUserCommand">
        <button class="account-btn logged" type="button">
          <img v-if="avatar" :src="avatar" alt="avatar" class="avatar">
          <span v-else class="avatar fallback">{{ userInitial }}</span>
          <span>{{ displayName }}</span>
          <Icon name="material-symbols:keyboard-arrow-down" size="18" />
        </button>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="/profile">个人中心</el-dropdown-item>
            <el-dropdown-item command="/knowledge">知识库检索</el-dropdown-item>
            <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </header>
</template>

<script setup lang="ts">
const props = withDefaults(defineProps<{
  brandTarget?: string
  brandProtected?: boolean
}>(), {
  brandTarget: '/',
  brandProtected: false,
})

const route = useRoute()
const runtimeConfig = useRuntimeConfig()
const authStore = useAuthStore()
const { siteConfig } = useSite()

const isLoggedIn = computed(() => Boolean(authStore.token))
const avatar = computed(() => authStore.avatar || '')
const displayName = computed(() => authStore.real_name || authStore.userName || authStore.username || '已登录')
const userInitial = computed(() => displayName.value.slice(0, 1).toUpperCase())
const navItems = [
  { label: '发现', path: '/discover' },
  { label: '知识库', path: '/knowledge' },
  { label: '社区', path: '/community' },
  { label: '资源中心', path: '/resources' },
]
const logoSrc = computed(() => {
  if (/^(https?:)?\/\//.test(siteConfig.logo)) return siteConfig.logo
  const baseURL = runtimeConfig.app.baseURL || '/'
  return `${baseURL.replace(/\/$/, '')}/${siteConfig.logo.replace(/^\//, '')}`
})

function loginRedirect() {
  return `/login?redirect=${encodeURIComponent(route.fullPath)}`
}

function goLogin() {
  navigateTo(loginRedirect())
}

function requireLogin() {
  if (isLoggedIn.value) return true
  ElMessage.warning('请先登录后再访问')
  goLogin()
  return false
}

function goProtected(path: string) {
  if (!requireLogin()) return
  navigateTo(path)
}

function handleBrandClick() {
  if (props.brandProtected && !requireLogin()) return
  navigateTo(props.brandTarget)
}

async function handleUserCommand(command: string) {
  if (command === 'logout') {
    await authStore.logout()
    navigateTo('/')
    return
  }
  goProtected(command)
}
</script>

<style scoped>
.landing-topbar {
  position: absolute;
  z-index: 10;
  inset: 0 0 auto;
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 28px;
  pointer-events: none;
}

.corner-brand,
.account-btn,
.topbar-nav button,
.topbar-actions {
  display: inline-flex;
  align-items: center;
  pointer-events: auto;
}

.corner-brand,
.account-btn,
.topbar-nav button {
  border: 0;
  cursor: pointer;
}

.corner-brand {
  gap: 10px;
  min-width: 0;
  padding: 0;
  background: transparent;
  color: var(--portal-text);
  font-size: 20px;
  font-weight: 900;
  text-shadow: 0 0 34px rgba(125, 211, 252, 0.45);
}

.corner-logo {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  flex: 0 0 auto;
  object-fit: cover;
}

.topbar-nav {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: inline-flex;
  align-items: center;
  gap: 6px;
  pointer-events: auto;
}

.topbar-actions {
  margin-left: auto;
  justify-content: flex-end;
  gap: 10px;
}

.topbar-nav button {
  height: 36px;
  border-radius: 8px;
  padding: 0 12px;
  color: var(--portal-subtext);
  background: transparent;
  font-size: 14px;
  font-weight: 800;
}

.topbar-nav button:hover {
  color: var(--portal-text);
  background: var(--portal-surface-soft);
}

.account-btn {
  height: 40px;
  border-radius: 8px;
  padding: 0 14px;
  gap: 8px;
  color: var(--portal-subtext);
  background: var(--portal-surface-glass);
  border: 1px solid var(--portal-line);
  font-weight: 800;
  backdrop-filter: blur(14px);
}

.account-btn.logged {
  padding-left: 8px;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  object-fit: cover;
  flex: 0 0 auto;
}

.avatar.fallback {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--color-primary-text);
  background: var(--portal-gradient);
  font-size: 13px;
  font-weight: 950;
}

:global(.landing-user-dropdown) {
  border: 1px solid var(--portal-line) !important;
  background: var(--portal-surface-strong) !important;
  backdrop-filter: blur(16px);
}

:global(.landing-user-dropdown .el-dropdown-menu) {
  background: transparent;
}

:global(.landing-user-dropdown .el-dropdown-menu__item) {
  color: var(--portal-subtext);
}

:global(.landing-user-dropdown .el-dropdown-menu__item:hover) {
  color: var(--portal-text);
  background: var(--portal-surface-soft);
}

@media (max-width: 680px) {
  .landing-topbar {
    height: 64px;
    padding: 0 16px;
  }

  .corner-brand span:last-child,
  .account-btn span:not(.avatar) {
    display: none;
  }

  .topbar-nav {
    left: 50%;
    max-width: calc(100vw - 148px);
    overflow-x: auto;
    scrollbar-width: none;
  }

  .topbar-nav::-webkit-scrollbar {
    display: none;
  }

  .topbar-nav button {
    height: 32px;
    padding: 0 8px;
    font-size: 13px;
    white-space: nowrap;
  }
}
</style>
