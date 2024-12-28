import { Navigate, Outlet } from 'react-router-dom'
import { useAuthStore } from '@/stores/authStore'
import { Sidebar } from './Sidebar'

export function AdminLayout() {
  const { user } = useAuthStore()
  
  // Admin değilse dashboard'a yönlendir
  if (user?.role !== 'ROLE_ADMIN') {
    return <Navigate to="/dashboard" />
  }

  return (
    <div className="flex h-screen">
      <Sidebar />
      <main className="flex-1 p-8 overflow-auto">
        <Outlet />
      </main>
    </div>
  )
} 