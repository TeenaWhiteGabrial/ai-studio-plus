import type { SiteConfig } from '~~/shared/types/site'
import { defaultSiteConfig } from '~~/shared/types/site'

// 全局网站配置状态
let siteConfig = reactive<SiteConfig>(defaultSiteConfig)

/**
 * 网站配置管理 Composable
 */
export const useSite = () => {
    /**
     * 从 API 获取网站配置
     */
    const fetchSiteConfig = async (): Promise<void> => {
        siteConfig = defaultSiteConfig
        // try {
        //     // 调用 API 获取网站配置
        //     const response = await $fetch<ApiResponse<SiteConfig>>('/api/site/config')
        //     console.log('获取网站配置:', response)
        //     if (response.code === 200 && response.data) {
        //         siteConfig = { ...defaultSiteConfig, ...response.data }

        //         // 更新页面标题和描述
        //         if (import.meta.client) {
        //             document.title = siteConfig.name

        //             // 更新 meta 描述
        //             const metaDescription = document.querySelector('meta[name="description"]')
        //             if (metaDescription && siteConfig.description) {
        //                 metaDescription.setAttribute('content', siteConfig.description)
        //             }

        //             // 更新 favicon
        //             if (siteConfig.icon) {
        //                 const favicon = document.querySelector('link[rel="icon"]') as HTMLLinkElement
        //                 if (favicon) {
        //                     favicon.href = siteConfig.icon
        //                 }
        //             }
        //         }
        //     }
        // } catch (err) {
        //     console.error('获取网站配置失败:', err)
        //     // API 失败时使用默认配置
        //     siteConfig = defaultSiteConfig
        // }
    }

    /**
     * 初始化网站配置
     */
    const initSiteConfig = async (): Promise<void> => {
        // 首先应用默认配置
        siteConfig = defaultSiteConfig

        // 从 API 获取配置
        await fetchSiteConfig()
    }

    return {
        // 标题 

        siteConfig: readonly(siteConfig),
        // 方法
        fetchSiteConfig,
        initSiteConfig
    }
}
