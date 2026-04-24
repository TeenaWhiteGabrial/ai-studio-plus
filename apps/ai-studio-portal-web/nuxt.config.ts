// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  app: {
    baseURL: '/ai-studio-portal-web/',
  },
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
  css: ['~/assets/css/csdn-theme.css', '~/assets/css/richText.css'],
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
      apiBase: import.meta.env.NUXT_API_BASE_URL || '/',
      loginType: import.meta.env.NUXT_LOGIN_TYPE,
      loginUrl: import.meta.env.NUXT_LOGIN_URL,
      logoutUrl: import.meta.env.NUXT_LOGOUT_URL,
      registerUrl: import.meta.env.NUXT_REGISTER_URL,
      tokenType: import.meta.env.NUXT_TOKEN_TYPE,
      baseRouter: import.meta.env.NUXT_APP_BASE_URL,

    }
  },
  nitro: {
    devProxy: {
      '/auth': {
        target: 'http://localhost:8080/ai-studio/v1/auth',
        changeOrigin: true,
      },
      '/portal': {
        target: 'http://localhost:8080/ai-studio/v1/portal',
        changeOrigin: true,
      },
      '/system': {
        target: 'http://localhost:8080/ai-studio/v1/system',
        changeOrigin: true,
      },
      '/cloudMarket': {
        target: 'http://localhost:8080/ai-studio/v1/cloudMarket',
        changeOrigin: true,
      },
      '/file': {
        target: 'http://localhost:8080/ai-studio/v1/file',
        changeOrigin: true,
      },
      '/oss': {
        target: 'http://localhost:8080/ai-studio/v1/oss',
        changeOrigin: true,
      },
    },
  },
})
