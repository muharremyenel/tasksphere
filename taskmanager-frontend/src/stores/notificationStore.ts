import { create } from 'zustand'
import { Notification } from '@/types/notification'
import { notificationService } from '@/services/notification.service'
import { AxiosError } from 'axios'

interface NotificationState {
  notifications: Notification[];
  unreadCount: number;
  loading: boolean;
  error: string | null;
  fetchNotifications: () => Promise<void>;
  markAsRead: (id: number) => Promise<void>;
  markAllAsRead: () => Promise<void>;
}

export const useNotificationStore = create<NotificationState>((set, get) => ({
  notifications: [],
  unreadCount: 0,
  loading: false,
  error: null,

  fetchNotifications: async () => {
    try {
      set({ loading: true })
      const notifications = await notificationService.getNotifications()
      set({ 
        notifications,
        unreadCount: notifications.filter((n: Notification) => !n.read).length,
        loading: false 
      })
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  markAsRead: async (id) => {
    try {
      await notificationService.markAsRead(id)
      await get().fetchNotifications()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message })
    }
  },

  markAllAsRead: async () => {
    try {
      await notificationService.markAllAsRead()
      await get().fetchNotifications()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message })
    }
  }
})) 