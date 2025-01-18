import {
  User,
  UserCredentials,
  UserRegisterData,
  UserUpdatedData,
} from '@/types';
import { AxiosResponse } from 'axios';
import { createContext } from 'react';

export const UserContext = createContext<
  | {
      user: User | null;
      login: (userCredentials: UserCredentials) => Promise<User>;
      // The naming 'registerUser' is necessary to avoid conflicts with the react-hook-form `register` function
      registerUser: (userRegisterData: UserRegisterData) => Promise<void>;
      logout: () => Promise<void>;
      refreshUser: () => Promise<User>;
      updateUser: (updatedUser: UserUpdatedData) => Promise<AxiosResponse>;
      updateUserAvatar: (image: FormData) => Promise<AxiosResponse>;
      deleteUserAvatar: () => Promise<AxiosResponse>;
    }
  | undefined
>(undefined);
