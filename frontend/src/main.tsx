import { createRoot } from 'react-dom/client';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AppShell } from './components/AppShell';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AuthProvider } from './context/AuthContext';
import { AuthLayout } from './layouts/AuthLayout';
import { AuthPage } from './pages/AuthPage';
import { HomePage } from './pages/HomePage';
import { MarketplacePage } from './pages/MarketplacePage';
import { CommentsPage, FavoritesPage, FollowingPage, NotificationsPage, ReportsPage } from './pages/UseCasePages';
import { EditProfilePage, ProfilePage } from './pages/ProfilePages';
import { OAuthCallbackPage } from './pages/OAuthCallbackPage';
import './styles.css';

createRoot(document.getElementById('root')!).render(<AuthProvider><BrowserRouter><Routes>
  <Route element={<AppShell />}>
    <Route index element={<HomePage />} />
    <Route path="profile/:id" element={<ProfilePage />} />
    <Route path="marketplace" element={<MarketplacePage />} /><Route path="marketplace/comments" element={<CommentsPage />} /><Route path="marketplace/favorites" element={<FavoritesPage />} /><Route path="marketplace/following" element={<FollowingPage />} /><Route path="marketplace/notifications" element={<NotificationsPage />} /><Route path="marketplace/reports" element={<ReportsPage />} />
    <Route element={<ProtectedRoute />}><Route path="profile" element={<ProfilePage />} /><Route path="profile/edit" element={<EditProfilePage />} /></Route>
  </Route>
  <Route element={<AuthLayout />}><Route path="login" element={<AuthPage mode="login" />} /><Route path="register" element={<AuthPage mode="register" />} /><Route path="verify-otp" element={<AuthPage mode="verify" />} /><Route path="verify-email" element={<Navigate to="/verify-otp" replace />} /></Route>
  <Route path="oauth2/callback" element={<OAuthCallbackPage />} />
  <Route path="*" element={<Navigate to="/" replace />} />
</Routes></BrowserRouter></AuthProvider>);
