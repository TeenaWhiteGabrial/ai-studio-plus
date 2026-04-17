import { defineNuxtPlugin } from '#app'
import { useTheme } from '#imports'

/**
 * 主题初始化插件（客户端）
 * 在客户端初始化主题配置，注入 CSS 变量
 */
export default defineNuxtPlugin(async () => {
    const { initTheme } = useTheme()

    try {
        // 初始化主题配置
        await initTheme()
    } catch (error) {
        console.error('客户端初始化主题失败:', error)
    }
})
