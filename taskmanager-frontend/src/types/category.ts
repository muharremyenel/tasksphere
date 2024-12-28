export interface Category {
  id: number
  name: string
  colorHex: string
  taskCount: number
}

export interface CreateCategoryRequest {
  name: string
  colorHex: string
}

export type UpdateCategoryRequest = CreateCategoryRequest 