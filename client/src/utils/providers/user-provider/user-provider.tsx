import { User, UserCredentials, UserRegisterData } from '@/types';
import { apiClient } from '@/utils/http-clients/api-http-client';
import { ReactNode, useState } from 'react';

import { UserContext } from './user-context';

export const UserProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [user, setUser] = useState<User | null>(null);

  //  TODO: Implement useEffect to check if user is logged in by sending a request to /auth/me based on the token stored in localStorage

  const login = (userCredentials: UserCredentials): Promise<void> => {
    return new Promise((resolve, reject) => {
      apiClient
        .post('/auth/login', userCredentials)
        .then((response) => {
          setUser(response.data.user);
          localStorage.setItem('authToken', response.data.token);
          localStorage.setItem('authTokenType', response.data.tokenType);
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
      setUser(null);
    });
  };

  const updateUser = (userData: User) => {
    // TODO: Send a request to update the user's data
    setUser(userData);
  };

  return (
    <UserContext.Provider
      value={{ user, login, registerUser, logout, updateUser }}
    >
      {children}
    </UserContext.Provider>
  );
};
