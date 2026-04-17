import { ROLE_STATUS } from './auth'
/**
 * 通用资源类型
 */
export interface Resource {
    id: string; // 资源ID
    tenantId: string; // 租户ID
    clientId: string; // 客户端ID
    name: string; // 资源名称
    introduction: string; // 资源介绍
    type: string; // 资源类型
    secondaryType: string; // 资源次级类型
    photo: string; // 资源图片地址
    publishTime: string; // 发布时间
    categoryName: string; // 分类名称
    link: string; // 资源链接
    sequence: string; // 排序序号
    createTime: string; // 创建时间
    updateTime: string; // 更新时间
    createUser: string; // 创建人
    updateUser: string; // 更新人
    status: string; // 资源状态
    afferentWay: string; // 传入方式
    industry: string; // 所属行业
    industryNames: string; // 行业名称
    industryIds: string[]; // 行业ID数组
    domain: string; // 所属领域
    domainNames: string; // 领域名称
    domainIds: string[]; // 领域ID数组
    publishStatus: string; // 发布状态
    price: string; // 资源价格
    address: string; // 地址
    addressName: string; // 地址名称
    publishedProcessStatus: string; // 发布流程状态
    organizeId: string; // 组织ID
    measurementUnit: string; // 计量单位
    changeRate: string; // 变化率
    mainSitePublishStatus: string; // 主站发布状态
}

/**
 * 导航栏数据类型
 */
export interface NavigationItem {
    id: string
    columnName: string
    // 跳转链接
    columnLink: string
    // 排序
    sequence: string | number
    // 0 本页面打开 1 新窗口打开
    runType: "0" | "1"
    icon?: string
    child: NavigationItem[]
}

/**
 * 导航栏右侧列表数据类型
 */
export interface WorkBenchItem {
    name: string
    link: string
    needLogin?: boolean
    showRoles?: ROLE_STATUS[]
}

/** 
 * 获取数据方式，枚举类型
 */
export enum GetDataWayEnum {
    RESOURCE = 'Resource',
    API = 'Api',
    STATIC = 'Static',
    JSON = 'Json'
}