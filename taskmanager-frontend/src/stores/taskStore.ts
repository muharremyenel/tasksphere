import { create } from 'zustand'
import { Task, CreateTaskRequest, TaskStatus, UpdateTaskRequest } from '@/types/task'
import { taskService } from '@/services/task.service'
import { AxiosError } from 'axios'

interface TaskState {
  tasks: Task[];
  loading: boolean;
  error: string | null;
  fetchTasks: () => Promise<void>;
  createTask: (task: CreateTaskRequest) => Promise<void>;
  updateTask: (taskId: number, data: UpdateTaskRequest) => Promise<void>;
  updateTaskStatus: (taskId: number, status: TaskStatus) => Promise<void>;
  deleteTask: (taskId: number) => Promise<void>;
}

export const useTaskStore = create<TaskState>((set, get) => ({
  tasks: [],
  loading: false,
  error: null,

  fetchTasks: async () => {
    try {
      set({ loading: true, error: null })
      const tasks = await taskService.getTasks()
      set({ tasks, loading: false })
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  createTask: async (taskData) => {
    try {
      set({ loading: true, error: null })
      await taskService.createTask(taskData)
      await get().fetchTasks()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  updateTask: async (taskId, data) => {
    try {
      set({ loading: true, error: null })
      await taskService.updateTask(taskId, data)
      await get().fetchTasks()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  updateTaskStatus: async (taskId, status) => {
    try {
      set({ loading: true, error: null })
      await taskService.updateTaskStatus(taskId, { status })
      await get().fetchTasks()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  deleteTask: async (taskId) => {
    try {
      set({ loading: true, error: null })
      await taskService.deleteTask(taskId)
      await get().fetchTasks()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  }
})) 