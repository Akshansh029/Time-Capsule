export type CapsuleStatus = "LOCKED" | "UNLOCKED";

export interface CapsuleDto {
  id: string;
  slug: string;
  title: string;
  status: CapsuleStatus;
  unlockDate: string; // ISO date string
  isPrivate: boolean;
  createdAt: string; // ISO date string
  ownerId: string;
  ownerName: string;
}

export interface PagedResponse<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface CreateCapsuleRequest {
  title: string;
  description: string;
  unlockDate: string; // ISO date string
  isPrivate: boolean;
}
