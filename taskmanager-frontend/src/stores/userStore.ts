import { create } from 'zustand'
import { User } from '@/types/auth'
import { api } from '@/services/api'
import { AxiosError } from 'axios'

interface UserState {
  users: User[]
  loading: boolean
  error: string | null
  fetchUsers: () => Promise<void>
}

export const useUserStore = create<UserState>((set) => ({
  users: [],
  loading: false,
  error: null,

  fetchUsers: async () => {
    try {
      set({ loading: true, error: null })
      const response = await api.get<User[]>('/users')
      set({ users: response.data, loading: false })
    } catch (error: unknown) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  }
})) 