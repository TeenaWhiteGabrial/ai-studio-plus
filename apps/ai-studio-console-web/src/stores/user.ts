import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import JSEncrypt from 'jsencrypt'

interface UserInfo {
  user_id: number
  username: string
  git_name?: string
  real_name: string
  roles: string[]
  token: string
  avatar?: string        // 头像URL
  dept_id?: number       // 部门ID
  dept_name?: string     // 部门名称
  team_id?: number       // 团队ID
  team_name?: string     // 团队名称
  email?: string         // 邮箱
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

    // 获取Token
    const tokenRes = await request.post('/auth/token', { username, password: encryptedPassword }) as any
    if (tokenRes.code !== 200) {
      throw new Error(tokenRes.message || '登录失败')
    }

    // 存储Token
    const token = tokenRes.data.token
    localStorage.setItem('token', token)

    // 获取用户信息
    const userRes = await request.get('/auth/user-info') as any
    if (userRes.code !== 200) {
      throw new Error(userRes.message || '获取用户信息失败')
    }

    const data = userRes.data
    data.token = token // 将token添加到用户信息中
    userInfo.value = data
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
