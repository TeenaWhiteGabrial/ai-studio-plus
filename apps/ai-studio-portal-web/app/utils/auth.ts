/**
 * 跳转登录页面逻辑，分2种：maxkey、own
 */
export async function goLoginPage(url?: string) {
  const config = useRuntimeConfig()
  const router = useRouter()
  const appBase = router.options.history.base || '/'
  const normalizeBase = (base: string) => base.endsWith('/') ? base : `${base}/`
  const normalizedBase = normalizeBase(appBase)
  const normalizeRedirect = (redirect: string) => {
    if (!redirect) {
      return '/'
    }
    let target = decodeURIComponent(redirect)
    if (!target.startsWith('/')) {
      target = `/${target}`
    }
    if (normalizedBase !== '/' && target.startsWith(normalizedBase)) {
      target = `/${target.slice(normalizedBase.length)}`.replace(/^\/+/, '/')
    }
    return target || '/'
  }

  const currentPath = import.meta.client ? `${window.location.pathname}${window.location.search}` : '/'
  const redirectUrl = normalizeRedirect(url || currentPath)

  if (['maxkey'].includes(config.public.loginType)) {
    const state = Math.floor(Math.random() * 10000000000)
    await navigateTo(`${config.public.loginUrl}${window.location.origin}${window.location.pathname}&state=${state}`, { external: true })
  } else {
    await navigateTo(`/login?redirect=${encodeURIComponent(redirectUrl)}`)
  }
}

/**
 * 跳转注册页面
 */
export async function goRegisterPage() {
  const config = useRuntimeConfig()
  const registerUrl = config.public.registerUrl
  await navigateTo(registerUrl, { external: isExternalUrl(registerUrl) })
}

/**
 * 退出逻辑
 */
export async function goLogout() {
  const authStore = useAuthStore()
  const config = useRuntimeConfig()
  if (['maxkey'].includes(config.public.loginType)) {
    authStore.clearLoginInfo()
    await navigateTo(`${config.public.logoutUrl}${window.location.origin}${window.location.pathname}`, { external: true })
  } else {
    await authStore.logout()
    await navigateTo('/')
  }
}

export function clearCode() {
  const url = new URL(window.location.href)
  url.searchParams.delete('code')
  window.history.replaceState({}, '', url.toString())
}
