export interface Team {
  id: number;
  name: string;
  description?: string;
  createdAt: string;
  members: Array<{
    id: number;
    name: string;
    email: string;
  }>;
}

export interface CreateTeamRequest {
  name: string;
  description?: string;
}

export type UpdateTeamRequest = CreateTeamRequest; 