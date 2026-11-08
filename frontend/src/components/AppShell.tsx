import { useState } from 'react';
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Logo } from './Logo';

export function AppShell() {
  const { user, logout } = useAuth(); const navigate = useNavigate(); const [logoutError, setLogoutError] = useState('');
const signOut = async () => {
    if (!window.confirm('Log out of your Hola account?')) return;
    setLogoutError('');
    try {
      await logout();
      navigate('/login?oauthError=' + encodeURIComponent('Google sign-in did not return a session.'), { replace: true });
    } catch {
      setLogoutError('We could not contact the server. Please try again.');
    }
  };
  return <div className="app-shell"><header className="site-header"><div className="container header-inner"><Logo /><nav className="desktop-nav"><NavLink to="/">Home</NavLink>{user && <NavLink to="/profile">My profile</NavLink>}</nav><div className="header-actions">{user ? <><Link className="avatar-chip" to="/profile"><span className="mini-avatar">{user.email?.[0]?.toUpperCase() || 'S'}</span><span>My profile</span></Link><Link className="text-button" to="/change-password">Đổi mật khẩu</Link><button className="icon-button" onClick={signOut} aria-label="Log out">↗</button></> : <><Link className="text-button" to="/login">Log in</Link><Link className="button button-small" to="/register">Join Hola</Link></>}</div></div></header>{logoutError && <div className="container shell-alert"><div className="alert alert-error">{logoutError}</div></div>}<main><Outlet /></main><footer className="site-footer"><div className="container footer-grid"><div><Logo /><p className="footer-copy">A trusted account and profile foundation for student-to-student life.</p></div><div><strong>Navigate</strong><Link to="/">Home</Link>{user && <Link to="/profile">My profile</Link>}</div><div><strong>Account</strong>{user ? <><Link to="/change-password">Đổi mật khẩu</Link><button className="footer-link-button" onClick={signOut}>Log out</button></> : <><Link to="/login">Log in</Link><Link to="/register">Create account</Link></>}</div></div><div className="container footer-bottom">© 2026 Hola Student Marketplace <span>Built for student life.</span></div></footer></div>;
}
