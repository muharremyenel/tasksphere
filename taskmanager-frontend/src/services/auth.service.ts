import api from './api'
import { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth'

export const authService = {
  login: (data: LoginRequest) => 
    api.post<AuthResponse>('/auth/authenticate', data),

  register: (data: RegisterRequest) => 
    api.post<AuthResponse>('/auth/register', data),

  getCurrentUser: () => 
    api.get('/auth/me')
} 