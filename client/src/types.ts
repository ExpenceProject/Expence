type DeepReadonly<T> = {
  readonly [P in keyof T]: T[P] extends object ? DeepReadonly<T[P]> : T[P];
};

export type User = DeepReadonly<{
  id: number;
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
