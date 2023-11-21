export class Group {
  id: number;
  name: string;
  host: string;
  cocktails: string[];
  numMembers: number;

  constructor(id: number, name: string, host: string, cocktails: string[], numMembers: number) {
   this.id = id;
   this.name = name;
   this.host = host;
   this.cocktails = cocktails;
   this.numMembers = numMembers;
  }
}
