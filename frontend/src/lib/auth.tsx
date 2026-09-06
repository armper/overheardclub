import { createContext, useCallback, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { api, getToken, setToken, type User } from './api';

interface AuthState {
  user: User | null;
  /** True until the stored token has been checked once. */
  ready: boolean;
  signIn: (token: string, user: User) => void;
  signOut: () => void;
  refresh: () => Promise<void>;
}

const AuthContext = createContext<AuthState | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [ready, setReady] = useState(false);

  const refresh = useCallback(async () => {
    if (!getToken()) {
      setUser(null);
      return;
    }
    try {
      setUser(await api.me());
    } catch {
      setToken(null);
      setUser(null);
    }
  }, []);

  useEffect(() => {
    refresh().finally(() => setReady(true));
  }, [refresh]);

  const value = useMemo<AuthState>(
    () => ({
      user,
      ready,
      signIn: (token, u) => {
        setToken(token);
        setUser(u);
      },
      signOut: () => {
        setToken(null);
        setUser(null);
      },
      refresh,
    }),
    [user, ready, refresh],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthState {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
}
