export default defineNuxtRouteMiddleware((to) => {
  const authStore = useAuthStore()
  const isPublicRoute = to.path === '/' || to.path === '/login' || to.path.startsWith('/login/')

  if (!isPublicRoute && !authStore.token) {
    const redirect = encodeURIComponent(to.fullPath)
    return navigateTo(`/login?redirect=${redirect}`)
  }
})
