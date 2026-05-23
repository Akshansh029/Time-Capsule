export interface ActiveUserResponse {
  id: string;
  name: string;
  email: string;
  createdAt: string;
  noOfOwnedCapsules: number;
  noOfOwnedContents: number;
  noOfMemberOfCapsules: number;
}

export interface UpdateUsernameRequestDto {
  name: string;
}
