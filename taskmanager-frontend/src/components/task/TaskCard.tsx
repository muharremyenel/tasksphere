import { Task, TaskPriority } from '@/types/task'
import { Badge } from '@/components/ui/badge'
import { Avatar } from '@/components/ui/avatar'
import { CalendarIcon, MessageSquare } from 'lucide-react'
import { format } from 'date-fns'
import { Draggable } from 'react-beautiful-dnd'

interface TaskCardProps {
  task: Task;
  index: number;
}

const priorityColors = {
  [TaskPriority.LOW]: 'bg-blue-100 text-blue-800',
  [TaskPriority.MEDIUM]: 'bg-yellow-100 text-yellow-800',
  [TaskPriority.HIGH]: 'bg-red-100 text-red-800'
}

export function TaskCard({ task, index }: TaskCardProps) {

  return (
    <Draggable draggableId={task.id.toString()} index={index}>
      {(provided) => (
        <div
          ref={provided.innerRef}
          {...provided.draggableProps}
          {...provided.dragHandleProps}
          className="bg-white p-4 rounded-lg shadow-sm border hover:shadow-md transition-shadow"
        >
          <div className="flex justify-between items-start mb-2">
            <h4 className="font-medium">{task.title}</h4>
            <Badge variant="outline" className={priorityColors[task.priority]}>
              {task.priority}
            </Badge>
          </div>

          {task.category && (
            <Badge 
              variant="secondary" 
              className="mb-2"
              style={{ backgroundColor: task.category.colorHex + '20', color: task.category.colorHex }}
            >
              {task.category.name}
            </Badge>
          )}

          {task.tags.map(tag => (
            <Badge 
              key={tag.id}
              variant="secondary" 
              className="mr-1 mb-1"
              style={{ backgroundColor: tag.colorHex + '20', color: tag.colorHex }}
            >
              {tag.name}
            </Badge>
          ))}

          <div className="flex items-center justify-between text-sm text-gray-500 mt-4">
            <div className="flex items-center gap-2">
              <Avatar 
                src={`https://avatar.vercel.sh/${task.assignedTo?.email}.png`} 
                alt={task.assignedTo?.name || 'Unassigned'} 
                className="w-6 h-6"
              />
              <span>{task.assignedTo?.name || 'Unassigned'}</span>
            </div>

            <div className="flex items-center gap-4">
              {task.dueDate && (
                <div className="flex items-center gap-1">
                  <CalendarIcon className="w-4 h-4" />
                  <span>{format(new Date(task.dueDate), 'MMM d')}</span>
                </div>
              )}
              
              {task.recentComments.length > 0 && (
                <div className="flex items-center gap-1">
                  <MessageSquare className="w-4 h-4" />
                  <span>{task.recentComments.length}</span>
                </div>
              )}
            </div>
          </div>
        </div>
      )}
    </Draggable>
  )
} 