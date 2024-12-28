import { Task } from '@/types/task'
import { TaskCard } from './TaskCard'
import { Skeleton } from '@/components/ui/skeleton'

interface TaskColumnProps {
  title: string
  tasks: Task[]
  loading: boolean
}

export function TaskColumn({ title, tasks, loading }: TaskColumnProps) {
  if (loading) {
    return (
      <div className="bg-gray-50 p-4 rounded-lg">
        <h3 className="font-semibold mb-4">{title}</h3>
        <div className="space-y-2">
          <Skeleton className="h-24 w-full" />
          <Skeleton className="h-24 w-full" />
          <Skeleton className="h-24 w-full" />
        </div>
      </div>
    )
  }

  return (
    <div className="bg-gray-50 p-4 rounded-lg">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold">{title}</h3>
        <span className="text-sm text-gray-500">{tasks.length}</span>
      </div>
      
      <div className="space-y-2">
        {tasks.map((task, index) => (
          <TaskCard key={task.id} task={task} index={index} />
        ))}
      </div>
    </div>
  )
} 