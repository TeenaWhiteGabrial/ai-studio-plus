/**
 * 路由守卫中间件
 * 需要登录的页面：/profile/*, /community/write, /community/ask
 */
export default defineNuxtRouteMiddleware((to) => {
  const authStore = useAuthStore()

  // 需要登录的路由
  const needAuthRoutes = [
    '/profile',
    '/community/write',
    '/community/ask'
  ]

  // 检查是否是需要登录的路由
  const isNeedAuth = needAuthRoutes.some(route => to.path.startsWith(route))

  if (isNeedAuth && !authStore.token) {
    // 未登录，重定向到登录页
    const redirect = encodeURIComponent(to.fullPath)
    return navigateTo(`/login?redirect=${redirect}`)
  }
})
