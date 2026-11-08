import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { setToken } from '../services/api';

export function OAuthCallbackPage() {
  const navigate = useNavigate();
  useEffect(() => {
    const token = new URLSearchParams(window.location.hash.replace(/^#/, '')).get('token');
    if (token) {
      setToken(token);
      window.history.replaceState(null, '', '/oauth2/callback');
      navigate('/profile', { replace: true });
    } else navigate('/login?oauthError=Google sign-in did not return a session.', { replace: true });
  }, [navigate]);
  return <div className="page-loader"><span className="spinner" />Completing Google sign-in...</div>;
}
