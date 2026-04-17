/**
 * 跳转登录页面逻辑，分2种：maxkey、own
 */
export async function goLoginPage(url?:string) {
    const redirectUrl = url || window.location.pathname + window.location.search
    const config = useRuntimeConfig()
    if (['maxkey'].includes(config.public.loginType)) {
        const state = Math.floor(Math.random() * 10000000000)
        await navigateTo(`${config.public.loginUrl}${window.location.origin}${window.location.pathname}&state=${state}`, { external: true })
    } else {
        await navigateTo(`/login?redirect=${redirectUrl}`)
    } 
}

/**
 * 跳转注册页面
 */
export async function goRegisterPage(){
    const config = useRuntimeConfig()
    const registerUrl = config.public.registerUrl
    await navigateTo(registerUrl, { external: isExternalUrl(registerUrl) })
}

/**
 * 登出逻辑
 */
export async function goLogout() {
    const authStore = useAuthStore()
    const config = useRuntimeConfig()
    if (['maxkey'].includes(config.public.loginType)){
        authStore.clearLoginInfo()
        await navigateTo(`${config.public.logoutUrl}${window.location.origin}${window.location.pathname}`, { external: true, })
    } else {
        const {success, message} = await authStore.ownLogout()
        if (success){
            // ElMessage.success(message)
            navigateTo('/')
        } else {
            ElMessage.error(`登出失败：${message}`)
        }
    }
}

export function clearCode(){
    // 获取当前 URL
    const url = new URL(window.location.href);
    // 删除 code 参数
    url.searchParams.delete('code');
    // 生成新的 URL（包含剩余参数）
    const newUrl = url.pathname + url.search;
    // 替换地址栏
    window.history.replaceState({}, '', url.toString());
}
