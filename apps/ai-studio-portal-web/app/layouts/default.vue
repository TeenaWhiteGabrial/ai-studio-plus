<template>
  <div class="portal-shell">
    <HeaderApolloNavbar />

    <main class="csdn-container portal-main" :class="`mode-${layoutMode}`">
      <aside v-if="showLeftSidebar" class="portal-left">
        <slot name="left-sidebar">
          <LeftSidebar />
        </slot>
      </aside>

      <section class="portal-content">
        <slot />
      </section>

      <aside v-if="showRightSidebar" class="portal-right">
        <slot name="right-sidebar">
          <RightSidebar />
        </slot>
      </aside>
    </main>

    <FooterApollo />
  </div>
</template>

<script setup lang="ts">
import type { TokenResponse, UserInfo } from '~~/shared/types/auth'

import HeaderApolloNavbar from '~/components/Header/ApolloNavbar.vue'
import LeftSidebar from '~/components/Sidebar/LeftSidebar.vue'
import RightSidebar from '~/components/Sidebar/RightSidebar.vue'

const route = useRoute()
const config = useRuntimeConfig()

const normalizedPath = computed(() => route.path.replace(/^\//, ''))

const isFocusPage = computed(() => {
  return /^(profile(?:\/|$))/.test(normalizedPath.value)
})

const isDetailPage = computed(() => {
  return /^(community\/article|community\/question|resources\/[^/]+\/[^/]+)/.test(normalizedPath.value)
})

const isEmbedPage = computed(() => {
  return /^(knowledge(?:\/|$))/.test(normalizedPath.value)
})

const layoutMode = computed<'three' | 'content-right' | 'single' | 'embed'>(() => {
  if (isEmbedPage.value) {
    return 'embed'
  }
  if (isFocusPage.value) {
    return 'single'
  }
  if (isDetailPage.value) {
    return 'content-right'
  }
  return 'three'
})

const showLeftSidebar = computed(() => {
  return layoutMode.value === 'three'
})

const showRightSidebar = computed(() => {
  return layoutMode.value !== 'single' && layoutMode.value !== 'embed'
})

async function loginByCode() {
  try {
    const authStore = useAuthStore()
    const url = new URL(window.location.href)
    const hashCodeMatch = window.location.hash.match(/code=([^&]+)/)
    const hashCode = hashCodeMatch?.[1] ? decodeURIComponent(hashCodeMatch[1]) : ''
    const paramCode = url.searchParams.get('code') || hashCode
    if (!paramCode) {
      return
    }

    const { code, data } = await useSimpleFetch<TokenResponse>('/auth/singleLogin', {
      params: { code: paramCode },
      noToken: true
    })

    if (code === 200 && data?.access_token) {
      authStore.setToken(data.access_token)
      navigateTo('/')
    }
  } catch (error) {
    console.error('单点登录失败:', error)
  }
}

async function syncUserInfo() {
  const authStore = useAuthStore()
  if (!authStore.token) {
    return
  }
  const userRes = await useSimpleFetch<UserInfo>('/auth/user-info')
  if (userRes.code === 200 && userRes.data) {
    authStore.setLoginInfo(userRes.data)
    return
  }
  ElMessage.error(userRes.msg || userRes.message || '获取用户信息失败')
}

onMounted(async () => {
  if (import.meta.client) {
    const { default: microApp } = await import('@micro-zoe/micro-app')
    microApp.start()
  }

  if (config.public.loginType === 'maxkey') {
    await loginByCode()
  }

  await syncUserInfo()
})
</script>

<style scoped>
.portal-shell {
  min-height: 100vh;
  background: var(--csdn-bg);
}

.portal-main {
  display: grid;
  width: 100%;
  max-width: none;
  grid-template-columns: 200px minmax(0, 1fr) 300px;
  gap: 12px;
  padding: 0 12px 18px 0;
  margin: 0;
}

.portal-main.mode-content-right {
  grid-template-columns: minmax(0, 1fr) 300px;
}

.portal-main.mode-single {
  grid-template-columns: minmax(0, 1fr);
  max-width: 1120px;
}

.portal-main.mode-embed {
  grid-template-columns: minmax(0, 1fr);
  padding: 0;
}

.portal-left,
.portal-right {
  position: sticky;
  top: 56px;
  align-self: start;
  max-height: calc(100vh - 56px);
  overflow-y: auto;
  scrollbar-width: thin;
}

.portal-left {
  overflow: visible;
}

.portal-content,
.portal-right {
  margin-top: 12px;
}

.portal-content {
  min-width: 0;
}

.portal-main.mode-single .portal-content {
  width: 100%;
  max-width: 960px;
  margin: 0 auto;
}

.portal-main.mode-embed .portal-content {
  margin-top: 0;
  width: 100%;
}

@media (max-width: 1400px) {
  .portal-main {
    grid-template-columns: 200px minmax(0, 1fr) 280px;
  }

  .portal-main.mode-content-right {
    grid-template-columns: minmax(0, 1fr) 280px;
  }
}

@media (max-width: 1200px) {
  .portal-main.mode-three {
    grid-template-columns: 200px minmax(0, 1fr) 280px;
  }

  .portal-main.mode-three .portal-left {
    display: block;
  }
}

@media (max-width: 1024px) {
  .portal-main,
  .portal-main.mode-content-right,
  .portal-main.mode-embed,
  .portal-main.mode-single,
  .portal-main.mode-three {
    grid-template-columns: minmax(0, 1fr);
    max-width: none;
  }

  .portal-left,
  .portal-right {
    display: none;
  }
}
</style>
