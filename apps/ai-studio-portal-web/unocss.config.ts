
import {
    defineConfig,
    presetAttributify,
    presetIcons,
    presetTypography,
    transformerDirectives,
    transformerVariantGroup,
} from 'unocss'

import { presetWind3 } from '@unocss/preset-wind3'

export default defineConfig({
    attributify: true,
    nuxtLayers: true,
    // 优化构建性能
    // 优化构建，减少扫描范围
    content: {
        // 明确指定扫描的文件模式，减少扫描范围
        filesystem: [
            'app/**/*.{vue,js,ts,jsx,tsx}',
            'server/**/*.{ts,js}',
            'shared/**/*.{ts,js}'
        ]
    },
    
    shortcuts: [
        // 主题色相关的快捷类
        // 注意：使用 CSS 变量而不是直接使用 primary-dark，因为它在 theme.colors 中定义为 hover 和 active
        ['btn-primary', 'border-none bg-primary-active hover:bg-primary-hover active:bg-[var(--color-primary-darker)] text-primary-text px-4 py-1 rounded-md font-medium transition-colors duration-200 text-base cursor-pointer'],
        ['btn-primary-light', 'bg-primary-light hover:bg-primary text-gray-800 px-4 py-2 rounded-md font-medium transition-colors duration-200'],
        ['btn-primary-outline', 'text-dark3Text text-base bg-transparent border-solid border-1 border-[#d9d9d9] cursor-pointer py-2 px-5 transition-all duration-300 rounded-sm hover:text-primary hover:border-primary'],
        // 鼠标悬浮时的卡片样式
        ['card-hover-shadow', 'hover:shadow-lg hover:scale-102 transition cursor-pointer'],
    ],
    theme: {
        colors: {
            // 主题色号及其衍生色号，使用 CSS 变量
            primary: {
                DEFAULT: 'var(--color-primary)',
                hover: 'var(--color-primary-dark)',
                active: 'var(--color-primary-darker)',
                pale: 'var(--color-primary-pale)',
                paler: 'var(--color-primary-paler)',
                light: 'var(--color-primary-light)',
                lighter: 'var(--color-primary-lighter)',
                faint: 'var(--color-primary-faint)',
                subtle: 'var(--color-primary-subtle)',
                transparent: 'var(--color-primary-transparent)',
                text: 'var(--color-primary-text)',
            },
            dark3Text: '#333',
        },
        breakpoints: {
            'sm': '640px',
            'md': '768px',
            'lg': '1024px',
            'xl': '1280px',
            '2xl': '1536px',
            '3xl': '1920px',
        },
    },

    presets: [
        // 默认预设，包括Tailwind CSS, Windi CSS, Bootstrap,是这些的通用超集
        presetWind3(),
        presetAttributify(),
        // 优化图标预设，减少图标库大小
        presetIcons({
            // 只加载使用的图标集合
            collections: {
                // 使用 CDN 加载图标，减少构建时间
                // 或者只加载本地需要的图标
            },
            // 减少图标转换开销
            extraProperties: {
                'display': 'inline-block'
            }
        }),
        presetTypography(),
    ],
    transformers: [
        transformerDirectives(),
        transformerVariantGroup(),
    ],
    rules: [
        [
            /^text-overflow-(\d+)$/,
            ([_, d]) =>
                d === '1'
                    ? { 'white-space': 'nowrap', overflow: 'hidden', 'text-overflow': 'ellipsis' }
                    : {
                        display: '-webkit-box',
                        '-webkit-line-clamp': d,
                        'line-clamp': d,
                        '-webkit-box-orient': 'vertical',
                        'box-orient': 'vertical',
                        overflow: 'hidden',
                        'text-overflow': 'ellipsis'
                    }
        ]
    ],
})
