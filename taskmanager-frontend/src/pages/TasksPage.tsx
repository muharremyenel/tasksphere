import { useEffect } from 'react'
import { useTaskStore } from '@/stores/taskStore'
import { TaskStatus } from '@/types/task'
import { TaskColumn } from '@/components/task/TaskColumn'
import { CreateTaskDialog } from '@/components/task/CreateTaskDialog'
import { useAuthStore } from '@/stores/authStore'
import { DragDropContext, Droppable, DropResult } from 'react-beautiful-dnd'

export default function TasksPage() {
  const { tasks, loading, fetchTasks } = useTaskStore()
  const { user } = useAuthStore()
  const isAdmin = user?.role === 'ROLE_ADMIN'

  useEffect(() => {
    void fetchTasks()
  }, [fetchTasks])

  const todoTasks = tasks.filter(task => task.status === TaskStatus.TODO)
  const inProgressTasks = tasks.filter(task => task.status === TaskStatus.IN_PROGRESS)
  const doneTasks = tasks.filter(task => task.status === TaskStatus.DONE)

  const handleDragEnd = async (result: DropResult) => {
    const { destination, source, draggableId } = result

    // Eğer geçerli bir hedef yoksa işlemi iptal et
    if (!destination) return

    // Aynı kolona bırakıldıysa işlem yapma
    if (destination.droppableId === source.droppableId) return

    // Task'in yeni statusunu belirle
    const newStatus = destination.droppableId as TaskStatus

    try {
      await updateTaskStatus(Number(draggableId), newStatus)
    } catch (error) {
      console.error('Failed to update task status:', error)
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Tasks</h1>
        {isAdmin && <CreateTaskDialog />}
      </div>

      <DragDropContext onDragEnd={handleDragEnd}>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <Droppable droppableId={TaskStatus.TODO}>
            {(provided) => (
              <div ref={provided.innerRef} {...provided.droppableProps}>
                <TaskColumn 
                  title="To Do" 
                  tasks={todoTasks} 
                  loading={loading}
                />
                {provided.placeholder}
              </div>
            )}
          </Droppable>

          <Droppable droppableId={TaskStatus.IN_PROGRESS}>
            {(provided) => (
              <div ref={provided.innerRef} {...provided.droppableProps}>
                <TaskColumn 
                  title="In Progress" 
                  tasks={inProgressTasks} 
                  loading={loading}
                />
                {provided.placeholder}
              </div>
            )}
          </Droppable>

          <Droppable droppableId={TaskStatus.DONE}>
            {(provided) => (
              <div ref={provided.innerRef} {...provided.droppableProps}>
                <TaskColumn 
                  title="Done" 
                  tasks={doneTasks} 
                  loading={loading}
                />
                {provided.placeholder}
              </div>
            )}
          </Droppable>
        </div>
      </DragDropContext>
    </div>
  )
} 