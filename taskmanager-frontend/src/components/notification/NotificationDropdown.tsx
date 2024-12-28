import { useEffect } from 'react'
import { useNotificationStore } from '@/stores/notificationStore'
import { Button } from '@/components/ui/button'
import { BellIcon } from 'lucide-react'
import { format } from 'date-fns'

export function NotificationDropdown() {
  const { notifications, unreadCount, fetchNotifications, markAsRead } = useNotificationStore()

  useEffect(() => {
    fetchNotifications()
    // Her 30 saniyede bir yeni bildirimleri kontrol et
    const interval = setInterval(fetchNotifications, 30000)
    return () => clearInterval(interval)
  }, [fetchNotifications])

  return (
    <div className="relative">
      <Button variant="ghost" size="icon" className="relative">
        <BellIcon className="h-5 w-5" />
        {unreadCount > 0 && (
          <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full w-4 h-4 flex items-center justify-center">
            {unreadCount}
          </span>
        )}
      </Button>
      
      {/* Dropdown içeriği */}
      <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border p-2 space-y-2">
        {notifications.map(notification => (
          <div 
            key={notification.id}
            className={`p-2 rounded-lg ${notification.read ? 'bg-gray-50' : 'bg-blue-50'}`}
            onClick={() => !notification.read && markAsRead(notification.id)}
          >
            <p className="text-sm">{notification.message}</p>
            <span className="text-xs text-gray-500">
              {format(new Date(notification.createdAt), 'MMM d, HH:mm')}
            </span>
          </div>
        ))}
      </div>
    </div>
  )
} 