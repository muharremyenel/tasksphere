import { create } from 'zustand'
import { User } from '@/types/auth'
import { authService } from '@/services/auth.service'

interface AuthState {
  user: User | null
  token: string | null
  isAuthenticated: boolean
  login: (email: string, password: string) => Promise<void>
  logout: () => Promise<void>
  setUser: (user: User) => void
}

export const useAuthStore = create<AuthState>((set) => ({
  user: authService.getCurrentUser(),
  token: localStorage.getItem('token'),
  isAuthenticated: !!localStorage.getItem('token'),

  login: async (email: string, password: string) => {
    const response = await authService.login({ email, password })
    localStorage.setItem('token', response.token)
    localStorage.setItem('user', JSON.stringify(response.user))
    set({ user: response.user, token: response.token, isAuthenticated: true })
  },

  logout: async () => {
    await authService.logout()
    set({ user: null, token: null, isAuthenticated: false })
  },

  setUser: (user: User) => set({ user })
})) 