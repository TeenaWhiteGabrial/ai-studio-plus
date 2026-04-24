import JSEncrypt from 'jsencrypt'

interface LoginResponse {
  code: number
  message: string
  msg: string
  data: {
    token: string
  }
}

interface UserInfoResponse {
  code: number
  message: string
  msg: string
  data: UserInfo
}

export const useAuthStore = defineStore('authStore', {
  state: (): UserInfo => ({
    userName: '',
    username: '',
    phone: '',
    email: '',
    token: getCookieToken(),
    userId: '',
    avatar: '',
    roles: [],
    deptId: undefined,
    deptName: undefined,
    teamId: undefined,
    teamName: undefined,
    user_id: undefined,
    id: undefined,
    real_name: undefined,
    dept_id: undefined,
    dept_name: undefined,
    team_id: undefined,
    team_name: undefined,
  }),
  actions: {
    saveRememberedCredentials(username: string, password: string) {
      if (!import.meta.client) {
        return
      }
      localStorage.setItem('rememberedUsername', username)
      localStorage.setItem('rememberedPassword', btoa(password))
    },

    getRememberedCredentials(): { username: string; password: string } | null {
      if (!import.meta.client) {
        return null
      }
      const username = localStorage.getItem('rememberedUsername')
      const password = localStorage.getItem('rememberedPassword')
      if (username && password) {
        return { username, password: atob(password) }
      }
      return null
    },

    clearRememberedCredentials() {
      if (!import.meta.client) {
        return
      }
      localStorage.removeItem('rememberedUsername')
      localStorage.removeItem('rememberedPassword')
    },

    clearLoginInfo() {
      this.userName = ''
      this.username = ''
      this.phone = ''
      this.email = ''
      this.token = ''
      this.userId = ''
      this.avatar = ''
      this.roles = []
      this.deptId = undefined
      this.deptName = undefined
      this.teamId = undefined
      this.teamName = undefined
      this.user_id = undefined
      this.id = undefined
      this.real_name = undefined
      this.dept_id = undefined
      this.dept_name = undefined
      this.team_id = undefined
      this.team_name = undefined
      removeCookieToken()

      if (import.meta.client) {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
      }
    },

    setLoginInfo(info: Partial<UserInfo> & Record<string, any>) {
      const has = (key: string) => Object.prototype.hasOwnProperty.call(info, key)

      if (has('token')) {
        this.token = info.token || ''
      }

      if (has('user_id')) {
        this.user_id = info.user_id
        this.id = info.user_id
        this.userId = info.user_id !== undefined && info.user_id !== null ? String(info.user_id) : ''
      }

      if (has('id')) {
        this.id = info.id
      }

      if (has('userId')) {
        this.userId = info.userId ? String(info.userId) : ''
      }

      if (has('username')) {
        this.username = info.username || ''
      }

      if (has('userName')) {
        this.userName = info.userName || ''
        this.username = info.userName || this.username
      }
      else if (has('username')) {
        this.userName = info.username || ''
      }

      if (has('real_name')) {
        this.real_name = info.real_name
      }

      if (has('avatar')) {
        this.avatar = info.avatar || ''
      }

      if (has('phone')) {
        this.phone = info.phone || ''
      }

      if (has('email')) {
        this.email = info.email || ''
      }

      if (has('dept_id')) {
        this.dept_id = info.dept_id
        this.deptId = info.dept_id
      }

      if (has('deptId')) {
        this.deptId = info.deptId
        this.dept_id = info.deptId
      }

      if (has('dept_name')) {
        this.dept_name = info.dept_name
        this.deptName = info.dept_name
      }

      if (has('deptName')) {
        this.deptName = info.deptName
        this.dept_name = info.deptName
      }

      if (has('team_id')) {
        this.team_id = info.team_id
        this.teamId = info.team_id
      }

      if (has('teamId')) {
        this.teamId = info.teamId
        this.team_id = info.teamId
      }

      if (has('team_name')) {
        this.team_name = info.team_name
        this.teamName = info.team_name
      }

      if (has('teamName')) {
        this.teamName = info.teamName
        this.team_name = info.teamName
      }

      if (Array.isArray(info.roles)) {
        this.roles = [...new Set(info.roles)]
      }
    },

    setToken(token: string) {
      this.token = token
      setCookieToken(token)
      if (import.meta.client) {
        localStorage.setItem('token', token)
      }
    },

    async login(username: string, password: string) {
      this.clearLoginInfo()
      const config = useRuntimeConfig()

      const keyRes = await $fetch<{ data: string }>('/auth/public-key')
      if (!keyRes?.data) {
        throw new Error('获取公钥失败')
      }

      const encryptor = new JSEncrypt()
      encryptor.setPublicKey(keyRes.data)
      const encryptedPassword = encryptor.encrypt(password)
      if (!encryptedPassword) {
        throw new Error('密码加密失败')
      }

      const tokenRes = await $fetch<LoginResponse>('/auth/token', {
        method: 'POST',
        body: {
          username,
          password: encryptedPassword,
        },
      })

      if (tokenRes?.code !== 200 || !tokenRes?.data?.token) {
        throw new Error(tokenRes?.message || tokenRes?.msg || '登录失败')
      }

      const token = tokenRes.data.token
      this.setToken(token)
      const authHeader = config.public.tokenType ? `${config.public.tokenType} ${token}` : token

      const userRes = await $fetch<UserInfoResponse>('/auth/user-info', {
        headers: {
          Authorization: authHeader,
        },
      })
      if (userRes?.code !== 200 || !userRes?.data) {
        throw new Error(userRes?.message || userRes?.msg || '获取用户信息失败')
      }

      const data = { ...(userRes.data as Record<string, any>), token }
      this.setLoginInfo(data)

      if (import.meta.client) {
        localStorage.setItem('userInfo', JSON.stringify(data))
      }
    },

    async logout() {
      try {
        const config = useRuntimeConfig()
        const authHeader = this.token
          ? (config.public.tokenType ? `${config.public.tokenType} ${this.token}` : this.token)
          : undefined
        await $fetch('/auth/logout', {
          method: 'POST',
          headers: authHeader
            ? {
              Authorization: authHeader,
            }
            : undefined,
        })
      } catch (err) {
        console.error('退出请求失败:', err)
      } finally {
        this.clearLoginInfo()
      }
    },

    async changePassword(oldPassword: string, newPassword: string) {
      try {
        const res = await useSimpleFetch<{
          code: number
          msg: string
        }>('/auth/change-password', {
          method: 'POST',
          body: { oldPassword, newPassword },
        })
        if (res.code === 200) {
          return { success: true, message: '密码修改成功' }
        }
        return { success: false, message: res.msg || '密码修改失败' }
      } catch (err) {
        return { success: false, message: err instanceof Error ? err.message : '密码修改请求失败' }
      }
    },

    hasRole(role: string) {
      return this.roles?.includes(role) ?? false
    },

    isAdmin() {
      return this.hasRole('OP_ADMIN') || this.hasRole('SUPER_ADMIN') || this.hasRole('DEPT_ADMIN')
    },

    isSuperAdmin() {
      return this.hasRole('SUPER_ADMIN')
    },
  },
})
