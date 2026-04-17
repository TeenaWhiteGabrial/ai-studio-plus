/** 网站配置类型定义 */
export interface SiteConfig {
    tenantId?: string // 租户ID
    name: string           // 网站标题
    description: string    // 网站描述
    logo: string          // 网站 URL
    icon: string       // 网站图标    
    contacts?: string      // 联系方式
}

/** 网站顶部组件类型 */
export interface HEADER_CONFIG_DATA {
    /** 栏目高度 */
    columnHeight: string
    /** 栏目左右边距 */
    columnleftRight: string
    /** 左右内边距 */
    leftRightPadding: string
    /** 固定样式 */
    fixStyle: 'static' | 'relative' | 'absolute' | 'fixed' | 'sticky' | 'inherit' | 'initial' | 'unset'
    /** 栏目背景颜色 */
    columnBgColor: string
    /** 文字颜色 */
    textColor: string
    /** 栏目位置 */
    columnPosition: 'left' | 'center' | 'right' | 'flex-start' | 'flex-end' | 'space-between' | 'space-around'
    /** 是否使用路径匹配 */
    usePathMatch: boolean
    /** 是否使用API */
    useApi: boolean
    /** API路径 */
    apiPath: string
    /** 栏目数据 */
    columnData: import('./resource').NavigationItem[]
    /** 是否显示注册按钮 */
    showRegisterButton: boolean
    /** 工作台列表 */
    workBenchList: Array<{
        /** 名称 */
        name: string
        /** 链接 */
        link: string
        /** 是否需要登录 */
        needLogin: boolean
        /** 显示角色列表 */
        showRoles: string[] | null
    }>
    /** 下拉列表 */
    dropdownList: Array<{
        /** 名称 */
        name: string
        /** 链接 */
        link: string
    }>
}

/** 网站底部组件类型 */
export interface FOOTER_CONFIG_DATA {
    contactsObj: any
    recommendProductions: Array<{ product_name: string; link: string }>
    friendlyLinks: Array<{ name: string; url: string }>
    email: string
    after_sale_hotline: string
    pre_sale_hotline: string
    contact_us: string[]
}

/** 默认网站配置 */
export const defaultSiteConfig: SiteConfig = {
    tenantId: '',
    name: 'AI Studio',
    description: 'AI 应用开发平台',
    logo: '',
    icon: '/favicon.ico',
    contacts: '',
}
