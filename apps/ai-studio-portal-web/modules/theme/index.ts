/**
 * 主题模块
 * 负责初始化动态主题色、CSS 变量注入
 */
import { defineNuxtModule, addPlugin, addImportsDir, createResolver } from '@nuxt/kit'

export interface ModuleOptions {
    /** 默认主题色 */
    defaultPrimaryColor?: string
    /** 是否启用主题持久化 */
    enablePersistence?: boolean
}

export default defineNuxtModule<ModuleOptions>({
    meta: {
        name: 'theme',
        configKey: 'theme',
        version: '^4.0.0'
    },
    setup(options, nuxt) {
        const resolver = createResolver(import.meta.url)

        // 将选项传递到运行时
        nuxt.options.runtimeConfig.public.theme = {
            defaultPrimaryColor: options.defaultPrimaryColor ?? '#3b82f6',
            enablePersistence: options.enablePersistence ?? true
        }

        // 添加客户端插件
        addPlugin(resolver.resolve('./runtime/plugins/theme.client'))

        // 添加服务端插件（用于 SSR 主题初始化）
        addPlugin(resolver.resolve('./runtime/plugins/theme.server'))

        // 自动导入 composables
        addImportsDir(resolver.resolve('./runtime/composables'))

        // 添加类型定义
        nuxt.hook('prepare:types', (options) => {
            options.references.push({ path: resolver.resolve('./types.d.ts') })
        })
    }
})
