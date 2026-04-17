// 主题配置类型定义
export interface ThemeConfig {
    primary: string          // 主题色（支持6位或8位HEX，8位包含Alpha通道）
    primaryDark: string     // 稍深
    primaryDarker: string    // 更深
    primaryPale: string     // 稍浅
    primaryLight: string     // 白浅色
    primaryLighter: string    // 更白浅
    primaryFaint: string      // 最浅色
    primaryTransparent: string // 半透明版本（50%透明度）
    primarySubtle: string     // 微透明版本（20%透明度）
    primaryText: string      // 主题色上的文字颜色
}

// 默认主题配置
export const defaultTheme: ThemeConfig = {
    primary: '#3b82f6',
    primaryDark: '#2563eb',
    primaryDarker: '#1d4ed8',
    primaryPale: '#1d4ed8',
    primaryLight: '#dbeafe',
    primaryLighter: '#f0f9ff',
    primaryFaint: '#f8fafc',
    primaryTransparent: '#3b82f680',
    primarySubtle: '#3b82f633',
    primaryText: '#ffffff'
}