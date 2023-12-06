
export interface UserListDto {
  id: number;
  name: string
}

export interface UserLocalStorageDto {
  id: number;
  username: string;
  email: string;
}

export interface UserListGroupDto {
  id: number;
  name: string;
  isHost: boolean;
}
