import {UserListDto} from "./user";

export interface IngredientGroupDto {
  name: string;
  users: UserListDto[];
}

export interface IngredientListDto {
  id: number;
  name: string;
}


