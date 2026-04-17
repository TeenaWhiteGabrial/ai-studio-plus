<template>
  <div class="min-h-screen bg-gray-50">
    <!-- 顶部导航栏 -->
    <HeaderApolloNavbar />
    <!-- 主要内容区域 - 三栏布局 -->
    <main class="main-layout">
      <!-- 左侧边栏 -->
      <aside class="left-sidebar" :class="{ 'hidden-mobile': showMobileLeftSidebar }">
        <slot name="left-sidebar">
          <LeftSidebar />
        </slot>
      </aside>
      <!-- 主内容区 -->
      <div class="main-content">
        <slot />
      </div>
      <!-- 右侧边栏 -->
      <aside class="right-sidebar" :class="{ 'hidden-mobile': showMobileRightSidebar }">
        <slot name="right-sidebar">
          <RightSidebar />
        </slot>
      </aside>
    </main>
    <!-- 底部信息栏 -->
    <FooterApollo />
  </div>
</template>

<script setup lang="ts">
import type { TokenResponse } from '~~/shared/types/auth'
import { ROLE_STATUS } from '~~/shared/types/auth'

// 显式导入头部组件和底部信息栏
import HeaderApolloNavbar from '~/components/Header/ApolloNavbar.vue'
import LeftSidebar from '~/components/Sidebar/LeftSidebar.vue'
import RightSidebar from '~/components/Sidebar/RightSidebar.vue'

// 响应式侧边栏控制
const showMobileLeftSidebar = ref(false)
const showMobileRightSidebar = ref(false)

const config = useRuntimeConfig()

async function login() {
  try {
    const authStore = useAuthStore()
    const searchParam = new URLSearchParams(window.location.href)
    const paramCode = searchParam.get('##code')
    if (!paramCode)
      return Promise.resolve()

    const { code, data, message } = await useSimpleFetch<TokenResponse>('/prod-api/auth/singleLogin', {
      params: {
        code: paramCode
      },
      noToken: true
    })
    if (code === 200 && data) {
      authStore.setToken(data.access_token)
      // 登录成功后跳转
      navigateTo('/')
    }
    else {
      console.error('登录失败:', message || '未知错误')
    }
  }
  catch (error) {
    console.error('登录过程中发生错误:', error)
  }
}

async function getUserInfo() {
  const authStore = useAuthStore()
  if (!!authStore.token) {
    const userRes = await useSimpleFetch<{
      code: number
      msg: string
      roles: string[]
      user: any
    }>('/prod-api/system/user/getInfo', {})
    if (userRes.code === 200 && userRes.data.user) {
      authStore.setLoginInfo({
        userName: userRes.data.user.nickName,
        phone: userRes.data.user.phonenumber,
        email: userRes.data.user.email,
        roles: userRes.data.roles as ROLE_STATUS[]
      })
    }
    else {
      ElMessage.error(userRes.data.msg || '获取用户信息失败')
      return Promise.resolve()
    }
    const { code, msg, data } = await useSimpleFetch<{
      code: string
      msg: string
      data: {
        userId: string,
        applied: string[],
        userStatus?: string[]
      }
    }>('/prod-api/cloudMarket/user/getUserInfo', {})
    if (code === 200 && data.data && data.data.userStatus) {
      const roleInfo: ROLE_STATUS[] = []
      if (data.data.userStatus.includes('personal_approved')) {
        roleInfo.push(ROLE_STATUS.PERSONAL)
      }
      if (data.data.userStatus.includes('business_approved')) {
        roleInfo.push(ROLE_STATUS.BUSINESS)
      }
      if (data.data.userStatus.includes('service_provider_approved')) {
        roleInfo.push(ROLE_STATUS.SERVICE_PROVIDER)
      }
      authStore.setLoginInfo({
        userId: data.data.userId,
        roles: roleInfo // 审核通过的权限
      })
    }
    else {
      ElMessage.error(msg || '获取用户信息失败')
      return Promise.resolve()
    }
  }
}

onMounted(async () => {
  if (import.meta.client) {
    // 动态导入 microApp 以确保仅在客户端加载
    const { default: microApp } = await import('@micro-zoe/micro-app')
    microApp.start()
  }
  if (config.public.loginType === 'maxkey') {
    await login()
    await getUserInfo()
  }
  else if (config.public.loginType === 'own') {
    await getUserInfo()
  }
})
</script>

<style scoped>
.main-layout {
  @apply flex w-full max-w-[1400px] mx-auto px-4 gap-6 pt-6;
}

.left-sidebar {
  @apply w-60 flex-shrink-0;
}

.right-sidebar {
  @apply w-72 flex-shrink-0;
}

.main-content {
  @apply flex-1 min-w-0;
}

/* 响应式 - 移动端隐藏左侧边栏 */
@media (max-width: 768px) {
  .main-layout {
    @apply px-2 gap-4;
  }

  .left-sidebar {
    @apply hidden;
  }

  .left-sidebar:not(.hidden-mobile) {
    @apply block fixed left-0 top-[60px] bottom-0 w-60 z-50 bg-white shadow-xl;
  }
}

/* 响应式 - 平板隐藏右侧边栏 */
@media (max-width: 1024px) {
  .right-sidebar {
    @apply hidden;
  }

  .right-sidebar:not(.hidden-mobile) {
    @apply block fixed right-0 top-[60px] bottom-0 w-72 z-50 bg-white shadow-xl;
  }
}
</style>
