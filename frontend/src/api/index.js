import axios from 'axios';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('wallet_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('wallet_token');
      localStorage.removeItem('wallet_user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
};

export const walletAPI = {
  createWallet: (currency = 'USD') => api.post(`/wallet/create?currency=${currency}`),
  getWallet: (walletId) => api.get(`/wallet/${walletId}`),
  getMyWallets: () => api.get('/wallet/my-wallets'),
  getBalance: (walletId) => api.get(`/wallet/${walletId}/balance`),
};

export const paymentAPI = {
  addMoney: (data) => api.post('/payment/add-money', data),
  transfer: (data) => api.post('/payment/transfer', data),
  getHistory: (walletId, page = 0, size = 10) => 
    api.get(`/payment/history/${walletId}?page=${page}&size=${size}`),
};

export default api;