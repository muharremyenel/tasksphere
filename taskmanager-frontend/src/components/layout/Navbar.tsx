import { Link } from "react-router-dom"
import { Button } from "@/components/ui/button"
import { RiBellLine, RiUserLine } from "react-icons/ri"

export default function Navbar() {
  return (
    <nav className="border-b bg-background">
      <div className="flex h-16 items-center px-4 justify-between">
        <Link to="/" className="text-xl font-bold">
          TaskSphere
        </Link>

        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon">
            <RiBellLine className="h-5 w-5" />
          </Button>
          <Button variant="ghost" size="icon">
            <RiUserLine className="h-5 w-5" />
          </Button>
        </div>
      </div>
    </nav>
  )
} 