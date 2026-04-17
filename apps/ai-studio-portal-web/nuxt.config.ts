// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  modules: [
    './modules/theme',
    '@unocss/nuxt',
    '@nuxt/icon',
    '@pinia/nuxt',
    'pinia-plugin-persistedstate/nuxt',
    '@element-plus/nuxt',
    'nuxt-swiper',
    '@nuxt/image',
  ],
  elementPlus: {
    defaultLocale:'zh-cn'
  },
  piniaPluginPersistedstate: {
    storage: 'cookies',
    cookieOptions: {
      sameSite: 'lax'
    }
  },
  compatibilityDate: '2025-09-11',
  devtools: { enabled: true },
  css: ['~/assets/css/richText.css'],
  icon: {
    collections: ['material-symbols'] // 明确指定集合
  },
  router: {
    options: {
      hashMode: false  // 不启用哈希路由模式
    }
  },
  runtimeConfig: {
    public: {
      apiBase: import.meta.env.NUXT_API_BASE_URL, 
      loginType: import.meta.env.NUXT_LOGIN_TYPE,
      loginUrl: import.meta.env.NUXT_LOGIN_URL,
      logoutUrl: import.meta.env.NUXT_LOGOUT_URL,
      registerUrl: import.meta.env.NUXT_REGISTER_URL,
      tokenType: import.meta.env.NUXT_TOKEN_TYPE,
      baseRouter: import.meta.env.NUXT_APP_BASE_URL,
      supplierUrl: import.meta.env.NUXT_SUPPLIER_MICRO_URL

    }
  },
})