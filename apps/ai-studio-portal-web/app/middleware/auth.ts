/**
 * 路由守卫中间件
 * 需要登录的页面：除首页和登录页以外的门户二级页
 */
export default defineNuxtRouteMiddleware((to) => {
  const authStore = useAuthStore()

  const publicRoutes = ['/', '/login']
  const isPublicRoute = publicRoutes.some(route => to.path === route || to.path.startsWith(`${route}/`))

  if (!isPublicRoute && !authStore.token) {
    const redirect = encodeURIComponent(to.fullPath)
    return navigateTo(`/login?redirect=${redirect}`)
  }
})
