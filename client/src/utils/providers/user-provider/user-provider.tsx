import {
  User,
  UserCredentials,
  UserRegisterData,
  UserUpdatedData,
} from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { AxiosResponse } from 'axios';
import { FC, ReactNode, useState } from 'react';

import { UserContext } from './user-context';

export const UserProvider: FC<{ children: ReactNode }> = ({ children }) => {
  const storageUser = localStorage.getItem('user');
  const [user, setUser] = useState<User | null>(
    storageUser ? JSON.parse(storageUser) : null,
  );

  const login = (userCredentials: UserCredentials): Promise<User> => {
    return new Promise((resolve, reject) => {
      apiClient
        .post('/auth/login', userCredentials)
        .then((response) => {
          const { user, token, tokenType } = response.data;
          apiClient.defaults.headers['Authorization'] = `${tokenType} ${token}`;
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

  const refreshUser = (): Promise<User> => {
    return new Promise((resolve, reject) => {
      apiClient
        .get(`/users/${user?.id}`)
        .then((response) => {
          const user = response.data;
          localStorage.setItem('user', JSON.stringify(user));
          setUser(user);
          resolve(user);
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

  const updateUser = (updatedUser: UserUpdatedData): Promise<AxiosResponse> => {
    return new Promise((resolve, reject) => {
      apiClient
        .put(`/users/${user?.id}`, updatedUser)
        .then((response) => {
          resolve(response.data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  const updateUserAvatar = (image: FormData): Promise<AxiosResponse> => {
    return new Promise((resolve, reject) => {
      apiClient
        .post(`/users/image/${user?.id}`, image)
        .then((response) => {
          resolve(response.data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  const deleteUserAvatar = (): Promise<AxiosResponse> => {
    return new Promise((resolve, reject) => {
      apiClient
        .delete(`/users/image/${user?.id}`)
        .then((response) => {
          resolve(response.data);
        })
        .catch((error) => {
          reject(error);
        });
    });
  };

  return (
    <UserContext.Provider
      value={{
        user,
        login,
        registerUser,
        refreshUser,
        logout,
        updateUser,
        updateUserAvatar,
        deleteUserAvatar,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
