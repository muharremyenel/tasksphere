export interface Tag {
  id: number;
  name: string;
  colorHex: string;
  usageCount: number;
}

export interface CreateTagRequest {
  name: string;
  colorHex: string;
}

export type UpdateTagRequest = CreateTagRequest; 