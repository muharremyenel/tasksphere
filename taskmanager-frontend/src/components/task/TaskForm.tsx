import { useEffect } from 'react'
import { useForm, Controller } from 'react-hook-form'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { TaskPriority, TaskStatus, CreateTaskRequest } from '@/types/task'
import { useCategoryStore } from '@/stores/categoryStore'
import { useUserStore } from '@/stores/userStore'

interface TaskFormProps {
  onSubmit: (data: CreateTaskRequest) => Promise<void>;
  defaultValues?: Partial<CreateTaskRequest>;
}

export function TaskForm({ onSubmit, defaultValues }: TaskFormProps) {
  const { control, handleSubmit } = useForm<CreateTaskRequest>({
    defaultValues: {
      status: TaskStatus.TODO,
      priority: TaskPriority.MEDIUM,
      ...defaultValues
    }
  })

  const { categories, fetchCategories } = useCategoryStore()
  const { users, fetchUsers } = useUserStore()

  useEffect(() => {
    void fetchCategories()
    void fetchUsers()
  }, [fetchCategories, fetchUsers])

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <Controller
        name="title"
        control={control}
        rules={{ required: 'Title is required' }}
        render={({ field, fieldState: { error } }) => (
          <div>
            <Input {...field} placeholder="Task title" />
            {error && <span className="text-sm text-red-500">{error.message}</span>}
          </div>
        )}
      />

      <Controller
        name="description"
        control={control}
        render={({ field }) => (
          <Textarea {...field} placeholder="Description" />
        )}
      />

      <div className="grid grid-cols-2 gap-4">
        <Controller
          name="priority"
          control={control}
          rules={{ required: 'Priority is required' }}
          render={({ field: { onChange, value } }) => (
            <Select value={value} onValueChange={onChange}>
              <SelectTrigger>
                <SelectValue placeholder="Priority" />
              </SelectTrigger>
              <SelectContent>
                {Object.values(TaskPriority).map(priority => (
                  <SelectItem key={priority} value={priority}>
                    {priority}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        />

        <Controller
          name="assignedToId"
          control={control}
          render={({ field: { onChange, value } }) => (
            <Select value={value?.toString()} onValueChange={val => onChange(Number(val))}>
              <SelectTrigger>
                <SelectValue placeholder="Assign to" />
              </SelectTrigger>
              <SelectContent>
                {users.map(user => (
                  <SelectItem key={user.id} value={user.id.toString()}>
                    {user.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        />
      </div>

      <div className="grid grid-cols-2 gap-4">
        <Controller
          name="categoryId"
          control={control}
          render={({ field: { onChange, value } }) => (
            <Select value={value?.toString()} onValueChange={val => onChange(Number(val))}>
              <SelectTrigger>
                <SelectValue placeholder="Category" />
              </SelectTrigger>
              <SelectContent>
                {categories.map(category => (
                  <SelectItem key={category.id} value={category.id.toString()}>
                    {category.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          )}
        />
      </div>

      <Button type="submit">Create Task</Button>
    </form>
  )
} 