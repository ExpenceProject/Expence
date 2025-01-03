import axios from 'axios';

const authToken = localStorage.getItem('authToken');
const authTokenType = localStorage.getItem('authTokenType');

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_REACT_API_URL,
  headers: {
    Authorization: `${authTokenType} ${authToken}`,
  },
});
