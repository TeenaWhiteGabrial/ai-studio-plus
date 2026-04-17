// 全局类型声明文件
import { Ref } from 'vue'

declare global {
    // Nuxt 自动导入的函数
    const ref: typeof import('vue')['ref']
    const readonly: typeof import('vue')['readonly']
    const $fetch: typeof import('ofetch')['$fetch']

    // 主题相关
    const useTheme: () => import('./theme').ThemeComposable
}

export { }
