export interface User {
  id: number
  name: string
  email: string
  role: string
  team?: {
    id: number
    name: string
  }
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

export interface TeamSummary {
  id: number
  name: string
} 