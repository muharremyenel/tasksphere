import { useEffect, useState } from "react"
import { useAuthStore } from "@/stores/authStore"
import { taskService } from "@/services/task.service"
import { Card } from "@/components/ui/card"

interface TaskStats {
  totalTasks: number
  todoTasks: number
  inProgressTasks: number
  completedTasks: number
  overdueTasks: number
}

export default function DashboardPage() {
  const user = useAuthStore((state) => state.user)
  const [stats, setStats] = useState<TaskStats>({
    totalTasks: 0,
    todoTasks: 0,
    inProgressTasks: 0,
    completedTasks: 0,
    overdueTasks: 0,
  })

  useEffect(() => {
    loadStats()
  }, [])

  const loadStats = async () => {
    try {
      const response = await taskService.getStatistics()
      setStats(response.data)
    } catch (error) {
      console.error('Failed to load stats:', error)
    }
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mb-6">Welcome, {user?.name}!</h1>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard title="Total Tasks" value={stats.totalTasks} />
        <StatCard title="To Do" value={stats.todoTasks} color="bg-gray-100" />
        <StatCard title="In Progress" value={stats.inProgressTasks} color="bg-blue-100" />
        <StatCard title="Completed" value={stats.completedTasks} color="bg-green-100" />
      </div>

      {/* Role-based content */}
      {user?.role === 'ROLE_ADMIN' ? (
        <AdminDashboard />
      ) : (
        <UserDashboard />
      )}
    </div>
  )
}

function StatCard({ title, value, color = "bg-white" }: { title: string; value: number; color?: string }) {
  return (
    <Card className={`${color} p-4`}>
      <h3 className="text-sm font-medium text-gray-500">{title}</h3>
      <p className="text-2xl font-semibold mt-2">{value}</p>
    </Card>
  )
}

function AdminDashboard() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      <Card className="p-4">
        <h2 className="text-lg font-semibold mb-4">Team Overview</h2>
        {/* Team stats ve grafikleri */}
      </Card>
      <Card className="p-4">
        <h2 className="text-lg font-semibold mb-4">Project Progress</h2>
        {/* Proje istatistikleri */}
      </Card>
    </div>
  )
}

function UserDashboard() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      <Card className="p-4">
        <h2 className="text-lg font-semibold mb-4">My Tasks</h2>
        {/* Kullanıcının görevleri */}
      </Card>
      <Card className="p-4">
        <h2 className="text-lg font-semibold mb-4">Upcoming Deadlines</h2>
        {/* Yaklaşan deadlinelar */}
      </Card>
    </div>
  )
} 