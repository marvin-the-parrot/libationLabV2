import {UserListDto} from "./user";

export interface IngredientDto {
  name: string;
  users: UserListDto[];
}
