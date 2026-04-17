// 公共方法
interface navigateConfig {
    target?: '_blank' | '_parent' | '_self' | '_top' | (string & {}),
    needLogin?: boolean
}
/**
 * 智能跳转
 * @param url 
 * @param target 
 */
export async function autoNavigate(url: string, config: navigateConfig = {
    target:'_self',
    needLogin:false
}){
    const authStore = useAuthStore()
    if(config.needLogin && !authStore.token){
        goLoginPage()
    } else {
        await navigateTo(url, {
            external: isExternalUrl(url),
            open: {
                target: config.target || '_self'
            }
        })
    }
    
}

export function goToHome(){
    window.location.href = '/'
}


/**
 * 从公有资源列表、Api请求数据、静态数据中获取指定的数据，可裁剪指定长度的列表
 * @param way 获取数据的方式
 * @param apiPath Api路径
 * @param resourceType 资源类型
 * @param staticData 静态数据
 * @param length 裁剪长度
 * @returns 裁剪后的列表
 */
export async function handleDataGet<T>(way:GetDataWayEnum,apiPath:string,resourceType:string,staticData:T[],length:number = NaN): Promise<T[]>{
        
        let resData: T[] = []
        if(way === GetDataWayEnum.STATIC){
            resData = staticData
        } else if(way === GetDataWayEnum.RESOURCE){
            const resourceList:any[] = []
            resData =  resourceList.filter(item=> item.type === resourceType) as T[]    
        } else if(way === GetDataWayEnum.API){
            const { code, data, message } = await useSimpleFetch(apiPath)
            if(code === 200 && data){
                resData = data as T[]
            } else {
                console.log('Api请求数据失败：',message)
                ElMessage.error('Api请求数据失败：' + message)        
            }    
        } else {        
            console.log('错误的请求数据方式：',way)    
        }        
        if(Number.isNaN(length)){
            return resData    
        } else {        
            return resData.slice(0,length)    
        }
}