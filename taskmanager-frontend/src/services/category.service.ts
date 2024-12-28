import api from './api'
import { Category } from '@/types/category'

export const categoryService = {
  getCategories: () => 
    api.get<Category[]>('/categories'),
} 