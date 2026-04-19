import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi, type UserInfo } from '@/api/auth'
import { storage } from '@/utils/storage'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(storage.getToken())
  const userInfo = ref<UserInfo | null>(storage.getUser())

  const isLoggedIn = () => !!token.value

  const isAdmin = () => {
    return userInfo.value?.username === 'admin'
  }

  const login = async (username: string, password: string) => {
    const jwtToken = await authApi.login({ username, password })
    token.value = jwtToken
    storage.setToken(jwtToken)
    
    // 获取用户信息
    await fetchUserInfo()
  }

  const fetchUserInfo = async () => {
    try {
      const userInfoData = await authApi.getCurrentUser()
      userInfo.value = userInfoData
      storage.setUser(userInfoData)
    } catch (error) {
      logout()
      throw error
    }
  }

  const logout = () => {
    authApi.logout().finally(() => {
      token.value = null
      userInfo.value = null
      storage.clear()
    })
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isAdmin,
    login,
    fetchUserInfo,
    logout
  }
})
