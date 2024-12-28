export interface User {
  id: number
  name: string
  email: string
  role: 'ROLE_ADMIN' | 'ROLE_USER'
}

export interface AuthResponse {
  token: string
  user: User
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest extends LoginRequest {
  name: string
} 