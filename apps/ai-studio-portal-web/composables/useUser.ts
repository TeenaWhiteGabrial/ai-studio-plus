import axios from 'axios'

export const useUser = () => {
  const userInfo = useState<any>('userInfo', () => null)

  const isLoggedIn = () => {
    return !!localStorage.getItem('token')
  }

  const login = async (username: string, password: string) => {
    // For portal, we use direct fetch to the open auth endpoint
    // In a real scenario, portal might have a different login flow
    const res = await fetch('/ai-studio/v1/open/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password }),
    })
    const data = await res.json()
    if (data.token) {
      userInfo.value = data
      localStorage.setItem('token', data.token)
      localStorage.setItem('userInfo', JSON.stringify(data))
    }
  }

  const logout = () => {
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const initUserInfo = () => {
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
}
