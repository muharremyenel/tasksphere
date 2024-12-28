export enum TaskStatus {
  TODO = 'TODO',
  IN_PROGRESS = 'IN_PROGRESS',
  DONE = 'DONE'
}

export enum TaskPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH'
}

export interface Task {
  id: number;
  title: string;
  description: string;
  status: TaskStatus;
  priority: TaskPriority;
  createdAt: string;
  dueDate: string;
  createdBy: {
    id: number;
    name: string;
    email: string;
  };
  assignedTo?: {
    id: number;
    name: string;
    email: string;
  };
  category?: {
    id: number;
    name: string;
    colorHex: string;
  };
  tags: Array<{
    id: number;
    name: string;
    colorHex: string;
  }>;
  collaborators: Array<{
    id: number;
    name: string;
    email: string;
  }>;
  recentComments: Array<{
    id: number;
    content: string;
    author: {
      id: number;
      name: string;
      email: string;
    };
    createdAt: string;
  }>;
}

export interface CreateTaskRequest {
  title: string;
  description?: string;
  status?: TaskStatus;
  priority: TaskPriority;
  dueDate?: string;
  assignedToId?: number;
  categoryId?: number;
  tagIds?: number[];
}

export interface UpdateTaskRequest extends Partial<CreateTaskRequest> {}

export interface UpdateTaskStatusRequest {
  status: TaskStatus;
}

export interface Comment {
  id: number;
  content: string;
  author: {
    id: number;
    name: string;
    email: string;
  };
  createdAt: string;
} 