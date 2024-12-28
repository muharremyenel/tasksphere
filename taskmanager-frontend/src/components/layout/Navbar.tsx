import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { RiUserLine, RiLogoutBoxLine } from "react-icons/ri"
import { useAuthStore } from "@/stores/authStore"
import { NotificationDropdown } from '@/components/notification/NotificationDropdown'

export default function Navbar() {
  const user = useAuthStore((state) => state.user)
  const logout = useAuthStore((state) => state.logout)

  return (
    <nav className="border-b bg-background">
      <div className="flex h-16 items-center px-4 justify-between">
        <Link to="/" className="text-xl font-bold">
          TaskSphere
        </Link>

        <div className="flex items-center gap-4">
          <span className="text-sm text-gray-600">
            {user?.name}
          </span>
          <NotificationDropdown />
          <Button variant="ghost" size="icon">
            <RiUserLine className="h-5 w-5" />
          </Button>
          <Button variant="ghost" size="icon" onClick={() => logout()}>
            <RiLogoutBoxLine className="h-5 w-5" />
          </Button>
        </div>
      </div>
    </nav>
  )
} 