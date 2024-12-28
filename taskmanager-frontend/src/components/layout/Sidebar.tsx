import { Link } from 'react-router-dom'
import { RiDashboardLine, RiTeamLine, RiUserLine, RiPriceTag3Line, RiListCheck } from 'react-icons/ri'
import { useAuthStore } from '@/stores/authStore'

export default function Sidebar() {
  const { user } = useAuthStore()
  const isAdmin = user?.role === 'ROLE_ADMIN'

  return (
    <aside className="w-64 bg-gray-50 border-r h-full">
      <nav className="p-4 space-y-2">
        <Link to="/" className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg">
          <RiDashboardLine /> Dashboard
        </Link>
        <Link to="/tasks" className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg">
          <RiListCheck /> Tasks
        </Link>
        
        {isAdmin && (
          <>
            <Link to="/admin/users" className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg">
              <RiUserLine /> Users
            </Link>
            <Link to="/admin/teams" className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg">
              <RiTeamLine /> Teams
            </Link>
            <Link to="/admin/categories" className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg">
              <RiPriceTag3Line /> Categories
            </Link>
            <Link to="/admin/tags" className="flex items-center gap-2 p-2 hover:bg-gray-100 rounded-lg">
              <RiPriceTag3Line /> Tags
            </Link>
          </>
        )}
      </nav>
    </aside>
  )
} 