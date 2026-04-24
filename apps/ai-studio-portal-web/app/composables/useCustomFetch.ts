import type { FetchOptions, ResponseType } from 'ofetch'
import type { ApiResponse } from '~~/shared/types/response'

/**
 * 简单请求配置项
 */
interface SimpleFetchOptions<T extends ResponseType = 'json'> extends FetchOptions<T> {
    noToken?: boolean
    withBear?: boolean
    /** 手动添加配置表里的 baseURL */
    addBaseUrl?: boolean
    /** 添加项目基础路由 */
    withBaseRouter?: boolean
    /** 请求失败时是否抛出错误（默认 false，返回错误响应对象） */
    throwError?: boolean
}

/**
 * 创建统一的错误响应对象
 */
function createErrorResult<T = null>(message: string): ApiResponse<T> {
    return {
        code: 500,
        data: null as T,
        message,
        msg: message
    }
}

/**
 * 附加认证头
 */
function attachAuthHeader(headers: HeadersInit | undefined, token: string | null) {
    const headerMap = new Headers(headers as any ?? {})
    if (token && !headerMap.has('Authorization')) {
        headerMap.set('Authorization', token)
    }
    return Object.fromEntries(headerMap.entries())
}

/**
 * 验证响应数据结构
 */
function validateResponsePayload(payload: unknown) {
    if (payload === null || payload === undefined) {
        throw new Error('响应数据为空')
    }

    if (typeof payload === 'object' && !Array.isArray(payload)) {
        const hasCode = Object.prototype.hasOwnProperty.call(payload, 'code')
        if (hasCode) {
            const codeValue = (payload as any).code
            if (codeValue === undefined || codeValue === null) {
                throw new Error('响应数据格式不正确：code 值为空')
            }
        }
        if (Object.prototype.hasOwnProperty.call(payload, 'data')) {
            const dataValue = (payload as any).data
            if (dataValue === undefined) {
                throw new Error('响应数据格式不正确：data 值为 undefined')
            }
        }
    }
}

/**
 * 调用钩子函数（支持数组或单个函数）
 */
function callMaybeArrayHook(hook: any, ctx: any) {
    if (!hook) return
    if (Array.isArray(hook)) {
        for (const fn of hook) {
            fn(ctx)
        }
        return
    }
    hook(ctx)
}

/**
 * 统一的 API 请求函数
 * @param url 请求地址
 * @param options 请求配置
 * @returns ApiResponse<T> - 统一的响应格式 { code, data, message, msg }
 */
export async function useSimpleFetch<R = any, T extends ResponseType = 'json'>(
    url: string,
    options: SimpleFetchOptions<T> = {}
): Promise<ApiResponse<R>> {
    const config = useRuntimeConfig()
    const authStore = useAuthStore()
    const rawOptions: Record<string, any> = { ...options }
    const tokenType = config.public.tokenType
    const throwError = rawOptions.throwError ?? false

    // 处理 token 逻辑
    let token = null
    if (!rawOptions.noToken) {
        if (authStore.token) {
            token = tokenType ? `${tokenType} ${authStore.token}` : authStore.token
        }
    }

    const originalOnResponse = rawOptions.onResponse
    const originalOnResponseError = rawOptions.onResponseError

    delete rawOptions.noToken
    delete rawOptions.addBaseUrl
    delete rawOptions.onResponse
    delete rawOptions.onResponseError
    delete rawOptions.throwError

    const commonHeaders = attachAuthHeader(rawOptions.headers, token)
    delete rawOptions.headers

    /**
     * 错误处理
     * @param err 错误对象
     * @returns 错误响应对象或抛出错误
     */
    const handleError = (err: unknown): ApiResponse<R> => {
        console.error('useSimpleFetch 错误:', err)
        const message = err instanceof Error ? err.message : '未知错误'

        if (throwError) {
            throw err
        }
        return createErrorResult<R>(message)
    }

    try {
        // 处理 baseURL
        const isAbsoluteUrl = url.startsWith('http://') || url.startsWith('https://')
        if (isAbsoluteUrl) {
            delete rawOptions.baseURL
        } else if (!rawOptions.baseURL) {
            if (config.public.apiBase) {
                rawOptions.baseURL = config.public.apiBase
            } else if (import.meta.client && !rawOptions.withBaseRouter) {
                rawOptions.baseURL = `${window.location.protocol}//${window.location.host}`
            }
        }

        const fetchOptions: Record<string, any> = {
            ...rawOptions,
            headers: commonHeaders,
            onResponse(context: any) {
                validateResponsePayload(context.response._data)
                callMaybeArrayHook(originalOnResponse, context)
            },
            onResponseError(context: any) {
                console.error('API 请求错误:', context.error)
                callMaybeArrayHook(originalOnResponseError, context)
            }
        }

        const response: ApiResponse<R> = await $fetch<ApiResponse<R>>(url, fetchOptions)

        // 验证响应数据
        validateResponsePayload(response)

        // 处理 401 未授权
        if ((response as any).code === 401) {
            const res = await ElMessageBox.confirm(
                '登录已过期，是否重新登录？',
                'warning',
                {
                    title: '登录已过期',
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).catch(() => null)

            if (res) {
                await goLogout()
                await goLoginPage()
            }
        }

        return response
    } catch (fetchError) {
        return handleError(fetchError)
    }
}
