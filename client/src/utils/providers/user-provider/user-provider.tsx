import { User, UserCredentials, UserRegisterData } from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { ReactNode, useState } from 'react';

import { UserContext } from './user-context';

export const UserProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const storageUser = localStorage.getItem('user');
  const [user, setUser] = useState<User | null>(
    storageUser ? JSON.parse(storageUser) : null,
  );

  const login = (userCredentials: UserCredentials): Promise<void> => {
    return new Promise((resolve, reject) => {
      apiClient
        .post('/auth/login', userCredentials)
        .then((response) => {
          const { user, token, tokenType } = response.data;
          localStorage.setItem('authToken', token);
          localStorage.setItem('authTokenType', tokenType);
          localStorage.setItem('user', JSON.stringify(user));
          setUser(user);
          resolve(response.data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  const registerUser = (userRegisterData: UserRegisterData): Promise<void> => {
    return new Promise((resolve, reject) => {
      apiClient
        .post('/auth/register', userRegisterData)
        .then((response) => {
          resolve(response.data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  const logout = (): Promise<void> => {
    return new Promise(() => {
      localStorage.removeItem('authToken');
      localStorage.removeItem('authTokenType');
      localStorage.removeItem('user');
      setUser(null);
    });
  };

  const updateUser = () => {
    // TODO: Send a request to update the user's data
  };

  return (
    <UserContext.Provider
      value={{ user, login, registerUser, logout, updateUser }}
    >
      {children}
    </UserContext.Provider>
  );
};
