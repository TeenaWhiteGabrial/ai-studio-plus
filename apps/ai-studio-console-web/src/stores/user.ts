import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'
import JSEncrypt from 'jsencrypt'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<any>(null)

  function isLoggedIn() {
    return !!localStorage.getItem('token')
  }

  async function login(username: string, password: string) {
    const keyRes = await request.get('/auth/public-key') as any
    const encryptor = new JSEncrypt()
    encryptor.setPublicKey(keyRes.data)
    const encryptedPassword = encryptor.encrypt(password)
    if (!encryptedPassword) throw new Error('密码加密失败')
    const res = await request.post('/auth/login', { username, password: encryptedPassword }) as any
    const data = res.data
    userInfo.value = data
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(data))
  }

  function logout() {
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  function initUserInfo() {
    const stored = localStorage.getItem('userInfo')
    if (stored) {
      userInfo.value = JSON.parse(stored)
    }
  }

  return {
    userInfo,
    isLoggedIn,
    login,
    logout,
    initUserInfo,
  }
})
