import {GroupDetailDto} from "./group";

export interface MessageDetailDto {
  id: number;
  group: GroupDetailDto;
  isRead: boolean;
  sentAt: string;
}

export interface MessageCreateDto {
  userId: number;
  groupId: number;
}
