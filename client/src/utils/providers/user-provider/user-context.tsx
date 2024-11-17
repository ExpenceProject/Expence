import { User, UserCredentials, UserRegisterData } from '@/types';
import { createContext } from 'react';

export const UserContext = createContext<
  | {
      user: User | null;
      login: (userCredentials: UserCredentials) => Promise<void>;
      // The naming 'registerUser' is necessary to avoid conflicts with the react-hook-form `register` function
      registerUser: (userRegisterData: UserRegisterData) => Promise<void>;
      logout: () => Promise<void>;
      updateUser: (userData: User) => void;
    }
  | undefined
>(undefined);
