export interface Notification {
  id: number;
  type: 'TASK_ASSIGNED' | 'TASK_UPDATED' | 'COMMENT_ADDED';
  message: string;
  read: boolean;
  createdAt: string;
  taskId?: number;
} 