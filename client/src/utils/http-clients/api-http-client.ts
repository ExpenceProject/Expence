import axios from 'axios';

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_REACT_API_URL,
  headers: {
    'Content-Type': 'application/json',
    Authorization: `${localStorage.getItem('authTokenType')} ${localStorage.getItem('authToken')}`,
  },
});
