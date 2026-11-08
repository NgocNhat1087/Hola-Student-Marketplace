import { Outlet } from 'react-router-dom';
import { Logo } from '../components/Logo';
export function AuthLayout() { return <div className="auth-page"><aside className="auth-side"><Logo /><div className="auth-quote"><span className="quote-mark">“</span><h2>Student life is better when we make room for each other.</h2><p>Buy less new. Share more. Find your people.</p></div><div className="auth-side-footer">Hola Student Marketplace <span>·</span> 2026</div></aside><section className="auth-panel"><div className="mobile-logo"><Logo /></div><Outlet /></section></div>; }
