import { create } from 'zustand'
import { Category, CreateCategoryRequest, UpdateCategoryRequest } from '@/types/category'
import { api } from '@/services/api'
import { AxiosError } from 'axios'

interface CategoryState {
  categories: Category[];
  loading: boolean;
  error: string | null;
  fetchCategories: () => Promise<void>;
  createCategory: (data: CreateCategoryRequest) => Promise<void>;
  updateCategory: (id: number, data: UpdateCategoryRequest) => Promise<void>;
  deleteCategory: (id: number) => Promise<void>;
}

export const useCategoryStore = create<CategoryState>((set, get) => ({
  categories: [],
  loading: false,
  error: null,

  fetchCategories: async () => {
    try {
      set({ loading: true, error: null })
      const response = await api.get<Category[]>('/categories')
      set({ categories: response.data, loading: false })
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  createCategory: async (data) => {
    try {
      set({ loading: true, error: null })
      await api.post('/categories', data)
      await get().fetchCategories()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  updateCategory: async (id, data) => {
    try {
      set({ loading: true, error: null })
      await api.put(`/categories/${id}`, data)
      await get().fetchCategories()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  deleteCategory: async (id) => {
    try {
      set({ loading: true, error: null })
      await api.delete(`/categories/${id}`)
      await get().fetchCategories()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  }
})) 