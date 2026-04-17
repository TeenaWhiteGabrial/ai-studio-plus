import { defineNuxtPlugin } from '#app'
import { useTheme } from '#imports'

/**
 * 主题初始化插件（服务端）
 * 在服务端初始化主题配置，确保 SSR 渲染时主题已生效
 */
export default defineNuxtPlugin(async () => {
    const { initTheme } = useTheme()

    try {
        // 服务端初始化主题
        await initTheme()
    } catch (error) {
        // 服务端失败不影响渲染，使用默认主题
        console.error('服务端初始化主题失败:', error)
    }
})
