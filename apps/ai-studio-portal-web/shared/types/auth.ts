//角色身份枚举
export enum ROLE_STATUS {
    // 尚未实名认证
    COMMON = 'common',
    // 个人认证
    PERSONAL = 'personal',
    // 企业认证
    BUSINESS = 'business',
    // 服务商认证
    SERVICE_PROVIDER = 'service_provider',
    /** 管理员 */
    ADMIN = 'admin'
}

// Token 响应数据
export interface TokenResponse {
    access_token: string
    token_type?: string
    expires_in?: number
    refresh_token?: string
}

// 用户信息
export interface UserInfo {
    /** 登录用户ID */
    userId?: string
    userName?: string
    phone?: string
    email?: string
    id?: string
    /** 头像 */
    avatar?: string
    /** 角色列表 */
    roles?: ROLE_STATUS[]
    /**
     * 用户token
     */
    token?: string
}