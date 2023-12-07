import {UserListDto} from "./user";

export interface IngredientGroupDto {
  name: string;
  users: UserListDto[];
}
