/**
 * 主题色 Composable
 * 提供主题色管理、颜色衍生、CSS 变量注入等功能
 */

import type { ThemeConfig } from '~~/shared/types/theme'
import { defaultTheme } from '~~/shared/types/theme'

// 全局主题状态
const themeConfig = ref<ThemeConfig>(defaultTheme)

/**
 * HEX 转 HSLA
 */
function hexToHsla(hex: string): [number, number, number, number] {
    // 移除 # 符号
    hex = hex.replace(/^#/, '')

    // 处理 8 位 HEX（包含 Alpha）
    let alpha = 1
    if (hex.length === 8) {
        alpha = parseInt(hex.slice(6, 8), 16) / 255
        hex = hex.slice(0, 6)
    }

    const r = parseInt(hex.slice(0, 2), 16) / 255
    const g = parseInt(hex.slice(2, 4), 16) / 255
    const b = parseInt(hex.slice(4, 6), 16) / 255

    const max = Math.max(r, g, b)
    const min = Math.min(r, g, b)
    let h = 0
    let s = 0
    const l = (max + min) / 2

    if (max !== min) {
        const d = max - min
        s = l > 0.5 ? d / (2 - max - min) : d / (max + min)

        switch (max) {
            case r:
                h = ((g - b) / d + (g < b ? 6 : 0)) / 6
                break
            case g:
                h = ((b - r) / d + 2) / 6
                break
            case b:
                h = ((r - g) / d + 4) / 6
                break
        }
    }

    return [h * 360, s * 100, l * 100, alpha]
}

/**
 * HSLA 转 HEX
 */
function hslaToHex(h: number, s: number, l: number, a: number): string {
    s /= 100
    l /= 100

    const c = (1 - Math.abs(2 * l - 1)) * s
    const x = c * (1 - Math.abs(((h / 60) % 2) - 1))
    const m = l - c / 2

    let r = 0
    let g = 0
    let b = 0

    if (h >= 0 && h < 60) {
        r = c; g = x; b = 0
    } else if (h >= 60 && h < 120) {
        r = x; g = c; b = 0
    } else if (h >= 120 && h < 180) {
        r = 0; g = c; b = x
    } else if (h >= 180 && h < 240) {
        r = 0; g = x; b = c
    } else if (h >= 240 && h < 300) {
        r = x; g = 0; b = c
    } else if (h >= 300 && h < 360) {
        r = c; g = 0; b = x
    }

    const toHex = (n: number) => {
        const hex = Math.round((n + m) * 255).toString(16)
        return hex.length === 1 ? '0' + hex : hex
    }

    // 如果 alpha 为 1，返回 6 位 HEX，否则返回 8 位
    if (a >= 1) {
        return `#${toHex(r)}${toHex(g)}${toHex(b)}`
    }
    return `#${toHex(r)}${toHex(g)}${toHex(b)}${Math.round(a * 255).toString(16).padStart(2, '0')}`
}

/**
 * 根据主题色衍生其他颜色
 */
function deriveColors(primaryColor: string): ThemeConfig {
    const [h, s, l, a] = hexToHsla(primaryColor)

    return {
        primary: primaryColor, // 主题色
        primaryDark: hslaToHex(h, s, Math.max(10, l - 10), a),     // 稍深的主题色
        primaryDarker: hslaToHex(h, s, Math.max(5, l - 15), a),     // 更深的主题色
        primaryPale: hslaToHex(h, s, Math.max(10, l + 10), a),  // 浅色版本 - 调整为比 primary 更浅但比 primaryLight 更深

        primaryLight: hslaToHex(h, Math.max(10, s - 20), Math.min(95, l + 30), a),  // 白浅色版本
        primaryLighter: hslaToHex(h, Math.max(10, s - 20), Math.min(95, l + 40), a),   // 更白浅色版本
        primaryFaint: hslaToHex(h, Math.max(5, s - 80), Math.min(98, l + 40), a),   // 最浅色版本
        primarySubtle: hslaToHex(h, s, l, 0.1),   // 微妙版本（10% 透明度）
        primaryTransparent: hslaToHex(h, s, l, 0.15),   // 半透明版本（20% 透明度）
        primaryText: l > 50 ? '#ffffff' : '#333',              // 根据亮度选择文字颜色
    }
}

/**
 * 将衍生的颜色应用到 CSS 变量
 */
function applyColorsToCSS(colors: ThemeConfig): void {
    if (import.meta.client) {
        const root = document.documentElement
        // 设置主题相关的 CSS 变量
        root.style.setProperty('--color-primary', colors.primary)
        root.style.setProperty('--color-primary-dark', colors.primaryDark)
        root.style.setProperty('--color-primary-darker', colors.primaryDarker)
        root.style.setProperty('--color-primary-pale', colors.primaryPale)
        root.style.setProperty('--color-primary-light', colors.primaryLight)
        root.style.setProperty('--color-primary-lighter', colors.primaryLighter)
        root.style.setProperty('--color-primary-faint', colors.primaryFaint)
        root.style.setProperty('--color-primary-transparent', colors.primaryTransparent)
        root.style.setProperty('--color-primary-subtle', colors.primarySubtle)
        root.style.setProperty('--color-primary-text', colors.primaryText)
    }
}

/**
 * 主题色 Composable
 */
export const useTheme = () => {
    /**
     * 从 API 获取主题配置
     */
    const fetchThemeConfig = async (): Promise<void> => {
        themeConfig.value = defaultTheme
        applyColorsToCSS(defaultTheme)
        // try {
        //     // 调用 API 获取主题色
        //     const response = await $fetch<ApiResponse<string>>('/api/theme/config')

        //     if (response.code === 200 && response.data) {
        //         const primaryColor = response.data

        //         // 衍生颜色并应用
        //         const colors = deriveColors(primaryColor)
        //         themeConfig.value = colors
        //         applyColorsToCSS(colors)
        //     } else {
        //         throw new Error(response.message || '获取主题配置失败')
        //     }
        // } catch (err) {
        //     console.error('获取主题配置失败:', err)

        //     // API 失败时使用默认主题
        //     themeConfig.value = defaultTheme
        //     applyColorsToCSS(defaultTheme)
        // }
    }

    /**
     * 更新主题配置
     */
    const updateTheme = (newTheme: Partial<ThemeConfig>): void => {
        themeConfig.value = { ...themeConfig.value, ...newTheme }
        applyColorsToCSS(themeConfig.value)
    }

    /**
     * 重置为默认主题
     */
    const resetTheme = (): void => {
        themeConfig.value = defaultTheme
        applyColorsToCSS(defaultTheme)
    }

    /**
     * 初始化主题
     */
    const initTheme = async (): Promise<void> => {
        // 首先应用默认主题
        themeConfig.value = defaultTheme
        applyColorsToCSS(defaultTheme)

        // 从 API 获取主题配置
        await fetchThemeConfig()
    }

    return {
        // 状态
        themeConfig,

        // 方法
        fetchThemeConfig,
        updateTheme,
        resetTheme,
        initTheme
    }
}
