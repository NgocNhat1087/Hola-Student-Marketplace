import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Alert } from '../components/Alert';
import { authApi } from '../services/api';

function validPassword(value: string) { return value.length >= 8 && /[A-Za-z]/.test(value) && /\d/.test(value); }

export function ChangePasswordPage() {
  const navigate = useNavigate();
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [message, setMessage] = useState(''); const [error, setError] = useState(''); const [busy, setBusy] = useState(false);
  const submit = async (event: FormEvent) => { event.preventDefault(); setError(''); setMessage('');
    if (!validPassword(newPassword)) { setError('Mật khẩu mới phải có ít nhất 8 ký tự, gồm chữ cái và chữ số.'); return; }
    if (newPassword !== confirmPassword) { setError('Mật khẩu mới và xác nhận mật khẩu không trùng khớp.'); return; }
    setBusy(true); try { const result = await authApi.changePassword(currentPassword, newPassword, confirmPassword); setMessage(result.message); setTimeout(() => navigate('/profile'), 700); } catch (e) { setError(e instanceof Error ? e.message : 'Không thể đổi mật khẩu.'); } finally { setBusy(false); }
  };
  return <div className="page"><div className="container narrow"><div className="edit-heading"><p className="eyebrow">Account security</p><h1>Đổi <em>mật khẩu.</em></h1><p>Thay mật khẩu hiện tại bằng một mật khẩu mới an toàn hơn.</p></div>{error && <Alert>{error}</Alert>}{message && <Alert tone="success">{message}</Alert>}<section className="edit-card"><form className="auth-form" onSubmit={submit}><label className="field"><span>Mật khẩu hiện tại</span><input type="password" value={currentPassword} onChange={e => setCurrentPassword(e.target.value)} required /></label><label className="field"><span>Mật khẩu mới</span><input type="password" value={newPassword} onChange={e => setNewPassword(e.target.value)} minLength={8} required /><small>Ít nhất 8 ký tự, gồm chữ cái và chữ số.</small></label><label className="field"><span>Xác nhận mật khẩu mới</span><input type="password" value={confirmPassword} onChange={e => setConfirmPassword(e.target.value)} minLength={8} required /></label><div className="edit-actions"><Link className="button button-ghost" to="/profile">Hủy</Link><button className="button" disabled={busy}>{busy ? 'Đang lưu...' : 'Đổi mật khẩu'} <span>→</span></button></div></form></section></div></div>;
}
