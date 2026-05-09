import axios from 'axios'
import { ElLoading, ElMessage } from 'element-plus'
import type { LoadingInstance } from 'element-plus/es/components/loading/src/loading'
import router from '@/router'

// API 基础路径
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'
// 业务路由前缀
const BASE_ROUTER = import.meta.env.VITE_BASE_ROUTER

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  withCredentials: true, // 允许携带 Cookie/认证信息
})

let pendingCount = 0
let loadingTimer: number | undefined
let loadingInstance: LoadingInstance | null = null

function startGlobalLoading() {
  pendingCount += 1
  if (pendingCount === 1) {
    loadingTimer = window.setTimeout(() => {
      loadingInstance = ElLoading.service({
        target: document.querySelector('.main-content') || document.body,
        lock: true,
        text: '加载中...',
        background: 'rgba(255, 255, 255, 0.6)',
      })
    }, 180)
  }
}

function stopGlobalLoading() {
  pendingCount = Math.max(0, pendingCount - 1)
  if (pendingCount > 0) return
  if (loadingTimer) {
    window.clearTimeout(loadingTimer)
    loadingTimer = undefined
  }
  if (loadingInstance) {
    loadingInstance.close()
    loadingInstance = null
  }
}

// 请求拦截器：自动携带 Token 根据请求路径设置 baseURL
request.interceptors.request.use(
  (config) => {
    startGlobalLoading()
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 判断是否为认证相关请求
    if (config.url?.startsWith('/auth/')) {
      // auth 接口直接请求 /ai-studio/v1/auth/*
      config.baseURL = API_BASE_URL
    } else {
      // 业务接口请求 /ai-studio/v1/admin/*
      config.baseURL = `${API_BASE_URL}${BASE_ROUTER}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理错误
request.interceptors.response.use(
  (response) => {
    stopGlobalLoading()
    // 如果配置了 transformResponse 直接返回原始数据，则跳过统一封装处理
    if (response.config.transformResponse) {
      return response.data
    }
    // 如果 responseType 为 blob，直接返回原始响应
    if (response.config.responseType === 'blob') {
      return response
    }
    const data = response.data
    // 业务错误码非 200 也走错误处理
    if (data.code !== 200) {
      if (!(response.config as any).silentError) {
        ElMessage.error(data.message || '请求失败')
      }
      return Promise.reject(new Error(data.message || data.data?.message || '请求失败'))
    }
    // 直接返回整个响应对象，保持 code、data、message 结构
    return response.data
  },
  (error) => {
    stopGlobalLoading()
    const isLoginRequest = error.config?.url?.includes('/auth/login')
    if (error.response?.status === 401) {
      if (!isLoginRequest) {
        // 非登录请求的 401 才跳转登录页
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        router.push('/admin/login')
        ElMessage.warning('登录已过期，请重新登录')
      } else {
        // 登录请求失败，显示后端返回的错误信息
        ElMessage.error(error.response?.data?.message || '用户名或密码错误')
      }
    } else if ((error.config as any)?.silentError) {
      // 页面自行处理错误提示
    } else if (error.response?.status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error(error.response?.data?.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
