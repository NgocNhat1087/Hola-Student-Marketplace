import { createRoot } from 'react-dom/client';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from './components/AppShell';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AuthProvider } from './context/AuthContext';
import { AuthLayout } from './layouts/AuthLayout';
import { AuthPage } from './pages/AuthPage';
import { HomePage } from './pages/HomePage';
import { EditProfilePage, ProfilePage } from './pages/ProfilePages';
import { OAuthCallbackPage } from './pages/OAuthCallbackPage';
import './styles.css';

createRoot(document.getElementById('root')!).render(<AuthProvider><BrowserRouter><Routes>
  <Route element={<AppShell />}>
    <Route index element={<HomePage />} />
    <Route path="profile/:id" element={<ProfilePage />} />
    <Route element={<ProtectedRoute />}><Route path="profile" element={<ProfilePage />} /><Route path="profile/edit" element={<EditProfilePage />} /></Route>
  </Route>
  <Route element={<AuthLayout />}><Route path="login" element={<AuthPage mode="login" />} /><Route path="register" element={<AuthPage mode="register" />} /><Route path="verify-otp" element={<AuthPage mode="verify" />} /><Route path="verify-email" element={<Navigate to="/verify-otp" replace />} /></Route>
  <Route path="oauth2/callback" element={<OAuthCallbackPage />} />
  <Route path="*" element={<Navigate to="/" replace />} />
</Routes></BrowserRouter></AuthProvider>);
