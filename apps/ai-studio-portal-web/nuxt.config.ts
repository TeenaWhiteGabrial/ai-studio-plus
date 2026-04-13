// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },

  ssr: true,

  runtimeConfig: {
    public: {
      apiBase: process.env.NUXT_PUBLIC_API_BASE || '/ai-studio/v1',
      appType: process.env.NUXT_PUBLIC_APP_TYPE || 'portal',
    }
  },

  app: {
    head: {
      title: 'AI Studio - 资源门户',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: 'AI Studio 资源门户，提供 Skill、MCP、Plugin、教程等资源下载' },
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
      ],
    },
  },

  css: [
    'element-plus/dist/index.css',
  ],

  build: {
    transpile: ['element-plus'],
  },

  vite: {
    server: {
      proxy: {
        '/ai-studio/v1': {
          target: 'http://localhost:8080',
          changeOrigin: true,
        },
      },
    },
  },
})
