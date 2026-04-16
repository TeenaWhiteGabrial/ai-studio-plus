export default defineNuxtRouteMiddleware((to) => {
  if (!import.meta.client) return

  const publicPaths = ['/', '/login', '/resource', '/community', '/search']
  const isPublic = publicPaths.some(path => to.path.startsWith(path))

  if (!isPublic) {
    const token = localStorage.getItem('token')
    if (!token) {
      return navigateTo('/login')
    }
  }
})
