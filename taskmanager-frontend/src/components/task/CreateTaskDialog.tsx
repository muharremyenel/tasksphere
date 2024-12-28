import { useState } from 'react'
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { PlusIcon } from 'lucide-react'
import { TaskForm } from './TaskForm'
import { useTaskStore } from '@/stores/taskStore'

export function CreateTaskDialog() {
  const [open, setOpen] = useState(false)
  const createTask = useTaskStore(state => state.createTask)

  const handleCreate = async (data: any) => {
    try {
      await createTask(data)
      setOpen(false)
    } catch (error) {
      console.error('Failed to create task:', error)
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <PlusIcon className="w-4 h-4 mr-2" />
          New Task
        </Button>
      </DialogTrigger>
      <DialogContent className="max-w-2xl">
        <DialogHeader>
          <DialogTitle>Create New Task</DialogTitle>
        </DialogHeader>
        <TaskForm onSubmit={handleCreate} />
      </DialogContent>
    </Dialog>
  )
} 