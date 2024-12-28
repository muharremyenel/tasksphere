import { api } from './api'
import { Task, CreateTaskRequest, UpdateTaskRequest, UpdateTaskStatusRequest, Comment } from '@/types/task'

interface AddCommentRequest {
  content: string;
}

class TaskService {
  async getTasks(): Promise<Task[]> {
    const response = await api.get<Task[]>('/tasks')
    return response.data
  }

  async createTask(data: CreateTaskRequest): Promise<Task> {
    const response = await api.post<Task>('/tasks', data)
    return response.data
  }

  async updateTask(taskId: number, data: UpdateTaskRequest): Promise<Task> {
    const response = await api.put<Task>(`/tasks/${taskId}`, data)
    return response.data
  }

  async updateTaskStatus(taskId: number, data: UpdateTaskStatusRequest): Promise<Task> {
    const response = await api.patch<Task>(`/tasks/${taskId}/status`, data)
    return response.data
  }

  async deleteTask(taskId: number): Promise<void> {
    await api.delete(`/tasks/${taskId}`)
  }

  async addComment(taskId: number, data: AddCommentRequest): Promise<Comment> {
    const response = await api.post<Comment>(`/tasks/${taskId}/comments`, data)
    return response.data
  }

  async addCollaborator(taskId: number, userId: number): Promise<void> {
    await api.put(`/tasks/${taskId}/collaborators/${userId}`)
  }

  async removeCollaborator(taskId: number, userId: number): Promise<void> {
    await api.delete(`/tasks/${taskId}/collaborators/${userId}`)
  }
}

export const taskService = new TaskService() 