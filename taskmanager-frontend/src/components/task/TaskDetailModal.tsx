import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { Avatar } from '@/components/ui/avatar'
import { Task, TaskStatus, UpdateTaskRequest } from '@/types/task'
import { useAuthStore } from '@/stores/authStore'
import { useTaskStore } from '@/stores/taskStore'
import { TaskForm } from './TaskForm'
import { format } from 'date-fns'
import { CalendarIcon, MessageSquare, UserPlus, Trash2, X } from 'lucide-react'
import { useState } from 'react'
import { CommentForm } from './CommentForm'
import { CommentList } from './CommentList'
import { taskService } from '@/services/task.service'
import { useUserStore } from '@/stores/userStore'

interface TaskDetailModalProps {
  task: Task
  isOpen: boolean
  onClose: () => void
}

export function TaskDetailModal({ task, isOpen, onClose }: TaskDetailModalProps) {
  const { user } = useAuthStore()
  const { updateTask, deleteTask } = useTaskStore()
  const isAdmin = user?.role === 'ROLE_ADMIN'
  const [isEditing, setIsEditing] = useState(false)
  const { users } = useUserStore()
  const [showUserList, setShowUserList] = useState(false)

  const handleUpdate = async (data: UpdateTaskRequest) => {
    try {
      await updateTask(task.id, data)
      setIsEditing(false)
      onClose()
    } catch (error) {
      console.error('Failed to update task:', error)
    }
  }

  const handleDelete = async () => {
    if (window.confirm('Are you sure you want to delete this task?')) {
      try {
        await deleteTask(task.id)
        onClose()
      } catch (error) {
        console.error('Failed to delete task:', error)
      }
    }
  }

  const handleStatusChange = async (status: TaskStatus) => {
    try {
      await updateTask(task.id, { ...task, status })
    } catch (error) {
      console.error('Failed to update status:', error)
    }
  }

  const handleAddComment = async (content: string) => {
    try {
      await taskService.addComment(task.id, { content })
      // Task'i yeniden yükle
      await useTaskStore.getState().fetchTasks()
    } catch (error) {
      console.error('Failed to add comment:', error)
    }
  }

  const handleAddCollaborator = async (userId: number) => {
    try {
      await taskService.addCollaborator(task.id, userId)
      await useTaskStore.getState().fetchTasks()
      setShowUserList(false)
    } catch (error) {
      console.error('Failed to add collaborator:', error)
    }
  }

  const handleRemoveCollaborator = async (userId: number) => {
    try {
      await taskService.removeCollaborator(task.id, userId)
      await useTaskStore.getState().fetchTasks()
    } catch (error) {
      console.error('Failed to remove collaborator:', error)
    }
  }

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl">
        <DialogHeader>
          <div className="flex justify-between items-center">
            <DialogTitle>{task.title}</DialogTitle>
            {isAdmin && (
              <div className="flex gap-2">
                <Button variant="outline" onClick={() => setIsEditing(!isEditing)}>
                  {isEditing ? 'Cancel' : 'Edit'}
                </Button>
                <Button variant="destructive" onClick={handleDelete}>
                  <Trash2 className="w-4 h-4" />
                </Button>
              </div>
            )}
          </div>
        </DialogHeader>

        {isEditing && isAdmin ? (
          <TaskForm onSubmit={handleUpdate} defaultValues={task} />
        ) : (
          <div className="space-y-6">
            {/* Task Details */}
            <div>
              <p className="text-gray-600">{task.description}</p>
              <div className="flex gap-2 mt-4">
                <Badge>{task.status}</Badge>
                <Badge variant="outline">{task.priority}</Badge>
                {task.category && (
                  <Badge 
                    style={{ 
                      backgroundColor: task.category.colorHex + '20', 
                      color: task.category.colorHex 
                    }}
                  >
                    {task.category.name}
                  </Badge>
                )}
              </div>
            </div>

            {/* Assignee & Due Date */}
            <div className="flex justify-between items-center">
              <div className="flex items-center gap-2">
                <Avatar 
                  src={`https://avatar.vercel.sh/${task.assignedTo?.email}.png`}
                  alt={task.assignedTo?.name || 'Unassigned'}
                  className="w-8 h-8"
                />
                <div>
                  <p className="text-sm font-medium">{task.assignedTo?.name || 'Unassigned'}</p>
                  <p className="text-xs text-gray-500">Assignee</p>
                </div>
              </div>
              {task.dueDate && (
                <div className="flex items-center gap-2 text-gray-500">
                  <CalendarIcon className="w-4 h-4" />
                  <span>{format(new Date(task.dueDate), 'MMM d, yyyy')}</span>
                </div>
              )}
            </div>

            {/* Status Change */}
            {(isAdmin || task.assignedTo?.id === user?.id) && (
              <div>
                <h4 className="font-medium mb-2">Status</h4>
                <div className="flex gap-2">
                  {Object.values(TaskStatus).map(status => (
                    <Button
                      key={status}
                      variant={task.status === status ? 'default' : 'outline'}
                      onClick={() => handleStatusChange(status)}
                    >
                      {status}
                    </Button>
                  ))}
                </div>
              </div>
            )}

            {/* Comments Section */}
            <div className="space-y-4">
              <h4 className="font-medium">Comments</h4>
              <CommentForm onSubmit={handleAddComment} />
              <CommentList comments={task.recentComments} />
            </div>

            {/* Collaborators Section */}
            {isAdmin && (
              <div className="space-y-4">
                <div className="flex justify-between items-center">
                  <h4 className="font-medium">Collaborators</h4>
                  <Button 
                    variant="outline" 
                    size="sm"
                    onClick={() => setShowUserList(!showUserList)}
                  >
                    <UserPlus className="w-4 h-4 mr-2" />
                    Add Collaborator
                  </Button>
                </div>

                {/* Collaborator List */}
                <div className="flex flex-wrap gap-2">
                  {task.collaborators.map(collaborator => (
                    <div 
                      key={collaborator.id}
                      className="flex items-center gap-2 bg-gray-100 rounded-full px-3 py-1"
                    >
                      <Avatar
                        src={`https://avatar.vercel.sh/${collaborator.email}.png`}
                        alt={collaborator.name}
                        className="w-6 h-6"
                      />
                      <span className="text-sm">{collaborator.name}</span>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="p-0 h-auto hover:bg-transparent"
                        onClick={() => handleRemoveCollaborator(collaborator.id)}
                      >
                        <X className="w-4 h-4" />
                      </Button>
                    </div>
                  ))}
                </div>

                {/* User Selection Dropdown */}
                {showUserList && (
                  <div className="border rounded-lg p-2 space-y-2">
                    {users
                      .filter(user => !task.collaborators.find(c => c.id === user.id))
                      .map(user => (
                        <div
                          key={user.id}
                          className="flex items-center justify-between p-2 hover:bg-gray-50 rounded-lg cursor-pointer"
                          onClick={() => handleAddCollaborator(user.id)}
                        >
                          <div className="flex items-center gap-2">
                            <Avatar
                              src={`https://avatar.vercel.sh/${user.email}.png`}
                              alt={user.name}
                              className="w-6 h-6"
                            />
                            <span>{user.name}</span>
                          </div>
                          <UserPlus className="w-4 h-4" />
                        </div>
                      ))}
                  </div>
                )}
              </div>
            )}
          </div>
        )}
      </DialogContent>
    </Dialog>
  )
} 