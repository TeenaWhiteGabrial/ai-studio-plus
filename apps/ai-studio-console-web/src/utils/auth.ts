import { createRouter, createWebHashHistory } from 'vue-router'

const router = createRouter({
  // 基础路径由 vite.config.ts 的 base 参数控制
  history: createWebHashHistory(),
  routes: [],
})

// 路由守卫：校验 Token + 自动跳转
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const appType = import.meta.env.VITE_APP_TYPE || 'console'

  // 计算当前 appType 的登录页路径
  const loginPath = appType === 'portal' ? '/login' : `/${appType}/login`

  // 未登录，访问非登录页 → 跳转登录
  if (!token && to.path !== loginPath) {
    const redirectUrl = encodeURIComponent(to.fullPath)
    localStorage.setItem('redirectUrl', redirectUrl)
    return loginPath
  }

  // 已登录，访问登录页 → 跳转首页
  if (token && to.path === loginPath) {
    const homeMap: Record<string, string> = {
      admin: '/admin/dashboard',
      console: '/console/dashboard',
      portal: '/',
    }
    return homeMap[appType] || '/'
  }

  const requiredRoles = to.meta?.roles as string[] | undefined
  if (token && requiredRoles?.length) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')
    const roles = userInfo?.roles || []
    if (!requiredRoles.some(role => roles.includes(role))) {
      return appType === 'console' ? '/console/dashboard' : '/'
    }
  }
})

export default router
