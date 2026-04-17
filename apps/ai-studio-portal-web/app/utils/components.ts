// 导入所有需要的组件
import HomeBanner from '~/components/Home/Banner.vue'

export function getComponent(componentName: string){

    // 组件映射表：kebab-case 组件名 -> 组件对象
    const componentsMap: Record<string, any> = {
        'HomeBanner': HomeBanner,
    }

    const component = componentsMap[componentName]
    if (!component) {
        console.warn(`[index.vue] 未找到组件: ${componentName}`)
    }
    return component
}