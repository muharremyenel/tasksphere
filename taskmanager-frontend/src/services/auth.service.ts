import { api } from './api';
import { LoginRequest, AuthResponse } from '@/types/auth';

class AuthService {
    async login(data: LoginRequest): Promise<AuthResponse> {
        const response = await api.post<AuthResponse>('/auth/authenticate', data);
        return response.data;
    }

    async logout(): Promise<void> {
        const token = localStorage.getItem('token');
        if (token) {
            await api.post('/auth/logout');
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        }
    }

    getCurrentUser() {
        const user = localStorage.getItem('user');
        return user ? JSON.parse(user) : null;
    }
}

export const authService = new AuthService(); 