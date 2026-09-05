import { type ReactNode } from 'react';
import { BrowserRouter, Link, Navigate, Outlet, Route, Routes, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './lib/auth';
import { TopBar } from './components/TopBar';
import { FeedPage } from './pages/Feed';
import { TopicPage, TopicsPage } from './pages/Topics';
import { PostPage } from './pages/PostPage';
import { PostEditorPage } from './pages/PostEditor';
import { LoginPage, RegisterPage } from './pages/Auth';
import { SettingsPage } from './pages/Settings';
import { AdminUsersPage } from './pages/AdminUsers';
import { Empty } from './components/ui';

function Layout({ wide }: { wide?: boolean }) {
  return (
    <>
      <TopBar />
      <main className={`page${wide ? ' wide' : ''}`}>
        <Outlet />
      </main>
      <footer className="footer">
        © {new Date().getFullYear()} Overheard Club · <Link to="/">Feed</Link> · <Link to="/topics">Topics</Link>
      </footer>
    </>
  );
}

function RequireAuth({ admin, children }: { admin?: boolean; children: ReactNode }) {
  const { user, ready } = useAuth();
  const location = useLocation();
  if (!ready) return null;
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname + location.search }} />;
  if (admin && user.role !== 'ADMIN') return <Navigate to="/" replace />;
  return <>{children}</>;
}

function GuestOnly({ children }: { children: ReactNode }) {
  const { user, ready } = useAuth();
  if (!ready) return null;
  if (user) return <Navigate to="/" replace />;
  return <>{children}</>;
}

function NotFound() {
  return (
    <Empty moose>
      <span className="eyebrow">404</span>
      <h2 style={{ marginBottom: '0.5rem' }}>This page isn't here</h2>
      <p>It may have been deleted, or the link is wrong.</p>
      <Link className="btn" to="/">
        Back to the feed
      </Link>
    </Empty>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route element={<Layout />}>
            <Route index element={<FeedPage />} />
            <Route path="topics" element={<TopicsPage />} />
            <Route path="topics/:id" element={<TopicPage />} />
            <Route path="posts/:id" element={<PostPage />} />
            <Route
              path="posts/new"
              element={
                <RequireAuth>
                  <PostEditorPage />
                </RequireAuth>
              }
            />
            <Route
              path="posts/:id/edit"
              element={
                <RequireAuth>
                  <PostEditorPage />
                </RequireAuth>
              }
            />
            <Route
              path="settings"
              element={
                <RequireAuth>
                  <SettingsPage />
                </RequireAuth>
              }
            />
            <Route
              path="login"
              element={
                <GuestOnly>
                  <LoginPage />
                </GuestOnly>
              }
            />
            <Route
              path="register"
              element={
                <GuestOnly>
                  <RegisterPage />
                </GuestOnly>
              }
            />
            <Route path="*" element={<NotFound />} />
          </Route>
          <Route element={<Layout wide />}>
            <Route
              path="admin/users"
              element={
                <RequireAuth admin>
                  <AdminUsersPage />
                </RequireAuth>
              }
            />
          </Route>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
