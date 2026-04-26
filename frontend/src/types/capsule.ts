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

export type ContentType = "TEXT" | "IMAGE" | "FILE";
export type MemberRole = "ADMIN" | "CONTRIBUTOR" | "VIEWER";

export interface AddContentRequestDto {
  type: ContentType;
  body?: string;
  fileUrl?: string;
}

export interface AddMemberRequestDto {
  capsuleId?: string; // Optional during creation
  userEmail: string;
  role: MemberRole;
}

export interface CreateCapsuleRequest {
  title: string;
  description: string;
  unlockDate: string; // ISO date string
  isPrivate: boolean;
  contents: AddContentRequestDto[];
  members: AddMemberRequestDto[];
}

export interface LockedCapsuleDto extends CapsuleDto {
  description: string;
  daysUntilUnlock: number;
}

export interface CapsuleContentDto {
  id: string;
  type: ContentType;
  body?: string;
  fileUrl?: string;
  createdAt: string;
}

export interface CapsuleMemberDto {
  id: string;
  userEmail: string;
  userName: string;
  role: MemberRole;
  joinedAt: string;
}

export interface UnlockedCapsuleDto extends LockedCapsuleDto {
  contents: CapsuleContentDto[];
  members: CapsuleMemberDto[];
}
