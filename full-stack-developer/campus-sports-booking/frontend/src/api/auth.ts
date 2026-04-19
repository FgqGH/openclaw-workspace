import request from '@/utils/request'

export interface LoginData {
  username: string
  password: string
}

export interface UserInfo {
  id: number
  username: string
  realName: string
  phone: string
  email: string
  avatar: string
  status: number
}

export const authApi = {
  login(data: LoginData): Promise<string> {
    return request.post<string>('/api/auth/login', data)
  },

  logout() {
    return request.post('/api/auth/logout')
  },

  getCurrentUser(): Promise<UserInfo> {
    return request.get<UserInfo>('/api/auth/me')
  }
}
