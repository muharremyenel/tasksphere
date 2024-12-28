import api from './api'
import { User } from '@/types/auth'

export const userService = {
  getUsers: () => 
    api.get<User[]>('/users'),
} 