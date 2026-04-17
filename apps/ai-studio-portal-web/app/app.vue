<template>
  <NuxtLayout>
    <NuxtPage />
  </NuxtLayout>
</template>

<script setup lang="ts">
  // 使用网站配置
  const { siteConfig } = useSite()

  // 动态设置页面标题
  useHead(() => ({
    title: siteConfig.name || 'AI Studio',
    meta: [
      { name: 'description', content: siteConfig.description || 'AI 应用开发平台' }
    ],
    link: [
      { rel: 'icon', type: 'image/png', href: siteConfig.icon || '/favicon.ico' }
    ]
  }))
</script>