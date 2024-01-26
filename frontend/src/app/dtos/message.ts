import {GroupDetailDto} from "./group";

export interface MessageDetailDto {
  id: number;
  text: string;
  group: GroupDetailDto;
  isRead: boolean;
  sentAt: string;
}

export interface MessageCountDto {
  count: number;
}

export class MessageCreate {
  constructor(
    public userId: number,
    public groupId: number
  ) {}
}

export interface MessageSetReadDto {
  id: number;
  isRead: boolean;
}
