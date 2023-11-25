export interface MessageDetailDto {
  id: number;
  groupName: string;
  isRead: boolean;
  sentAt: string;
}

export interface MessageCreateDto {
  userId: number;
  groupId: number;
}
