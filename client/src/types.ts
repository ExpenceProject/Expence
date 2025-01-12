type DeepReadonly<T> = {
  readonly [P in keyof T]: T[P] extends object ? DeepReadonly<T[P]> : T[P];
};

export type User = DeepReadonly<{
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  image: {
    key: string;
  };
  roles: {
    name: string;
  }[];
  version: number;
  createdAt: string;
  updatedAt: string;
}>;

export type UserCredentials = DeepReadonly<{
  email: string;
  password: string;
}>;

export type UserRegisterData = DeepReadonly<{
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
}>;

export type UserUpdatedData = DeepReadonly<{
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}>;

export type GroupMember = DeepReadonly<{
  id: string;
  user: string;
  nickname: string;
  groupRole: {
    name: string;
  };
  version: number;
  createdAt: string;
  updatedAt: string;
}>;

export type GroupMemberWithUser = GroupMember &
  DeepReadonly<{ userInfo?: User }>;

export type Group = DeepReadonly<{
  id: string;
  image: {
    key: string;
  };
  name: string;
  settledDown: boolean;
  version: number;
  createdAt: string;
  updatedAt: string;
}>;

export type GroupWithMembers = DeepReadonly<{
  id: string;
  image: {
    key: string;
  };
  name: string;
  settledDown: boolean;
  version: number;
  createdAt: string;
  updatedAt: string;
  members: GroupMember[];
}>;

export enum InvitationStatus {
  SENT = 'SENT',
  ACCEPTED = 'ACCEPTED',
  DECLINED = 'DECLINED',
  CANCELED = 'CANCELED',
}

export type Invitation = DeepReadonly<{
  id: string;
  inviteeId: string;
  inviterId: string;
  groupId: string;
  status: InvitationStatus;
  version: number;
  createdAt: string;
  updatedAt: string;
}>;

export type InvitationWithInviter = Invitation &
  DeepReadonly<{ inviter?: User }>;

export type InvitationWithInviterAndGroup = InvitationWithInviter &
  DeepReadonly<{ group?: Group }>;

export type Bill = DeepReadonly<{
  id: string;
  name: string;
  expenses: Expense[];
  totalAmount: number;
  lender: GroupMember;
  groupId: number;
  version: number;
  createdAt: string;
  updatedAt: string;
}>;

export type Payment = DeepReadonly<{
  id: string;
  receiver: GroupMember;
  sender: GroupMember;
  amount: number;
  groupId: string;
  version: number;
  createdAt: string;
  updatedAt: string;
}>;

export type Expense = DeepReadonly<{
  borrower: GroupMember;
  amount: number;
}>;
