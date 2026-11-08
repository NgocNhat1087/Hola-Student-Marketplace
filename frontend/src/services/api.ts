import type { ApiError, Profile, User } from '../types';

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
const TOKEN_KEY = 'hsm_token';

export class ApiRequestError extends Error {
  constructor(message: string, public readonly code = 'REQUEST_FAILED', public readonly status = 0) {
    super(message);
    this.name = 'ApiRequestError';
  }
}

export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY);
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const headers = new Headers(options.headers);
  if (!(options.body instanceof FormData)) headers.set('Content-Type', 'application/json');
  const token = getToken();
  if (token) headers.set('Authorization', `Bearer ${token}`);

  const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });
  const text = await response.text();
  let data: T | ApiError | null = null;
  if (text) {
    try { data = JSON.parse(text) as T; } catch { data = null; }
  }
  if (!response.ok) {
    const error = data as ApiError | null;
    throw new ApiRequestError(error?.message || `Request failed (${response.status})`, error?.error, response.status);
  }
  return data as T;
}

export const authApi = {
  register: (email: string, password: string) => request<{ verificationRequired: boolean }>('/api/auth/register', { method: 'POST', body: JSON.stringify({ email, password }) }),
  verifyEmail: (email: string, otp: string) => request<void>('/api/auth/verify-email', { method: 'POST', body: JSON.stringify({ email, otp }) }),
  resendOtp: (email: string) => request<void>('/api/auth/resend-otp', { method: 'POST', body: JSON.stringify({ email }) }),
  login: (email: string, password: string) => request<{ user: User; token: string }>('/api/auth/login', { method: 'POST', body: JSON.stringify({ email, password }) }),
  logout: () => request<void>('/api/auth/logout', { method: 'POST' }),
};

export const profileApi = {
  getMine: () => request<Profile>('/api/profile/me'),
  getById: (id: string) => request<Profile>(`/api/profile/${id}`),
  update: (nickname: string, bio: string) => request<Profile>('/api/profile/me', { method: 'PUT', body: JSON.stringify({ nickname, bio }) }),
  uploadImage: (kind: 'avatar' | 'cover', file: File) => {
    const form = new FormData();
    form.append('kind', kind);
    form.append('file', file);
    return request<Profile>('/api/profile/me/image', { method: 'POST', body: form });
  },
};
