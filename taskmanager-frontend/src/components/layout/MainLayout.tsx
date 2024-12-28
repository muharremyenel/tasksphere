import { Outlet } from "react-router-dom"
import Navbar from "./Navbar"
import { Sidebar } from "./Sidebar"

export default function MainLayout() {
  return (
    <div className="min-h-screen bg-background">
      <Navbar />
      <div className="flex">
        <Sidebar />
        <main className="flex-1 p-8">
          <Outlet />
        </main>
      </div>
    </div>
  )
} 