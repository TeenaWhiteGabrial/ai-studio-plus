/**
 *
 * maxkey登录相关信息，以此为准
 */

export const useAuthStore = defineStore('authStore', {
    state: (): UserInfo => ({
        userName: '', // 显示用户名
        phone: '', // 手机号
        email: '', // 邮箱
        token: getCookieToken(), // token
        userId: '', // 用户ID
        roles:[], // 角色权限组
    }),
    actions: {
        /**
         * 清除登录信息
         */
        clearLoginInfo() {
            this.userName = ''
            this.phone = ''
            this.email = ''
            this.token = ''
            this.userId = ''
            this.roles = []
            removeCookieToken()
        },
        /**
         * 设置除token外的登录信息
         * @param info 登录信息
         */
        setLoginInfo(info: UserInfo) {
            info.userId && (this.userId = info.userId)
            info.userName && (this.userName = info.userName)
            info.phone && (this.phone = info.phone)
            info.email && (this.email = info.email)
            if (info.roles && info.roles.length > 0) {
                // 使用Set对象去除重复角色
                this.roles = [...new Set([...(this.roles || []), ...info.roles])]
            }
        },
        /**
         * 设置 token，同时将已登录设为true
         * @param token token
         */
        setToken(token: string) {
            this.token = token
            setCookieToken(token)
        },
        /**
         * 自有登录方法
         * @param username 用户名
         * @param password 密码
         * @param code 验证码
         * @param uuid 验证码key
         * @returns 登录结果
         */
        async ownLogin(username: string, password: string, code: string, uuid: string){
            try {
                // 登录前先清理一遍登录信息
                this.clearLoginInfo()
                const response = await useSimpleFetch<{
                    code: number
                    msg: string
                    data: { access_token: string,expires_in: number }
                }>('/prod-api/auth/login', {
                    method: 'POST',
                    body: { 
                        username,
                        password,
                        code,
                        uuid
                    }})
                if (response.code === 200 && response.data) {
                    // 存储token到cookie
                    this.setToken(response.data.data.access_token)
                    return { success: true, message: '登录成功' }
                } else {
                    return { success: false, message: response.msg || '登录失败' }
                }
            } catch (err) {
                return { success: false, message: err instanceof Error ? err.message : '登录请求失败' }
            }
        }, 

        async ownLogout(){

            const res = await useSimpleFetch<{
                code: number
                msg: string
            }>('/prod-api/auth/logout',{
                method:'post'
            })
            if (res.code === 200) {
                this.clearLoginInfo()
                return { success: true, message: '登出成功' }
            } else {
                return { success: false, message: res.msg || '登出失败' }
            }
        }
    }
})