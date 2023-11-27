import {GroupDetailDto} from "./group";

export interface MessageDetailDto {
  id: number;
  group: GroupDetailDto;
  isRead: boolean;
  sentAt: string;
}

export class MessageCreate {
  constructor(
    public userId: number,
    public groupId: number
  ) {}
}
