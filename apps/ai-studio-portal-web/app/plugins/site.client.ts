export default defineNuxtPlugin(async () => {
    // 初始化网站配置
    const { initSiteConfig } = useSite()
    await initSiteConfig()
})
