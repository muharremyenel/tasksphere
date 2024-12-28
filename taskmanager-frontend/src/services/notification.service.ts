import { api } from './api'
import { Notification } from '@/types/notification'
import { AxiosError } from 'axios'

class NotificationService {
  async getNotifications(): Promise<Notification[]> {
    const response = await api.get<Notification[]>('/notifications')
    return response.data
  }

  async markAsRead(id: number): Promise<void> {
    await api.patch(`/notifications/${id}/read`)
  }

  async markAllAsRead(): Promise<void> {
    await api.patch('/notifications/read-all')
  }
}

export const notificationService = new NotificationService() 