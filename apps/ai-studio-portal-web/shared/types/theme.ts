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
    primary: '#2563eb',
    primaryDark: '#1d4ed8',
    primaryDarker: '#1e40af',
    primaryPale: '#3b82f6',
    primaryLight: '#dbeafe',
    primaryLighter: '#eff6ff',
    primaryFaint: '#f5f8ff',
    primaryTransparent: '#2563eb80',
    primarySubtle: '#2563eb33',
    primaryText: '#ffffff'
}
