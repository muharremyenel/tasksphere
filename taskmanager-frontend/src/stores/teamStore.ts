import { create } from 'zustand'
import { Team, CreateTeamRequest, UpdateTeamRequest } from '@/types/team'
import { api } from '@/services/api'
import { AxiosError } from 'axios'

interface TeamState {
  teams: Team[];
  loading: boolean;
  error: string | null;
  fetchTeams: () => Promise<void>;
  createTeam: (data: CreateTeamRequest) => Promise<void>;
  updateTeam: (id: number, data: UpdateTeamRequest) => Promise<void>;
  deleteTeam: (id: number) => Promise<void>;
  addMember: (teamId: number, userId: number) => Promise<void>;
  removeMember: (teamId: number, userId: number) => Promise<void>;
}

export const useTeamStore = create<TeamState>((set, get) => ({
  teams: [],
  loading: false,
  error: null,

  fetchTeams: async () => {
    try {
      set({ loading: true, error: null })
      const response = await api.get<Team[]>('/teams')
      set({ teams: response.data, loading: false })
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  createTeam: async (data) => {
    try {
      set({ loading: true, error: null })
      await api.post('/teams', data)
      await get().fetchTeams()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  updateTeam: async (id, data) => {
    try {
      set({ loading: true, error: null })
      await api.put(`/teams/${id}`, data)
      await get().fetchTeams()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  deleteTeam: async (id) => {
    try {
      set({ loading: true, error: null })
      await api.delete(`/teams/${id}`)
      await get().fetchTeams()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  addMember: async (teamId, userId) => {
    try {
      set({ loading: true, error: null })
      await api.put(`/teams/${teamId}/members/${userId}`)
      await get().fetchTeams()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  },

  removeMember: async (teamId, userId) => {
    try {
      set({ loading: true, error: null })
      await api.delete(`/teams/${teamId}/members/${userId}`)
      await get().fetchTeams()
    } catch (error) {
      const err = error as AxiosError
      set({ error: err.message, loading: false })
    }
  }
})) 