import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import type { User } from '../types';
import { authApi, clearToken, getToken, profileApi, setToken } from '../services/api';

type AuthContextValue = {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  completeOAuthLogin: (token: string) => Promise<void>;
  logout: () => Promise<void>;
};

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(Boolean(getToken()));

  useEffect(() => {
    if (!getToken()) { setLoading(false); return; }
    fetch(`${import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'}/api/profile/me`, { headers: { Authorization: `Bearer ${getToken()}` } })
      .then((response) => { if (!response.ok) throw new Error('Session expired'); return response.json(); })
      .then((profile) => setUser({ id: profile.userId, email: '', role: 'USER', verified: true }))
      .catch(() => { clearToken(); setUser(null); })
      .finally(() => setLoading(false));
  }, []);

  const value = useMemo<AuthContextValue>(() => ({
    user,
    loading,
    async login(email, password) {
      const result = await authApi.login(email, password);
      setToken(result.token);
      setUser(result.user);
    },
    async completeOAuthLogin(token) {
      setToken(token);
      try {
        const profile = await profileApi.getMine();
        setUser({ id: profile.userId, email: '', role: 'USER', verified: true });
      } catch (error) {
        clearToken();
        setUser(null);
        throw error;
      }
    },
    async logout() {
      try { await authApi.logout(); } finally { clearToken(); setUser(null); }
    },
  }), [loading, user]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used inside AuthProvider');
  return context;
}
