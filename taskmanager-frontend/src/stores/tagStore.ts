import { create } from 'zustand'
import { Tag, CreateTagRequest, UpdateTagRequest } from '@/types/tag'
import { api } from '@/services/api'
import { AxiosError } from 'axios'

interface TagState {
  tags: Tag[];
  loading: boolean;
  error: string | null;
  fetchTags: () => Promise<void>;
  createTag: (data: CreateTagRequest) => Promise<void>;
  updateTag: (id: number, data: UpdateTagRequest) => Promise<void>;
  deleteTag: (id: number) => Promise<void>;
}

export const useTagStore = create<TagState>((set, get) => ({
  tags: [],
  loading: false,
  error: null,

  fetchTags: async () => {
    try {
      set({ loading: true, error: null })
      const response = await api.get<Tag[]>('/tags')
      set({ tags: response.data, loading: false })
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  createTag: async (data) => {
    try {
      set({ loading: true, error: null })
      await api.post('/tags', data)
      await get().fetchTags()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  updateTag: async (id, data) => {
    try {
      set({ loading: true, error: null })
      await api.put(`/tags/${id}`, data)
      await get().fetchTags()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  deleteTag: async (id) => {
    try {
      set({ loading: true, error: null })
      await api.delete(`/tags/${id}`)
      await get().fetchTags()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  }
})) 