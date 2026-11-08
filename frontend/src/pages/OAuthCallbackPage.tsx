import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function OAuthCallbackPage() {
  const navigate = useNavigate();
  const { completeOAuthLogin } = useAuth();
  useEffect(() => {
    const queryToken = new URLSearchParams(window.location.search).get('token');
    const hashToken = new URLSearchParams(window.location.hash.replace(/^#/, '')).get('token');
    const token = queryToken || hashToken;
    if (token) {
      completeOAuthLogin(token)
        .then(() => {
          window.history.replaceState(null, '', '/oauth2/callback');
          navigate('/profile', { replace: true });
        })
        .catch((error: unknown) => {
          const message = error instanceof Error ? error.message : 'Google session could not be created.';
          navigate(`/login?oauthError=${encodeURIComponent(message)}`, { replace: true });
        });
    } else navigate('/login?oauthError=Google sign-in did not return a session.', { replace: true });
  }, [completeOAuthLogin, navigate]);
  return <div className="page-loader"><span className="spinner" />Completing Google sign-in...</div>;
}
