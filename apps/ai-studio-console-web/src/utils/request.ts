import axios, { type AxiosInstance, type InternalAxiosRequestConfig } from 'axios'
import { ElLoading, ElMessage } from 'element-plus'
import type { LoadingInstance } from 'element-plus/es/components/loading/src/loading'
import router from './auth'

// API 基础路径
const API_BASE_URL = '/ai-studio/v1'
// 业务路由前缀
const BASE_ROUTER = import.meta.env.VITE_BASE_ROUTER

const request: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
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

// 请求拦截器：添加 Token
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
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
      // 业务接口请求 /ai-studio/v1/console/*
      config.baseURL = `${API_BASE_URL}${BASE_ROUTER}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理 + 401 跳转
request.interceptors.response.use(
  (response) => {
    stopGlobalLoading()
    return response.data
  },
  (error) => {
    stopGlobalLoading()
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/console/login')
      ElMessage.warning('登录已过期，请重新登录')
    } else {
      const msg = error.response?.data?.message || error.message || '请求失败'
      ElMessage.error(msg)
    }
    return Promise.reject(error)
  }
)

export default request
