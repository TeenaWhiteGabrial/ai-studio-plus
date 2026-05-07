import type { SiteConfig } from '~~/shared/types/site'
import { defaultSiteConfig } from '~~/shared/types/site'

// 全局网站配置状态
const siteConfig = reactive<SiteConfig>({ ...defaultSiteConfig })

interface SiteConfigPayload {
    site_name?: string
    siteName?: string
    site_description?: string
    siteDescription?: string
    logo_url?: string
    logoUrl?: string
    icon_url?: string
    iconUrl?: string
    footer_text?: string
    footerText?: string
    footer_copyright?: string
    footerCopyright?: string
    footer_record?: string
    footerRecord?: string
    footer_links?: string
    footerLinks?: string
    contacts?: string
}

interface ApiResponse<T> {
    code: number
    message: string
    data: T
}

function parseFooterLinks(value?: string) {
    if (!value) return defaultSiteConfig.footerLinks || []
    try {
        const parsed = JSON.parse(value)
        return Array.isArray(parsed) ? parsed : defaultSiteConfig.footerLinks || []
    } catch {
        return defaultSiteConfig.footerLinks || []
    }
}

function toSiteConfig(payload?: SiteConfigPayload): SiteConfig {
    if (!payload) return { ...defaultSiteConfig }
    return {
        ...defaultSiteConfig,
        name: payload.site_name ?? payload.siteName ?? defaultSiteConfig.name,
        description: payload.site_description ?? payload.siteDescription ?? defaultSiteConfig.description,
        logo: payload.logo_url ?? payload.logoUrl ?? defaultSiteConfig.logo,
        icon: payload.icon_url ?? payload.iconUrl ?? defaultSiteConfig.icon,
        footerText: payload.footer_text ?? payload.footerText ?? defaultSiteConfig.footerText,
        footerCopyright: payload.footer_copyright ?? payload.footerCopyright ?? defaultSiteConfig.footerCopyright,
        footerRecord: payload.footer_record ?? payload.footerRecord ?? defaultSiteConfig.footerRecord,
        footerLinks: parseFooterLinks(payload.footer_links ?? payload.footerLinks),
        contacts: payload.contacts ?? defaultSiteConfig.contacts,
    }
}

/**
 * 网站配置管理 Composable
 */
export const useSite = () => {
    /**
     * 从 API 获取网站配置
     */
    const fetchSiteConfig = async (): Promise<void> => {
        const runtimeConfig = useRuntimeConfig()
        try {
            const response = await $fetch<ApiResponse<SiteConfigPayload>>('/portal/site/config', {
                baseURL: runtimeConfig.public.apiBase,
            })
            if (response.code === 200) {
                Object.assign(siteConfig, toSiteConfig(response.data))
            }
        } catch (err) {
            console.error('获取网站配置失败:', err)
            Object.assign(siteConfig, defaultSiteConfig)
        }
    }

    /**
     * 初始化网站配置
     */
    const initSiteConfig = async (): Promise<void> => {
        // 首先应用默认配置
        Object.assign(siteConfig, defaultSiteConfig)

        // 从 API 获取配置
        await fetchSiteConfig()
    }

    return {
        // 标题 

        siteConfig,
        // 方法
        fetchSiteConfig,
        initSiteConfig
    }
}
