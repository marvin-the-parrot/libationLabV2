
export interface UserListDto {
  id: number;
  name: string
}

export interface UserSearch {
  name?: string;
  limit?: number;
}

export interface UsernameDto {
  username: string
}
