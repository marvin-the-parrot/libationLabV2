
export interface UserListDto {
  id: number;
  name: string
}

export interface UserSearch {
  name?: string;
  limit?: number;
}

export interface UserListGroupDto {
  id: number;
  name: string;
  isHost: boolean;
}
