import { beforeEach, describe, expect, it, vi } from 'vitest';
import { authApi, clearToken, getToken, setToken } from './api';

describe('frontend API client', () => {
  const values = new Map<string, string>();

  beforeEach(() => {
    values.clear();
    vi.stubGlobal('localStorage', {
      getItem: (key: string) => values.get(key) ?? null,
      setItem: (key: string, value: string) => values.set(key, value),
      removeItem: (key: string) => values.delete(key),
    });
  });

  it('stores and clears the session token', () => {
    setToken('test-token');
    expect(getToken()).toBe('test-token');
    clearToken();
    expect(getToken()).toBeNull();
  });

  it('posts registration data and returns the verification requirement', async () => {
    const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify({ verificationRequired: true }), { status: 201 }));
    vi.stubGlobal('fetch', fetchMock);

    await expect(authApi.register('student@university.edu', 'password1')).resolves.toEqual({ verificationRequired: true });
    expect(fetchMock).toHaveBeenCalledWith(expect.stringContaining('/api/auth/register'), expect.objectContaining({ method: 'POST' }));
  });
});
