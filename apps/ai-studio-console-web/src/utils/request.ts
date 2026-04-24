import axios, { type AxiosInstance, type InternalAxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from './auth'

// API 基础路径
const API_BASE_URL = '/ai-studio/v1'
// 业务路由前缀
const BASE_ROUTER = import.meta.env.VITE_BASE_ROUTER

const request: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
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
