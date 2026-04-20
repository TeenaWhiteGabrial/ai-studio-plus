import axios, { type AxiosInstance, type AxiosRequestConfig, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'
import router from './auth'

// 控制台 Web 的 baseURL: /ai-studio/v1/console
const baseURL = import.meta.env.VITE_API_BASE_URL
const baseRouterUrl = `${import.meta.env.VITE_API_BASE_URL}${import.meta.env.VITE_BASE_ROUTER}`
const request: AxiosInstance = axios.create({
  baseURL,
  timeout: 30000,
})

// 请求拦截器：添加 Token
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // 判断是否为认证相关请求
    if (config.url?.startsWith('/auth/')) {
      config.baseURL = baseURL
    } else {
      config.baseURL = baseRouterUrl
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一错误处理 + 401 跳转
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
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
