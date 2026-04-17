export default defineNuxtPlugin(async () => {
    // 在服务端初始化网站配置
    // 这确保在 SSR 时 siteConfig 已经被初始化
    const { initSiteConfig } = useSite()
    try {
        await initSiteConfig()
    } catch (error) {
        // 如果初始化失败，使用默认配置，不影响页面渲染
        console.error('服务端初始化网站配置失败:', error)
    }
})

