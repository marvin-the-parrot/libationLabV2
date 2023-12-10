import {UserListDto, UserListGroupDto} from "./user";

export class GroupOverview {
  id: number;
  name: string;
  cocktails?: String[];
  host?: UserListDto;
  members: UserListGroupDto[];

  constructor(id: number, name: string, cocktails: string[], members: UserListGroupDto[]) {
   this.id = id;
   this.name = name;
   this.cocktails = cocktails;
   this.members = members
  }
}
