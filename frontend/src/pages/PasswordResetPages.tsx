import { useState, type FormEvent } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { Alert } from '../components/Alert';
import { authApi } from '../services/api';

function Frame({ title, subtitle, children }: { title: string; subtitle: string; children: React.ReactNode }) { return <div className="auth-form-wrap"><p className="eyebrow">Welcome to Hola</p><h1>{title}</h1><p className="auth-subtitle">{subtitle}</p>{children}</div>; }
function validPassword(value: string) { return value.length >= 8 && /[A-Za-z]/.test(value) && /\d/.test(value); }

export function ForgotPasswordPage() {
  const [email, setEmail] = useState(''); const [error, setError] = useState(''); const [sent, setSent] = useState(false); const [busy, setBusy] = useState(false);
  const submit = async (event: FormEvent) => { event.preventDefault(); setError(''); setBusy(true); try { await authApi.forgotPassword(email.trim().toLowerCase()); setSent(true); } catch (e) { setError(e instanceof Error ? e.message : 'Unable to request a reset link.'); } finally { setBusy(false); } };
  if (sent) return <Frame title="Check your inbox." subtitle="If an account exists for that email, we sent a password reset link."><Alert tone="success">The link is valid for 15 minutes and can only be used once.</Alert><p className="auth-switch"><Link to="/login">Back to log in</Link></p></Frame>;
  return <Frame title="Forgot your password?" subtitle="Enter your registered email and we’ll send you a secure reset link.">{error && <Alert>{error}</Alert>}<form className="auth-form" onSubmit={submit}><label className="field"><span>Email address</span><input type="email" value={email} onChange={(e) => setEmail(e.target.value)} placeholder="you@university.edu" required /></label><button className="button button-full" disabled={busy}>{busy ? 'Sending...' : 'Send reset link'} <span>→</span></button></form><p className="auth-switch"><Link to="/login">Back to log in</Link></p></Frame>;
}

export function ResetPasswordPage() {
  const [params] = useSearchParams(); const navigate = useNavigate(); const token = params.get('token') || ''; const [password, setPassword] = useState(''); const [confirm, setConfirm] = useState(''); const [error, setError] = useState(''); const [busy, setBusy] = useState(false);
  const submit = async (event: FormEvent) => { event.preventDefault(); setError(''); if (!token) { setError('This password reset link is invalid or expired.'); return; } if (!validPassword(password)) { setError('Password must be at least 8 characters and include both letters and numbers.'); return; } if (password !== confirm) { setError('Passwords do not match.'); return; } setBusy(true); try { await authApi.resetPassword(token, password); navigate('/login?reset=success', { replace: true }); } catch (e) { setError(e instanceof Error ? e.message : 'This password reset link is invalid or expired.'); } finally { setBusy(false); } };
  return <Frame title="Set a new password." subtitle="Choose a password with at least 8 characters, including letters and numbers.">{error && <Alert>{error}</Alert>}<form className="auth-form" onSubmit={submit}><label className="field"><span>New password</span><input type="password" value={password} onChange={(e) => setPassword(e.target.value)} minLength={8} required /></label><label className="field"><span>Confirm password</span><input type="password" value={confirm} onChange={(e) => setConfirm(e.target.value)} minLength={8} required /></label><button className="button button-full" disabled={busy}>{busy ? 'Saving...' : 'Set new password'} <span>→</span></button></form><p className="auth-switch"><Link to="/login">Back to log in</Link></p></Frame>;
}
