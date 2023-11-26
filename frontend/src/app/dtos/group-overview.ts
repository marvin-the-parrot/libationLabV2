import {UserListDto} from "./user";

export class GroupOverview {
  id: number;
  name: string;
  host: UserListDto;
  cocktails?: string[];
  members: UserListDto[];

  constructor(id: number, name: string, host: UserListDto, cocktails: string[], members: UserListDto[]) {
   this.id = id;
   this.name = name;
   this.host = host;
   this.cocktails = cocktails;
   this.members = members
  }
}
