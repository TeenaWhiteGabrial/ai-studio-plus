import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import JSEncrypt from 'jsencrypt'

interface UserInfo {
  user_id: number
  username: string
  real_name: string
  roles: string[]
  token: string
}

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(
    JSON.parse(localStorage.getItem('userInfo') || 'null')
  )

  // 记住密码相关方法
  function saveRememberedCredentials(username: string, password: string) {
    localStorage.setItem('rememberedUsername', username)
    localStorage.setItem('rememberedPassword', btoa(password)) // Base64 编码
  }

  function getRememberedCredentials(): { username: string; password: string } | null {
    const username = localStorage.getItem('rememberedUsername')
    const password = localStorage.getItem('rememberedPassword')
    if (username && password) {
      return { username, password: atob(password) }
    }
    return null
  }

  function clearRememberedCredentials() {
    localStorage.removeItem('rememberedUsername')
    localStorage.removeItem('rememberedPassword')
  }

  async function login(username: string, password: string) {
    // 获取公钥并加密密码
    const keyRes = await request.get('/auth/public-key') as any
    const encryptor = new JSEncrypt()
    encryptor.setPublicKey(keyRes.data)
    const encryptedPassword = encryptor.encrypt(password)
    if (!encryptedPassword) throw new Error('密码加密失败')

    const res = await request.post('/auth/login', { username, password: encryptedPassword }) as any
    // 登录失败时，后端返回 code=401，这里直接抛出错误让调用方处理
    if (res.code !== 200) {
      throw new Error(res.message || '登录失败')
    }
    const data = res.data
    userInfo.value = data
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data))
  }

  function logout() {
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    // clearRememberedCredentials()
  }

  function hasRole(role: string) {
    return userInfo.value?.roles?.includes(role) ?? false
  }

  function isAdmin() {
    return hasRole('OP_ADMIN') || hasRole('SUPER_ADMIN') || hasRole('DEPT_ADMIN')
  }

  function isSuperAdmin() {
    return hasRole('SUPER_ADMIN')
  }

  return { userInfo, login, logout, hasRole, isAdmin, isSuperAdmin, saveRememberedCredentials, getRememberedCredentials, clearRememberedCredentials }
})
