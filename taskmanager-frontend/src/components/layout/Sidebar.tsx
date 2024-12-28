import { Link } from "react-router-dom"
import { RiDashboardLine, RiTaskLine, RiTeamLine, RiSettings4Line } from "react-icons/ri"

const menuItems = [
  { icon: RiDashboardLine, label: "Dashboard", path: "/" },
  { icon: RiTaskLine, label: "Tasks", path: "/tasks" },
  { icon: RiTeamLine, label: "Team", path: "/team" },
  { icon: RiSettings4Line, label: "Settings", path: "/settings" }
]

export default function Sidebar() {
  return (
    <aside className="w-64 border-r bg-background p-4">
      <nav className="space-y-2">
        {menuItems.map((item) => (
          <Link
            key={item.path}
            to={item.path}
            className="flex items-center gap-2 rounded-lg px-3 py-2 text-gray-500 transition-all hover:text-gray-900 hover:bg-gray-100"
          >
            <item.icon className="h-5 w-5" />
            <span>{item.label}</span>
          </Link>
        ))}
      </nav>
    </aside>
  )
} 